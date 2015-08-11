package models

import java.util.UUID

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
case class Gathering(
  id: Option[Long],
  userId: UUID,
  name:String,
  dedication:String,
  isAchieved:Boolean,
  isPrivate:Boolean,
  year: Int,
  month: Int,
  day: Int)