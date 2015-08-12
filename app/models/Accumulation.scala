package models

import java.util.UUID

/**
 * Class representing an Accumulation which is used to describe the number
 * of mantra accumulations that occurred on a particular day.
 *
 * @constructor Create a new Accumulation
 *
 * @param id Set to None if not previously persisted, or to value of id
 * @param year Year (asummed to be positive and later than 2015)
 * @param month Month (value between 1 and 12)
 * @param day Day (value between 1 and 31)
 * @param count Number of mantra accumulations for this date
 * @param mantraId Id of mantra this accumulation is related to
 * @param userId UUID of owning/creating user
 * @param gatheringID Id of gathering this accumulation is related to
 */
case class Accumulation(
  id: Option[Long],
  year: Int,
  month: Int,
  day: Int,
  count: Long,
  mantraId: Long,
  userId: UUID,
  gatheringId: Long)