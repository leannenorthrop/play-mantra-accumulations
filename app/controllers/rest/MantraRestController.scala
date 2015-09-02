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

  def index = SecuredAction.async { implicit request =>
    mantraService.findAll() map { mantras =>
      Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(mantras)))
    } recover {
      case t: Throwable => InternalServerError(Json.obj("status" -> "KO", "message" -> ("Unable to find mantra. " + t.getMessage())))
    }
  }

  def find(id: Long) = SecuredAction.async { implicit request =>
    mantraService.find(id) map { mantra =>
      Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(mantra)))
    } recover {
      case t: Throwable => InternalServerError(Json.obj("status" -> "KO", "message" -> ("Unable to find mantra. " + t.getMessage())))
    }
  }

  def save = SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val mantraResult = request.body.validate[Mantra]
    mantraResult match {
      case e: JsError => {
        val str = e.errors.map {
          case (path, seq) => "" + path + seq.map { e =>
            Messages(e.message, e.args: _*)
          }.foldLeft("")(_ + ": " + _)
        }

        Future { BadRequest(Json.obj("status" -> "KO", "message" -> "Json errors.", "errors" -> str)) }
      }
      case s: JsSuccess[Mantra] => {
        val mantra = s.get
        mantraService.save(mantra) map { savedMantraOption =>
          Ok(Json.obj("status" -> "OK", "message" -> ("Mantra '" + savedMantraOption.name + "' saved with id '" + savedMantraOption.id.get + "'.")))
        } recover {
          case _: Throwable => InternalServerError(Json.obj("status" -> "KO", "message" -> "Database error: Trying to create a new mantra that already exists? Please refresh to get latest mantras."))
        }
      }
    }
  }
}