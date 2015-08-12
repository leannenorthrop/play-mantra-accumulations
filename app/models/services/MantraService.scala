package models.services

import models.Mantra
import scala.concurrent.Future

/**
 * Handles actions to mantra.
 */
trait MantraService {

  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Future[Mantra]

  def findAll(): Future[Seq[Mantra]]

  def find(id: Long): Future[Mantra]
}
