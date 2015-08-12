package models.daos

import models.Goal
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

/**
 * Goal Data Access Object implementation using Slick to persist to/from default database.
 * Provides access to persistence layer for all Goal objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
class GoalDAOImpl extends GoalDAO with DAOSlick {
  import driver.api._

  /**
   * Saves given goal.
   *
   * @param goal The goal to save
   * @return goal
   */
  def save(goal: Goal): Future[Goal] = {
    val dbRow = GoalRow(goal.gatheringId, goal.mantraId, goal.goal, if (goal.isAchieved) 1 else 0)
    db.run(goalsTable.insertOrUpdate(dbRow)).map(_ => goal)
  }

  /**
   * Finds goals for specified gathering.
   *
   * @param gatheringId Gathering id to find goals for
   * @return List of goals
   */
  def find(gatheringId: Long): Future[Seq[Goal]] = {
    db.run(goalsTable.filter(_.gatheringId === gatheringId).result).map {
      _.map { row =>
        Goal(row.gatheringId, row.mantraId, row.goal, (row.isAchieved == 1))
      }
    }
  }
}