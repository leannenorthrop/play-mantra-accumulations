package controllers.rest

import com.mohiva.play.silhouette.test._
import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatestplus.play._
import org.scalamock.scalatest.MockFactory

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import models.{ Mantra, User }
import models.services._
import controllers.rest._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import com.mohiva.play.silhouette.impl.providers._
import scala.concurrent._
import scala.concurrent.duration._
import java.util.UUID
import java.util.Calendar
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import modules.RestEnvironment
import play.api.mvc.{ Action, BodyParsers }
import play.api.libs.json._

class MockableCredentialsProvider extends CredentialsProvider(null, null, null)

class CredentialsAuthRestControllerSpec extends ControllerSpec with BeforeAndAfter {
  val provider: CredentialsProvider = mock[MockableCredentialsProvider]
  val service: UserService = mock[UserService]
  var controller: CredentialsAuthRestController = null
  val injector = new GuiceInjectorBuilder()
    .bindings(Seq(bind[UserService].toInstance(service),
      bind[RestEnvironment].toInstance(RestEnvironment(env)),
      bind[play.api.i18n.MessagesApi].toInstance(msgApi),
      bind[CredentialsProvider].toInstance(provider)))
    .injector

  before {
    controller = injector.instanceOf[CredentialsAuthRestController]
  }

  after {
  }

  "Sign out" should "log out a logged in user" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)
    val f = controller.signOut(request)

    val result = await { f }

    val body = contentAsJson(f)
    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsString("Token expired.")))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    val request = FakeRequest() withAuthenticator (LoginInfo("facebook", "someone.else@gmail.com"))

    val future = controller.signOut(request)
    val result = await { future }

    val body = contentAsJson(future)
    body shouldBe JsObject(Seq("status" -> JsString("KO"),
      "message" -> JsString("Not logged in.")))
    result.header.status shouldBe (401)
  }
}