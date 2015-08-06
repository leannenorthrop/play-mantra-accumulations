package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import java.util.UUID

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param socialProviderRegistry The social provider registry.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[User, CookieAuthenticator] {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = Action { implicit request =>
    Ok(views.html.index("Leanne"))
  }

  /**
   * Handles the home action.
   *
   * @return The result to display.
   */
  def home = SecuredAction.async { implicit request =>
    println(request.identity)
    Future.successful(Ok(views.html.home(request.identity)))
  }

  /**
   * Handles the Sign In action.
   *
   * @return The result to display.
   */
  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.get()))
      case None => Future.successful(Ok(views.html.signIn(SignInForm.form, socialProviderRegistry)))
    }
  }

  /**
   * Handles the Sign Up action.
   *
   * @return The result to display.
   */
  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.home()))
      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.home())
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))

    env.authenticatorService.discard(request.authenticator, result)
  }

  /**
   * Handles the home action.
   *
   * @return The result to display.
   */
  def save = SecuredAction.async { implicit securedRequest =>
      AccumulationForm.form.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.accumulation_form(Some(securedRequest.identity),formWithErrors,socialProviderRegistry)))
        },
        accumulationFormData => {
          val newAccumulation = models.Accumulation(accumulationFormData.year,
            accumulationFormData.month,
            accumulationFormData.day,
            accumulationFormData.count,
            accumulationFormData.mantraId,
            UUID.randomUUID())
          println(newAccumulation)
          Future.successful(Ok(views.html.accumulation_form(Some(securedRequest.identity),AccumulationForm.form, socialProviderRegistry)))
        }
      )
  }  

  def get = UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.accumulation_form(request.identity,AccumulationForm.form, socialProviderRegistry)))
  }  
}
