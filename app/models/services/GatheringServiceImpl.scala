package models.services

import models.{ Goal, Gathering }

import javax.inject._
import models.daos.{ GatheringDAO, GoalDAO }
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Service implementation for handling Accumulation objects. Delegates to AccumulationDAO for all persistence.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
class GatheringServiceImpl @Inject() (dao: GatheringDAO, goalDAO: GoalDAO) extends GatheringService {
  /**
   * Saves given gathering returning updated gathering if not previously saved.
   *
   * @param gathering Gathering to save
   * @return Updated accumulation if not previously saved, otherwise the unchanged accumulation.
   */
  def save(gathering: Gathering): Future[Gathering] = dao.save(gathering)

  /**
   * Finds all Gatherings.
   *
   * @return Collection of found Gatherings.
   */
  def find(): Future[Seq[Gathering]] = dao.find()

  /**
   * Finds gathering for specified gathering and mantra.
   *
   * @param gatheringId Gathering id to find Gathering for
   * @param mantraId Mantra id to find Gathering for
   * @return Gathering
   */
  def find(gatheringId: Long, mantraId: Long): Future[Gathering] = dao.find(gatheringId, mantraId)

  /**
   * Archives gathering specified by given gathering id removing from all find results.
   *
   * @param gatheringId Id of gathering to archive.
   * @return true if found and archived, false if not found
   */
  def delete(gatheringId: Long): Future[Boolean] = dao.delete(gatheringId)

  /**
   * Finds Gathering by it's id.
   *
   * @param id Unique id of mantra to find gatherings for
   * @return Found gatherings, if not found future will fail.
   */
  def findByMantra(id: Long): Future[Seq[Gathering]] = dao.find(id)

  /**
   * Finds a goal by it's gathering and mantra id.
   *
   * @param gatheringId Id of gathering goal belongs to
   * @param mantraId Id of mantra goal refers to
   * @return Goal
   */
  def findGoal(gatheringId: Long, mantraId: Long): Future[Goal] = goalDAO.find(gatheringId, mantraId)
  /**
   * Add an accumulation goal to a gathering.
   *
   * @param goal Goal to add
   * @return true if successfully added
   */
  def add(goal: Goal): Future[Boolean] = {
    val f: Future[Boolean] = goalDAO.find(goal.gatheringId, goal.mantraId).map { _ => false }
    val g: Future[Boolean] = f.recoverWith {
      case t: java.util.NoSuchElementException => goalDAO.save(goal).map { _ => true }
    }
    //f fallbackTo g
    g
  }

  /**
   * Remove an accumulation goal from a gathering.
   *
   * @param gatheringId Gathering id of Goal to remove
   * @param mantraId Mantra id of Goal to remove
   * @return true if successfully added
   */
  def remove(gatheringId: Long, mantraId: Long): Future[Boolean] = goalDAO.delete(gatheringId, mantraId)
}