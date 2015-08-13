package models.services

import models.Gathering

import javax.inject._
import models.daos.GatheringDAO
import scala.concurrent._

/**
 * Service implementation for handling Accumulation objects. Delegates to AccumulationDAO for all persistence.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
class GatheringServiceImpl @Inject() (dao: GatheringDAO) extends GatheringService {
  /**
   * Saves given gathering returning updated gathering if not previously saved.
   *
   * @param gathering Gathering to save
   * @return Updated accumulation if not previously saved, otherwise the unchanged accumulation.
   */
  def save(gathering: Gathering): Future[Gathering] = dao.save(gathering)

  /**
   * Finds Gathering by it's id.
   *
   * @param id Unique id of mantra to find gatherings for
   * @return Found gatherings, if not found future will fail.
   */
  def findByMantra(id: Long): Future[Seq[Gathering]] = dao.find(id)
}