package models.services

import models.Mantra
import scala.concurrent.Future

/**
 * Service for handling Mantra objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
trait MantraService {

  /**
   * Saves a mantra to persistence layer
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Future[Mantra]

  /**
   * Finds all non-archived mantra.
   *
   * @return Sequence of all non-archived mantra found.
   */
  def findAll(): Future[Seq[Mantra]]

  /**
   * Finds a single mantra by it's id.
   *
   * @param id Id of mantra to find
   * @return Mantra if found, future fails if not found.
   */
  def find(id: Long): Future[Mantra]

  /**
   * 'Deletes' a mantra.
   *
   * @param mantra The mantra to delete
   * @return true if deleted false otherwise
   */
  def delete(mantra: Mantra): Future[Boolean]
}
