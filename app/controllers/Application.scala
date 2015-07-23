package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index(name:String) = Action {
    Ok(views.html.index(name))
  }

}
