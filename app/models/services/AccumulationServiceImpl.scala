package models.services

import models.Accumulation

import javax.inject._
import models.daos.AccumulationDAO
import java.util.UUID
import scala.concurrent._

class AccumulationServiceImpl @Inject() (dao:AccumulationDAO) extends AccumulationService {
  def save(accumulation: Accumulation): Future[Option[Accumulation]] = dao.save(accumulation)
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Option[Accumulation]] = dao.findForToday(userId, gatheringId, mantraId)
  def counts(mantraId: Long) : Future[(Long,Long,Long,Long)] = dao.counts(mantraId)(None)
  def counts(mantraId: Long, gatheringId: Long) : Future[(Long,Long,Long,Long)] = dao.counts(mantraId)(Some(gatheringId))
}
