package models.daos

import models.Gathering
import scala.concurrent.Future

/**
 * Gathering Data Access Object.
 * Provides access to persistence layer for all Gathering objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
trait GatheringDAO {
  /**
   * Saves given gathering. If supplied gathering id is set to None the id field the returned
   * gathering will be set after successful save.
   *
   * @param gathering The gathering to save
   * @return Updated gathering with updated id if supplied gathering id was None
   */
  def save(gathering: Gathering): Future[Gathering]

  /**
   * Finds gatherings for specified mantra.
   *
   * @param mantraID Mantra id to find gatherings for
   * @return List of gatherings
   */
  def find(mantraId: Long): Future[Seq[Gathering]]
}
