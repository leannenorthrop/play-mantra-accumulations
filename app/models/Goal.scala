package models

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
