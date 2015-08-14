package controllers.rest

import javax.inject.Inject
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.api.{ Environment, Silhouette }
import modules.RestEnvironment
import models.{ User, Gathering, Goal }
import models.services._

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
import java.util.UUID

class GatheringRestController @Inject() (val messagesApi: MessagesApi,
    e: RestEnvironment,
    mantraService: MantraService,
    gatheringService: GatheringService,
    accumulationService: AccumulationService) extends Silhouette[User, JWTAuthenticator] {
  import models.Goal._
  import models.Gathering._

  val env: Environment[User, JWTAuthenticator] = e.env

  def index = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) =>
        gatheringService.find() map { gatherings =>
          Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(gatherings)))
        } recover {
          case t: Throwable => NotFound(Json.obj("status" -> "KO", "message" -> ("Unable to find gatherings. " + t.getMessage())))
        }
      case None => Future.successful(Ok(Json.toJson(Json.obj("status" -> "KO", "message" -> "You are not logged! Login!"))))
    }
  }

  def find(mantraId: Long) = SecuredAction.async { implicit request =>
    gatheringService.findByMantra(mantraId) map { gatherings =>
      Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(gatherings)))
    } recover {
      case t: Throwable => NotFound(Json.obj("status" -> "KO", "message" -> ("Unable to find gatherings. " + t.getMessage())))
    }
  }

  def delete(gatheringId: Long) = SecuredAction.async { implicit request =>
    gatheringService.delete(gatheringId) map { isDeleted =>
      if (isDeleted)
        Ok(Json.obj("status" -> "OK", "message" -> "Gathering successfully archived"))
      else
        Ok(Json.obj("status" -> "KO", "message" -> "No such gathering."))
    } recover {
      case t: Throwable => NotFound(Json.obj("status" -> "KO", "message" -> ("Unable to find gathering. " + t.getMessage())))
    }
  }

  /**
   *
   * Example:
   * curl -X POST http://localhost:9000/api/gatherings -H 'Content-Type: application/json' -d '{"id":null, "owner": "600ba5de-01ff-4cb5-9b7b-ec4c5521f6e3", "name": "hello", "dedication":"dedication", "isAchieved":false, "isPrivate": false, "year": 2015, "month" : 8, "day":20}' -vvvv -H 'X-Auth-Token:...'
   */
  def save = SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val gathering = request.body.validate[Gathering]
    gathering.fold(
      errors => {
        Future { BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))) }
      },
      gathering => {
        gatheringService.save(gathering) map { savedGathering =>
          Ok(Json.obj("status" -> "OK", "message" -> ("Gathering '" + savedGathering.name + "' saved with id '" + savedGathering.id.get + "'.")))
        } recover {
          case _: Throwable => BadRequest(Json.obj("status" -> "KO", "message" -> "Database error: Trying to create a new gathering that already exists? Please refresh to get latest gatherings."))
        }
      }
    )
  }

  def addGoal(gatheringId: Long, mantraId: Long) = SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val goal = request.body.validate[Goal]
    goal.fold(
      errors => {
        Future { BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors))) }
      },
      goal => {
        gatheringService.add(goal) map { isSaved =>
          if (isSaved)
            Ok(Json.obj("status" -> "OK", "message" -> ("Gathering goal was saved.")))
          else
            Ok(Json.obj("status" -> "KO", "message" -> ("Gathering goal was not saved. Already exists?")))
        } recover {
          case _: Throwable => BadRequest(Json.obj("status" -> "KO", "message" -> "Database error: Trying to create a new gathering goal that already exists? Please refresh to get latest goals."))
        }
      }
    )
  }

  def removeGoal(gatheringId: Long, mantraId: Long) = SecuredAction.async { implicit request =>
    gatheringService.remove(gatheringId, mantraId) map { isComplete =>
      if (isComplete)
        Ok(Json.obj("status" -> "OK", "message" -> ("Gathering goal was deleted.")))
      else
        Ok(Json.obj("status" -> "KO", "message" -> ("Gathering goal was not deleted. Did it exist?")))
    } recover {
      case t: Throwable => BadRequest(Json.obj("status" -> "KO", "message" -> ("Database error: Trying to delete gathering goal. " + t.getMessage())))
    }
  }

  def findGoal(gatheringId: Long, mantraId: Long) = SecuredAction.async { implicit request =>
    gatheringService.findGoal(gatheringId, mantraId) map { goal =>
      Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(goal)))
    } recover {
      case t: Throwable => BadRequest(Json.obj("status" -> "KO", "message" -> ("Database error: " + t.getMessage())))
    }
  }

  def findGathering(gatheringId: Long, mantraId: Long) = SecuredAction.async { implicit request =>
    gatheringService.find(gatheringId, mantraId) map { gathering =>
      Ok(Json.obj("status" -> "OK", "message" -> Json.toJson(gathering)))
    } recover {
      case t: Throwable => BadRequest(Json.obj("status" -> "KO", "message" -> ("Database error: " + t.getMessage())))
    }
  }
}