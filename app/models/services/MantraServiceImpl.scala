package models.services

import models.Mantra
import javax.inject._
import models.daos.MantraDAO
import scala.concurrent.Future

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 */
class MantraServiceImpl @Inject() (mantraDao: MantraDAO) extends MantraService {
  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Future[Mantra] = mantraDao.save(mantra)

  def findAll(): Future[Seq[Mantra]] = mantraDao.findAll()

  def find(id: Long): Future[Mantra] = mantraDao.findById(id)
}
