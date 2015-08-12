package models.services

import models.Mantra

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
  def save(mantra: Mantra): Option[Mantra]

  def findAll(): List[Mantra]

  def find(id: Long): Option[Mantra]
}
