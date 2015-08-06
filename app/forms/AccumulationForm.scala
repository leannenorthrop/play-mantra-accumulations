package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the submission of the credentials.
 */
object AccumulationForm {

  /**
   * A play framework form.
   */
  val form = Form(mapping(
      "year" -> number(min = 2000, max = 3000),
      "month" -> number(min = 0, max = 11),
      "day" -> number(min = 0, max = 31),
      "count" -> longNumber(min = 0),
      "mantraId" -> longNumber
    )(AccumulationFormData.apply)(AccumulationFormData.unapply)
  )

  case class AccumulationFormData(year: Int,
    month: Int,
    day: Int,
    count: Long,
    mantraId: Long)
}
