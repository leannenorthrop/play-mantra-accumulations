package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import models.Mantra
import models.services.MantraService
import controllers.responses._
import javax.inject._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._

/**
 *
 * Curl Examples:
 *
 * Save:
 *   - Find All: curl http://localhost:9000/api/mantra -vvvv -H 'X-Auth-Token: ...'
 *   - Find By Id: curl http://localhost:9000/api/mantra/1 -vvvv -H 'X-Auth-Token: ...'
 *   - New: curl -X POST http://localhost:9000/mantra -H 'Content-Type: application/json' -d '{"id":null,"name":"mani","description":"No description","imgUrl":"http://someimg","year":2015,"month":8,"day":6}' -vvvv -H 'X-Auth-Token: ...'
 *   - Existing: curl -X POST http://localhost:9000/mantra -H 'Content-Type: application/json' -d '{"id":2,"name":"mani","description":"No description","imgUrl":"http://someimg","year":2015,"month":8,"day":6}' -vvvv -H 'X-Auth-Token: ...'
 */
class MantraController @Inject() (mantraService: MantraService) extends Controller {
  implicit val mantraWrites: Writes[Mantra] = (
    (JsPath \ "id").write[Option[Long]] and
    (JsPath \ "name").write[String] and
    (JsPath \ "description").write[String] and
    (JsPath \ "imgUrl").write[String] and
    (JsPath \ "year").write[Int] and
    (JsPath \ "month").write[Int] and
    (JsPath \ "day").write[Int]

  )(unlift(Mantra.unapply))

  implicit val mantraReads: Reads[Mantra] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "name").read[String](minLength[String](2)) and
    (JsPath \ "description").read[String](minLength[String](2)) and
    (JsPath \ "imgUrl").read[String](minLength[String](2)) and
    (JsPath \ "year").read[Int] and
    (JsPath \ "month").read[Int] and
    (JsPath \ "day").read[Int]
  )(Mantra.apply _)

  def list = Action.async {
    mantraService.findAll().map { found =>
      val json = Json.toJson(found)
      Ok(json)
    }
  }

  def get(id: Long) = Action.async {
    mantraService.find(id) map { mantra =>
      Ok(Json.toJson(mantra))
    } recover {
      case _: Throwable => NotFound(Json.obj("status" -> "KO", "message" -> ("Unable to find mantra with id " + id)))
    }
  }

  def save = Action.async(BodyParsers.parse.json) { request =>
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
  }
}