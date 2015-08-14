package models.services

import models.{ Gathering, Goal }
import scala.concurrent._

/**
 * Service for handling Gathering objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0Gathering
 */
trait GatheringService {
  /**
   * Saves given gathering returning updated gathering if not previously saved.
   *
   * @param gathering Gathering to save
   * @return Updated accumulation if not previously saved, otherwise the unchanged accumulation.
   */
  def save(gathering: Gathering): Future[Gathering]

  /**
   * Finds Gathering by it's id.
   *
   * @param id Unique id of mantra to find gatherings for
   * @return Found gatherings, if not found future will fail.
   */
  def findByMantra(id: Long): Future[Seq[Gathering]]

  /**
   * Add an accumulation goal to a gathering.
   *
   * @param goal Goal to add
   * @return true if successfully added
   */
  def add(goal: Goal): Future[Boolean]

  /**
   * Remove an accumulation goal from a gathering.
   *
   * @param gatheringId Gathering id of Goal to remove
   * @param mantraId Mantra id of Goal to remove
   * @return true if successfully added
   */
  def remove(gatheringId: Long, mantraId: Long): Future[Boolean]
}