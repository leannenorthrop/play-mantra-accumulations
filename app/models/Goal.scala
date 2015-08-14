package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

/**
 * Class representing a goal which is used to describe a target of
 * of mantra accumulations to achieve.
 *
 * @constructor Create a new Goal
 *
 * @param gatheringId Id of gathering this goal belongs to
 * @param mantraId Id of mantra this goal belongs to
 * @param goal Number of mantra accumulations to achieve
 * @param isAchieved true if all mantra accumulations reach or exceed target
 */
case class Goal(gatheringId: Long, mantraId: Long, goal: Long, isAchieved: Boolean)

object Goal {
  implicit val goalWrites: Writes[Goal] = (
    (JsPath \ "gatheringId").write[Long] and
    (JsPath \ "mantraId").write[Long] and
    (JsPath \ "goal").write[Long] and
    (JsPath \ "isAchieved").write[Boolean]

  )(unlift(Goal.unapply))

  implicit val goalReads: Reads[Goal] = (
    (JsPath \ "gatheringId").read[Long] and
    (JsPath \ "mantraId").read[Long] and
    (JsPath \ "goal").read[Long] and
    (JsPath \ "isAchieved").read[Boolean]
  )(Goal.apply _)
}