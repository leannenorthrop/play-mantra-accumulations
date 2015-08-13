package models.daos

import models.Mantra
import scala.concurrent.Future

/**
 * Mantra Data Access Object.
 * Provides access to persistence layer for all Mantra objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
trait MantraDAO {
  /**
   * Finds a mantra by its ID.
   *
   * @param mantraID The ID of the mantra to find.
   * @return The found mantra or None if no mantra for the given ID could be found.
   */
  def findById(mantraID: Long): Future[Mantra]

  /**
   * Finds all mantra.
   *
   * @return The found mantra or empty list if no mantra could be found.
   */
  def findAll(): Future[Seq[Mantra]]

  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Future[Mantra]

  /**
   * 'Deletes' a mantra.
   *
   * @param mantra The mantra to delete
   * @return true if deleted false otherwise
   */
  def delete(mantra: Mantra): Future[Boolean]
}
