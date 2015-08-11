package models.services

import models.Accumulation

import javax.inject._
import models.daos.AccumulationDAO
import java.util.UUID
import scala.concurrent._
import java.util.Calendar
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class AccumulationServiceImpl @Inject() (dao:AccumulationDAO) extends AccumulationService {
  def save(accumulation: Accumulation): Future[Accumulation] = dao.save(accumulation)
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation] = dao.findForToday(userId, gatheringId, mantraId)
  def findOrCreateForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation] = {
  	findForToday(userId, gatheringId, mantraId) recoverWith {
      case _: Throwable =>        
          val cal = Calendar.getInstance()
          val year = cal.get(Calendar.YEAR)
          val month = cal.get(Calendar.MONTH) + 1
          val day = cal.get(Calendar.DAY_OF_MONTH)
          val accumulation = Accumulation(None, year, month, day, 0L, mantraId, userId, gatheringId)
          dao.save(accumulation)
    }
  }
  def counts(mantraId: Long) : Future[(Long,Long,Long,Long)] = dao.counts(mantraId)(None)
  def counts(mantraId: Long, gatheringId: Long) : Future[(Long,Long,Long,Long)] = dao.counts(mantraId)(Some(gatheringId))
}
