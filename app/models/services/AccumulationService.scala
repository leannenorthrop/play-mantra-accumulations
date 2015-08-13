package models.services

import models.Accumulation
import java.util.UUID
import scala.concurrent._

/**
 * Service for handling Accumulation objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
trait AccumulationService {
  /**
   * Saves given accumulation returning updated accumulation if not previously saved.
   *
   * @param accumulation Accumulation to save
   * @return Updated accumulation if not previously saved, otherwise the unchanged accumulation.
   */
  def save(accumulation: Accumulation): Future[Accumulation]

  /**
   * Finds Accumulation for today, for given user, gathering and mantra, failing future
   * if not found.
   *
   * @param userId UUID of user to find accumulation for
   * @param gatheringID Id of gathering the accumulation relates to
   * @param mantraId Id of mantra the accumulation relates to
   * @return Found accumulation, if not found future will fail.
   */
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation]

  /**
   * Finds Accumulation for today, for given user, gathering and mantra creating and saving
   * if it doesn't already exist.
   *
   * @param userId UUID of user to find accumulation for
   * @param gatheringID Id of gathering the accumulation relates to
   * @param mantraId Id of mantra the accumulation relates to
   * @return Accumulation for today.
   */
  def findOrCreateForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation]

  /**
   * Returns tuple of (grand total, this year total, this month total this day total) for the
   * given mantra.
   *
   * @param mantraId Id of mantra to retrieve counts for
   * @return (grand total, this year total, this month total this day total)
   */
  def counts(mantraId: Long): Future[(Long, Long, Long, Long)]

  /**
   * Returns tuple of (grand total, this year total, this month total this day total) for the
   * given mantra and gathering.
   *
   * @param mantraId Id of mantra to retrieve counts for
   * @param gatheringId Id of gathering to retrieve counts for
   * @return (grand total, this year total, this month total this day total)
   */
  def counts(mantraId: Long, gatheringId: Long): Future[(Long, Long, Long, Long)]
}