package controllers.rest

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import modules.RestEnvironment
import models.User
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{ Messages, MessagesApi }

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{ Action, BodyParsers }
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class CredentialsAuthRestController @Inject() (val messagesApi: MessagesApi,
    val e: RestEnvironment,
    userService: UserService,
    credentialsProvider: CredentialsProvider) extends Silhouette[User, JWTAuthenticator] {
  val env: Environment[User, JWTAuthenticator] = e.env

  implicit val credentialReads: Reads[Credentials] = (
    (JsPath \ "identifier").read[String](minLength[String](12)) and
    (JsPath \ "password").read[String]
  )(Credentials.apply _)

  implicit val credentialWrites: Writes[Credentials] = (
    (JsPath \ "identifier").write[String] and
    (JsPath \ "password").write[String]
  )(unlift(Credentials.unapply))

  implicit val credentialsFormat: Format[Credentials] = Format(credentialReads, credentialWrites)

  /**
   * Authenticates a user against the credentials provider.
   *
   * receive json like this:
   * {
   *    "identifier": "...",
   *    "password": "..."
   * }
   *
   * e.g
   * curl -X POST http://localhost:9000/auth/signin/credentials -H 'Content-Type: application/json' -d '{"identifier": "some@gmail.com", "password": "password"}' -vvvv
   *
   * @return The result to display.
   */
  def authenticate = Action.async(BodyParsers.parse.json) { implicit request =>
    val credentialsResult = request.body.validate[Credentials]
    credentialsResult.fold(
      errors => {
        Future { BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))) }
      },
      credentials => {
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) => env.authenticatorService.create(user.loginInfo).flatMap { authenticator =>
              env.eventBus.publish(LoginEvent(user, request, request2Messages))
              env.authenticatorService.init(authenticator).flatMap { token =>
                env.authenticatorService.embed(token, Ok(Json.toJson("{'token':'" + token + "''}")))
              }
            }
            case None =>
              Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recoverWith(exceptionHandler)
      }
    )
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) =>
        env.eventBus.publish(LogoutEvent(user, request, request2Messages))

        request.authenticator match {
          case Some(auth) => env.authenticatorService.discard(auth, Ok(Json.obj("status" -> "OK", "message" -> "Token expired.")))
          case None => Future.successful(Ok(Json.obj("status" -> "KO", "message" -> "Token failed to expire due to authenticator not present.")))
        }

      case None => Future.successful(Ok(Json.obj("status" -> "KO", "message" -> "Not logged in.")))
    }
  }
}