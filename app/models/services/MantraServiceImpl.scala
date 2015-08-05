package models.services

import models.Mantra
import javax.inject._
import models.daos.MantraDAO

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 */
class MantraServiceImpl @Inject() (mantraDao:MantraDAO) extends MantraService {
  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Option[Mantra] = mantraDao.save(mantra)

  def findAll(): List[Mantra] = mantraDao.findAll()

  def find(id:Long): Option[Mantra] = mantraDao.findById(id)
}
