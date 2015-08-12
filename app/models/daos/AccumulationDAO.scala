package models.daos

import models.Accumulation
import java.util.UUID
import scala.concurrent._

/**
 * Accumulation Data Access Object.
 * Provides access to persistence layer for all Accumulation objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
trait AccumulationDAO {
  /**
   * Saves given accumulation. If supplied accumulation id is set to None the id field the returned
   * accumulation will be set after successful save.
   *
   * @param accumulation The accumulation to save
   * @return Updated accumulation with updated id if supplied accumulation id was None
   */
  def save(accumulation: Accumulation): Future[Accumulation]

  /**
   * Finds 'todays' accumulation for specified user, gathering and mantra.
   *
   * @param userId UUID of user to find accumulation for
   * @param gatheringID Gathering id to find accumulation for
   * @param mantraID Mantra id to find accumulation for
   * @return Today's accumulation
   */
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation]

  /**
   * Return (overall, this year, this month, this day) sum of accumulation ammounts for the
   * given gathering and mantra.
   *
   * @param mantraId Mantra id to find counts for
   * @param gatheringId If None returns sums for just mantra, if Some returns sums for mantra and gathering
   * @return Tuple of (grand total, this year, this month, this day) for given mantra or mantra/gathering
   */
  def counts(mantraId: Long)(gatheringId: Option[Long]): Future[(Long, Long, Long, Long)]
}
