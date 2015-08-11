package models

case class Mantra(
  id: Option[Long],
  name: String,
  description: String,
  imgUrl: String,
  year:Int,
  month:Int,
  day:Int)
