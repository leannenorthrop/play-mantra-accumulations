package models.daos

import models.Goal
import scala.concurrent.Future

/**
 * Goal Data Access Object.
 * Provides access to persistence layer for all Goal objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
trait GoalDAO {
  /**
   * Saves given goal.
   *
   * @param goal The goal to save
   * @return goal
   */
  def save(goal: Goal): Future[Goal]

  /**
   * Finds goals for specified gathering.
   *
   * @param gatheringId Gathering id to find goals for
   * @return List of goals
   */
  def find(gatheringId: Long): Future[Seq[Goal]]

  /**
   * Finds goals for specified gathering and mantra.
   *
   * @param gatheringId Gathering id to find goal for
   * @param mantraId Mantra id to find goal for
   * @return List of goals
   */
  def find(gatheringId: Long, mantraId: Long): Future[Goal]

  /**
   * Archives a goal but doesn't remove.
   *
   * @param gatheringId Id of goal's gathering
   * @param mantraId Id of goal's mantra
   * @return true if archived, false otherwise.
   */
  def delete(gatheringId: Long, mantraId: Long): Future[Boolean]
}
