package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi
import models.services.AccumulationService
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
  socialProviderRegistry: SocialProviderRegistry,
  accumulationService: AccumulationService)
  extends Silhouette[User, CookieAuthenticator] {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.home(request.identity)))
  }

  /**
   * Handles the home action.
   *
   * @return The result to display.
   */
  def home = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.home(Some(request.identity))))
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
          val uid = UUID.fromString(accumulationFormData.userId)
          for {
            acc <- accumulationService.findForToday(uid, -1, accumulationFormData.mantraId)
            _ <- if (acc == None) accumulationService.save(Accumulation(None, accumulationFormData.year, accumulationFormData.month, accumulationFormData.day, accumulationFormData.count, accumulationFormData.mantraId, UUID.fromString(accumulationFormData.userId), -1)) else accumulationService.save(acc.copy(count = acc.count + accumulationFormData.count))
            counts <- accumulationService.counts(1)
          } yield counts map { counts =>
            Future.successful(Ok(views.html.accumulation_form(Some(securedRequest.identity),AccumulationForm.form, socialProviderRegistry, counts)))              
          } recover {
            case _ : Throwable => Future.successful(InternalServerError())           
          }
        }
      )
  }  

  def get = UserAwareAction.async { implicit request =>
    val counts = accumulationService.counts(1)
    Future.successful(Ok(views.html.accumulation_form(request.identity, AccumulationForm.form, socialProviderRegistry, counts)))
  }  
}
