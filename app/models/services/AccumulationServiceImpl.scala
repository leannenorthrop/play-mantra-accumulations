package models.services

import models.Accumulation

import javax.inject._
import models.daos.AccumulationDAO
import java.util.UUID
import scala.concurrent._
import java.util.Calendar
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Service implementation for handling Accumulation objects. Delegates to AccumulationDAO for all persistence.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
class AccumulationServiceImpl @Inject() (dao: AccumulationDAO) extends AccumulationService {

  /**
   * Saves given accumulation returning updated accumulation if not previously saved.
   *
   * @param accumulation Accumulation to save
   * @return Updated accumulation if not previously saved, otherwise the unchanged accumulation.
   */
  def save(accumulation: Accumulation): Future[Accumulation] = dao.save(accumulation)

  /**
   * Finds Accumulation for today, for given user, gathering and mantra, failing future
   * if not found.
   *
   * @param userId UUID of user to find accumulation for
   * @param gatheringID Id of gathering the accumulation relates to
   * @param mantraId Id of mantra the accumulation relates to
   * @return Found accumulation, if not found future will fail.
   */
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation] = dao.findForToday(userId, gatheringId, mantraId)

  /**
   * Finds Accumulation for today, for given user, gathering and mantra creating and saving
   * if it doesn't already exist.
   *
   * @param userId UUID of user to find accumulation for
   * @param gatheringID Id of gathering the accumulation relates to
   * @param mantraId Id of mantra the accumulation relates to
   * @return Accumulation for today.
   */
  def findOrCreateForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation] = {
    findForToday(userId, gatheringId, mantraId) recoverWith {
      case _: Throwable =>
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val accumulation = Accumulation(None, year, month, day, 0L, mantraId, userId, gatheringId)
        println("Saving " + accumulation)
        dao.save(accumulation)
    }
  }

  /**
   * Returns tuple of (grand total, this year total, this month total this day total) for the
   * given mantra.
   *
   * @param mantraId Id of mantra to retrieve counts for
   * @return (grand total, this year total, this month total this day total)
   */
  def counts(mantraId: Long): Future[(Long, Long, Long, Long)] = dao.counts(mantraId)(None)

  /**
   * Returns tuple of (grand total, this year total, this month total this day total) for the
   * given mantra and gathering.
   *
   * @param mantraId Id of mantra to retrieve counts for
   * @param gatheringId Id of gathering to retrieve counts for
   * @return (grand total, this year total, this month total this day total)
   */
  def counts(mantraId: Long, gatheringId: Long): Future[(Long, Long, Long, Long)] = dao.counts(mantraId)(Some(gatheringId))
}
