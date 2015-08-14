package models

import java.util.UUID

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

/**
 * Class representing a gathering which is used to describe a collection
 * of mantra accumulations with targets.
 *
 * @constructor Create a new Gathering
 *
 * @param id Set to None if not previously persisted, or to value of id
 * @param userId UUID of owning/creating user
 * @param name Display name of this gathering
 * @param dedication Reason for this gathering of mantra accumulations
 * @param isAchieved true if all mantra accumulations associated have
 *        been acheived
 * @param isPrivate true if only owning user can contribute accumulations
 * @param year Year created (asummed to be positive and later than 2015)
 * @param month Month created
 * @param day Day created
 */
case class Gathering(id: Option[Long], userId: UUID, name: String, dedication: String, isAchieved: Boolean, isPrivate: Boolean, year: Int, month: Int, day: Int)

object Gathering {
  implicit object UUIDFormatter extends Format[UUID] {
    override def reads(json: JsValue): JsResult[UUID] = {
      val uuid = json.validate[String]
      JsSuccess(UUID.fromString(uuid.get))
    }

    override def writes(o: UUID): JsValue = {
      JsString(o.toString)
    }
  }

  implicit val gatheringWrites: Writes[Gathering] = (
    (JsPath \ "id").write[Option[Long]] and
    (JsPath \ "owner").write[UUID] and
    (JsPath \ "name").write[String] and
    (JsPath \ "dedication").write[String] and
    (JsPath \ "isAchieved").write[Boolean] and
    (JsPath \ "isPrivate").write[Boolean] and
    (JsPath \ "year").write[Int] and
    (JsPath \ "month").write[Int] and
    (JsPath \ "day").write[Int]

  )(unlift(Gathering.unapply))

  implicit val gatheringReads: Reads[Gathering] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "owner").read[UUID] and
    (JsPath \ "name").read[String](minLength[String](2)) and
    (JsPath \ "dedication").read[String](minLength[String](2)) and
    (JsPath \ "isAchieved").read[Boolean] and
    (JsPath \ "isPrivate").read[Boolean] and
    (JsPath \ "year").read[Int] and
    (JsPath \ "month").read[Int] and
    (JsPath \ "day").read[Int]
  )(Gathering.apply _)
}