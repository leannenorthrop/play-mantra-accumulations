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

class MantraController @Inject() (mantraService:MantraService) extends Controller {
	implicit val mantraWrites: Writes[Mantra] = (
	  (JsPath \ "id").write[Option[Long]] and
	  (JsPath \ "name").write[String] and
	  (JsPath \ "description").write[String] and
	  (JsPath \ "imgUrl").write[String]
	)(unlift(Mantra.unapply))	

	implicit val mantraReads: Reads[Mantra] = (
	  (JsPath \ "id").readNullable[Long] and
	  (JsPath \ "name").read[String](minLength[String](2)) and
	  (JsPath \ "description").read[String](minLength[String](2)) and
	  (JsPath \ "imgUrl").read[String](minLength[String](2))
	)(Mantra.apply _)

	def list = Action {
  		val json = Json.toJson(mantraService.findAll())
  		Ok(json)
	}

	def get(id:Long) = Action {
		val mantra = mantraService.find(id)
		mantra match {
			case Some(m) => Ok(Json.toJson(m))
			case None => NotFound(Json.obj("status" ->"KO", "message" -> ("Unable to find mantra with id " + id)))
		}
	}	

	def save = Action(BodyParsers.parse.json) { request =>
	  val mantraResult = request.body.validate[Mantra]
	  mantraResult.fold(
	    errors => {
	      BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors)))
	    },
	    mantra => { 
	      val savedMantraOption = mantraService.save(mantra)
		  savedMantraOption match {
		  	case Some(m) => Ok(Json.obj("status" ->"OK", "message" -> ("Mantra '"+m.name+"' saved with id '"+m.id.get+"'.")))
          	case None => BadRequest(Json.obj("status" ->"KO", "message" -> "Database error: Trying to create a new mantra that already exists? Please refresh to get latest mantras."))
          }
	    }
	  )
	}	
}