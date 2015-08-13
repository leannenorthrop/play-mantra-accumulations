package models.services

import models.Mantra
import javax.inject._
import models.daos.MantraDAO
import scala.concurrent.Future

/**
 * Service layer implementation for Mantra objects
 *
 * @constructor Creates a new service
 *
 * @param userDAO The user DAO implementation.
 */
class MantraServiceImpl @Inject() (mantraDao: MantraDAO) extends MantraService {
  /**
   * Saves a mantra to persistence layer
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: Mantra): Future[Mantra] = mantraDao.save(mantra)

  /**
   * Finds all non-archived mantra.
   *
   * @return Sequence of all non-archived mantra found.
   */
  def findAll(): Future[Seq[Mantra]] = mantraDao.findAll()

  /**
   * Finds a single mantra by it's id.
   *
   * @param id Id of mantra to find
   * @return Mantra if found, future fails if not found.
   */
  def find(id: Long): Future[Mantra] = mantraDao.findById(id)

  /**
   * 'Deletes' a mantra.
   *
   * @param mantra The mantra to delete
   * @return true if deleted false otherwise
   */
  def delete(mantra: Mantra): Future[Boolean] = mantraDao.delete(mantra)
}
