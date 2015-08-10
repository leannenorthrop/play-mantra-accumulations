package models.daos

import models.Accumulation
import java.util.UUID
import scala.concurrent._

/**
 * Give access to the mantra object.
 */
trait AccumulationDAO {
  def save(accumulation: Accumulation): Future[Option[Accumulation]]
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Option[Accumulation]]
  //def counts(mantraId: Long) : Future[Option[(Long,Long,Long,Long)]]
  //def counts(mantraId: Long, gatheringId: Long) : Future[(Long,Long,Long,Long)]
}
