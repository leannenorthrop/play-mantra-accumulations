package controllers.rest

import javax.inject.Inject
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.{ Environment, Silhouette }
import modules.RestEnvironment
import models.{ User, Mantra }
import models.services.MantraService

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{ Action, BodyParsers }
import play.api.i18n.{ Messages, MessagesApi }
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

class MantraRestController @Inject() (val messagesApi: MessagesApi,
    val e: RestEnvironment,
    val mantraService: MantraService) extends Silhouette[User, JWTAuthenticator] {
  import models.Mantra._

  val env: Environment[User, JWTAuthenticator] = e.env

  def index = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) =>
        mantraService.findAll() map { mantras =>
          Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(mantras)))
        } recover {
          case t: Throwable => NotFound(Json.obj("status" -> "KO", "message" -> ("Unable to find mantra. " + t.getMessage())))
        }
      case None => Future.successful(Ok(Json.toJson(Json.obj("status" -> "KO", "message" -> "You are not logged! Login!"))))
    }
  }

  def get(id: Long) = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) =>
        mantraService.find(id) map { mantra =>
          Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(mantra)))
        } recover {
          case t: Throwable => NotFound(Json.obj("status" -> "KO", "message" -> ("Unable to find mantra. " + t.getMessage())))
        }
      case None => Future.successful(Ok(Json.toJson(Json.obj("status" -> "KO", "message" -> "You are not logged! Login!"))))
    }
  }

  def save = UserAwareAction.async(BodyParsers.parse.json) { implicit request =>
    request.identity match {
      case Some(user) =>
        val mantraResult = request.body.validate[Mantra]
        mantraResult.fold(
          errors => {
            Future { BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))) }
          },
          mantra => {
            mantraService.save(mantra) map { savedMantraOption =>
              Ok(Json.obj("status" -> "OK", "message" -> ("Mantra '" + savedMantraOption.name + "' saved with id '" + savedMantraOption.id.get + "'.")))
            } recover {
              case _: Throwable => BadRequest(Json.obj("status" -> "KO", "message" -> "Database error: Trying to create a new mantra that already exists? Please refresh to get latest mantras."))
            }
          }
        )
      case None => Future.successful(Ok(Json.toJson(Json.obj("status" -> "KO", "message" -> "You are not logged! Login!"))))
    }
  }
}