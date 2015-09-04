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
import com.mohiva.play.silhouette.api.util.{ Credentials }
import com.mohiva.play.silhouette.impl.exceptions._

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

  "Sign in" should "authenticate valid user pre-registered" in {
    val json = s"""{
    |  "identifier" : "apollonia.vanova@watchmen.com",
    |  "password" : "password"
    |}""".stripMargin
    val jsonBody = Json.parse(json)
    val fr = FakeRequest().withBody(jsonBody)
    val li = LoginInfo("facebook", "apollonia.vanova@watchmen.com")
    val user = User(UUID.randomUUID(), li, Some("apollonia"), Some("Vanova"), Some(""), Some("apollonia.vanova@watchmen.com"), None)

    (provider.authenticate _).expects(Credentials("apollonia.vanova@watchmen.com", "password")).returning(Future { li })
    (service.retrieve _).expects(li).returning(Future { Some(user) })

    val future = controller.authenticate()(fr)
    val result = await { future }

    result.header.status shouldBe (OK)
  }

  it should "return BadRequest if user is unknown" in {
    val json = s"""{
    |  "identifier" : "apollonia.vanova@watchmen.com",
    |  "password" : "password"
    |}""".stripMargin
    val jsonBody = Json.parse(json)
    val fr = FakeRequest().withBody(jsonBody)
    val li = LoginInfo("facebook", "apollonia.vanova@watchmen.com")

    (provider.authenticate _).expects(Credentials("apollonia.vanova@watchmen.com", "password")).returning(Future { li })
    (service.retrieve _).expects(li).returning(Future { None })

    val future = controller.authenticate()(fr)
    val result = await { future }

    result.header.status shouldBe (400)
    contentAsJson(future) shouldBe Json.obj("status" -> "KO", "message" -> "No account is associated with provided details. Please sign up first via web site.")
  }

  it should "return BadRequest if password is incorrect" in {
    val json = s"""{
    |  "identifier" : "apollonia.vanova@watchmen.com",
    |  "password" : "password"
    |}""".stripMargin
    val jsonBody = Json.parse(json)
    val fr = FakeRequest().withBody(jsonBody)

    (provider.authenticate _).expects(Credentials("apollonia.vanova@watchmen.com", "password")).returning(Future { throw new InvalidPasswordException("") })

    val future = controller.authenticate()(fr)
    val result = await { future }

    result.header.status shouldBe (400)
    contentAsJson(future) shouldBe Json.obj("status" -> "KO", "message" -> "Invalid password.")
  }
}