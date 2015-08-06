package models

import java.util.UUID

case class Accumulation(
  year: Int,
  month: Int,
  day: Int,
  count: Long,
  mantraId: Long,
  userID: UUID)