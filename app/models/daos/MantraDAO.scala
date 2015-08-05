package models.daos

import models.Mantra

/**
 * Give access to the mantra object.
 */
trait MantraDAO {
  /**
   * Finds a mantra by its ID.
   *
   * @param mantraID The ID of the mantra to find.
   * @return The found mantra or None if no mantra for the given ID could be found.
   */
  def findById(mantraID: Long): Option[Mantra]

  /**
   * Finds all mantra.
   *
   * @return The found mantra or empty list if no mantra could be found.
   */
  def findAll(): List[Mantra]

  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Option[Mantra]
}
