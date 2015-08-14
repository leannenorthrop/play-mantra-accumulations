package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

/**
 * Class representing a Mantra.
 *
 * @constructor Create a new Mantra
 *
 * @param id Set to None if not previously persisted, or to value of id
 * @param name Common, display name of this mantra
 * @param description Textual description of this mantra
 * @param imgUrl URL to use for full page display
 * @param year Year (asummed to be positive and later than 2015)
 * @param month Month (value between 1 and 12)
 * @param day Day (value between 1 and 31)
 */
case class Mantra(id: Option[Long], name: String, description: String, imgUrl: String, year: Int, month: Int, day: Int)

object Mantra {
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
}