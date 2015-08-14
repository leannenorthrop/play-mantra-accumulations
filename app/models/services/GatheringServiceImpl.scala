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
   * Finds Gathering by it's id.
   *
   * @param id Unique id of mantra to find gatherings for
   * @return Found gatherings, if not found future will fail.
   */
  def findByMantra(id: Long): Future[Seq[Gathering]] = dao.find(id)

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