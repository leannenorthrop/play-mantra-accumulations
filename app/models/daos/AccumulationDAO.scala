package models.daos

import models.Accumulation
import java.util.UUID

/**
 * Give access to the mantra object.
 */
trait AccumulationDAO {
  def save(accumulation: Accumulation): Option[Accumulation]
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Option[Accumulation] 
  def counts(mantraId: Long) : (Long,Long,Long,Long)
  def counts(mantraId: Long, gatheringId: Long) : (Long,Long,Long,Long)
}
