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
}
