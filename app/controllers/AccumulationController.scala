package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms.AccumulationForm
import models.{Accumulation, User}
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
class AccumulationController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  accumulationService: AccumulationService)
  extends Silhouette[User, CookieAuthenticator] {
  /**
   * Handles the home action.
   *
   * @return The result to display.
   */
  def save = SecuredAction.async { implicit securedRequest =>
      AccumulationForm.form.bindFromRequest.fold(
        formWithErrors => {
          accumulationService.counts(1).map { c =>
            Ok(views.html.accumulation_form(Some(securedRequest.identity),formWithErrors, socialProviderRegistry, c))
          } recover {
            case e : Throwable => e.printStackTrace(); Ok(views.html.accumulation_form(Some(securedRequest.identity),formWithErrors, socialProviderRegistry, (0L,0L,0L,0L)))
          }
        },
        accumulationFormData => {
          val uid = UUID.fromString(accumulationFormData.userId)
          for {
            acc <- accumulationService.findOrCreateForToday(uid, -1, accumulationFormData.mantraId)
            _ <- accumulationService.save(acc.get.copy(count = acc.get.count + accumulationFormData.count)) if acc != None
          } yield ()
          accumulationService.counts(1).map { c =>
            Ok(views.html.accumulation_form(Some(securedRequest.identity),AccumulationForm.form, socialProviderRegistry, c))
          } recover {
            case e : Throwable => e.printStackTrace(); Ok(views.html.accumulation_form(Some(securedRequest.identity),AccumulationForm.form, socialProviderRegistry, (0L,0L,0L,0L)))
          }
        }
      )
  }  

  def get = UserAwareAction.async { implicit request =>
    accumulationService.counts(1).map { counts =>
      Ok(views.html.accumulation_form(request.identity,AccumulationForm.form, socialProviderRegistry, counts))
    } recover {
      case _ : Throwable => Ok(views.html.accumulation_form(request.identity,AccumulationForm.form, socialProviderRegistry, (0L,0L,0L,0L)))
    }    
  }  
}