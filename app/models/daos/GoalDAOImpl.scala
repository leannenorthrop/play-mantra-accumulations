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
    val dbRow = GoalRow(goal.gatheringId, goal.mantraId, goal.goal, if (goal.isAchieved) 1 else 0, 0)
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

  /**
   * Finds goals for specified gathering and mantra.
   *
   * @param gatheringId Gathering id to find goal for
   * @param mantraId Mantra id to find goal for
   * @return List of goals
   */
  def find(gatheringId: Long, mantraId: Long): Future[Goal] = {
    db.run(goalsTable.filter(_.gatheringId === gatheringId).filter(_.mantraId === mantraId).result).map { result =>
      val list = result.map { row =>
        Goal(row.gatheringId, row.mantraId, row.goal, (row.isAchieved == 1))
      }
      if (list.length > 0) list.head else throw new java.util.NoSuchElementException(s"Goal for gathering ${gatheringId} and mantra ${mantraId} not found.")
    }
  }

  /**
   * Archives a goal but doesn't remove.
   *
   * @param gatheringId Id of goal's gathering
   * @param mantraId Id of goal's mantra
   * @return true if archived, false otherwise.
   */
  def delete(gatheringId: Long, mantraId: Long): Future[Boolean] = {
    val result = for {
      goal <- db.run(goalsTable.filter(_.gatheringId === gatheringId).filter(_.mantraId === mantraId).result)
      _ <- db.run(goalsTable.insertOrUpdate(goal.head.copy(isArchived = 1)))
      done <- Future { true }
    } yield done
    result recover {
      case _: Throwable => false
    }
  }
}