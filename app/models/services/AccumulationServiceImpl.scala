package models.services

import models.Accumulation

import javax.inject._
import models.daos.AccumulationDAO
import java.util.UUID

class AccumulationServiceImpl @Inject() (dao:AccumulationDAO) extends AccumulationService {
  def save(accumulation: Accumulation): Option[Accumulation] = ??? //dao.save(accumulation)
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Option[Accumulation] = ??? //dao.findForToday(userId, gatheringId, mantraId)
  def counts(mantraId: Long) : (Long,Long,Long,Long) = ??? //dao.counts(mantraId)
  def counts(mantraId: Long, gatheringId: Long) : (Long,Long,Long,Long) = ??? //dao.counts(mantraId,gatheringId)
}
