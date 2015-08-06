package models.services

import models.Accumulation
import java.util.UUID

trait AccumulationService {
  def save(accumulation: Accumulation): Option[Accumulation]
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Option[Accumulation]
  def counts(mantraId: Long) : (Long,Long,Long,Long)
  def counts(mantraId: Long, gatheringId: Long) : (Long,Long,Long,Long)
}