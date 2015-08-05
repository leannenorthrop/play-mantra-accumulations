package models

import play.api.libs.json.{Json, Format}

case class Mantra(
  id: Option[Long],
  name: String,
  description: String,
  imgUrl: String)
