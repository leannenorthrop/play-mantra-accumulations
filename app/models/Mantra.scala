package models

/**
 * Class representing a Mantra.
 *
 * @constructor Create a new Mantra
 *
 * @param id Set to None if not previously persisted, or to value of id
 * @param name Common, display name of this mantra
 * @param description Textual description of this mantra
 * @param imgUrl URL to use for full page display
 * @param year Year (asummed to be positive and later than 2015)
 * @param month Month (value between 1 and 12)
 * @param day Day (value between 1 and 31)
 */
case class Mantra(
  id: Option[Long],
  name: String,
  description: String,
  imgUrl: String,
  year: Int,
  month: Int,
  day: Int)
