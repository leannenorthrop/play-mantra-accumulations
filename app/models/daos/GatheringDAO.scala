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
   * Finds all Gatherings.
   *
   * @return Collection of found Gatherings.
   */
  def find(): Future[Seq[Gathering]]

  /**
   * Finds gatherings for specified mantra.
   *
   * @param mantraID Mantra id to find gatherings for
   * @return List of gatherings
   */
  def find(mantraId: Long): Future[Seq[Gathering]]

  /**
   * Returns true if gathering with the given name exists, false otherwise.
   *
   * @param name Name of gathering to check existence for
   * @return true if exists in persistence layer, false otherwise
   */
  def exists(name: String): Future[Boolean]

  /**
   * 'Delete' gathering. Subsequent finds will not find given gathering.
   *
   * @param gatheringId The gathering id to hide/delete
   * @return true if successfully archived
   */
  def delete(gatheringId: Long): Future[Boolean]
}
