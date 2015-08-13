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

class MantraRestController @Inject() (val messagesApi: MessagesApi,
    val e: RestEnvironment,
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    credentialsProvider: CredentialsProvider,
    socialProviderRegistry: SocialProviderRegistry,
    configuration: Configuration,
    clock: Clock) extends Silhouette[User, JWTAuthenticator] {
  val env: Environment[User, JWTAuthenticator] = e.env

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Ok(Json.obj("status" -> "OK", "message" -> (user.fullName + " received"))))
      case None => Future.successful(Ok(Json.toJson(Json.obj("status" -> "KO", "message" -> "You are not logged! Login!"))))
    }
  }
}