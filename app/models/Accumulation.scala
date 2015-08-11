package models

import java.util.UUID

case class Accumulation(
  id: Option[Long],
  year: Int,
  month: Int,
  day: Int,
  count: Long,
  mantraId: Long,
  userId: UUID,
  gatheringId: Long)