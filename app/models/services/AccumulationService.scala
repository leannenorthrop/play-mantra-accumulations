package models.services

import models.Accumulation
import java.util.UUID
import scala.concurrent._

trait AccumulationService {
  def save(accumulation: Accumulation): Future[Accumulation]
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation]
  def findOrCreateForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Accumulation]
  def counts(mantraId: Long) : Future[(Long,Long,Long,Long)]
  def counts(mantraId: Long, gatheringId: Long) : Future[(Long,Long,Long,Long)]
}