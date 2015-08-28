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
import scala.concurrent._
import scala.concurrent.duration._
import java.util.UUID
import java.util.Calendar
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import modules.RestEnvironment
import play.api.mvc.{ Action, BodyParsers }
import play.api.libs.json._

class MantraRestControllerSpec extends ControllerSpec with BeforeAndAfter {
  val service: MantraService = mock[MantraService]
  var controller: MantraRestController = null
  val injector = new GuiceInjectorBuilder()
    .bindings(Seq(bind[MantraService].toInstance(service),
      bind[RestEnvironment].toInstance(RestEnvironment(env)),
      bind[play.api.i18n.MessagesApi].toInstance(msgApi)))
    .injector

  before {
    controller = injector.instanceOf[MantraRestController]
  }

  after {
  }

  "Index" should "return list of all Mantras in json" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    val mantra = Mantra(Some(9), "name", "description", "http://url", 2015, 8, 19)
    (service.findAll _).expects().returning(Future { Seq(mantra) })

    val f = controller.index(request)

    val result = Await.result(f, Duration(5, SECONDS))

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsArray(Seq(JsObject(Seq("id" -> JsNumber(9),
        "name" -> JsString("name"),
        "description" -> JsString("description"),
        "imgUrl" -> JsString("http://url"),
        "year" -> JsNumber(2015),
        "month" -> JsNumber(8),
        "day" -> JsNumber(19)))))))
    result.header.status shouldBe (OK)
  }

  it should "return error message if unable to find mantra" in {
    val request = FakeRequest() withAuthenticator (identity.loginInfo)

    (service.findAll _).expects().returning(Future { throw new IllegalArgumentException("Something bad happened") })

    val future = controller.index(request)

    val result = Await.result(future, Duration(5, SECONDS))

    val body = contentAsJson(future)

    body shouldBe JsObject(Seq("status" -> JsString("KO"),
      "message" -> JsString("Unable to find mantra. Something bad happened")))

    result.header.status shouldBe (500)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.index(_))
  }

  "Find mantra by id" should "return mantra if exists" in {
    val request = FakeRequest("GET", "/api/mantra/9").withAuthenticator(identity.loginInfo)
    val mantra = Mantra(Some(9), "name", "description", "http://bbc.co.uk", 2015, 8, 19)

    (service.find _).expects(9L).returning(Future { mantra })

    val future = controller.find(9L)(request)
    val result = await { future }

    val body = contentAsJson(future)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsObject(Seq("id" -> JsNumber(9),
        "name" -> JsString("name"),
        "description" -> JsString("description"),
        "imgUrl" -> JsString("http://bbc.co.uk"),
        "year" -> JsNumber(2015),
        "month" -> JsNumber(8),
        "day" -> JsNumber(19)))))

    result.header.status shouldBe (OK)
  }

  it should "return error message if unable to find requested mantra" in {
    val request = FakeRequest("GET", "/api/mantra/9") withAuthenticator (identity.loginInfo)

    (service.find _).expects(9L).returning(Future { throw new IllegalArgumentException("Something bad happened") })

    val future = controller.find(9L)(request)
    val result = await { future }

    val body = contentAsJson(future)

    body shouldBe JsObject(Seq("status" -> JsString("KO"),
      "message" -> JsString("Unable to find mantra. Something bad happened")))

    result.header.status shouldBe (500)
  }

  it should "return error message if not authenticated" in {
    val request = FakeRequest("GET", "/api/mantra/9") withAuthenticator (LoginInfo("facebook", "someone.else@gmail.com"))

    val future = controller.find(9L)(request)
    val result = await { future }

    val body = contentAsJson(future)
    body shouldBe JsObject(Seq("status" -> JsString("KO"),
      "message" -> JsString("You are not logged! Login!")))
    result.header.status shouldBe (401)
  }

  /*"Save" should "persist mantra if valid" in {
    val json = s"""{"id": null,
        |  "name": "Some Name",
        |  "description": "Some description",
        |  "imgUrl": "http://bbc.co.uk",
        |  "year" : 2015,
        |  "month" : 8,
        |  "day" : 16
        |}""".stripMargin
    val jsonBody = Json.parse(json)
    var req = FakeRequest().withJsonBody(jsonBody).withAuthenticator(identity.loginInfo)

    val future = controller.save()(req).run
    val result = await { future }

    val body = contentAsJson(future)
    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsString("Mantra 'Some Name' saved with id '1'.")))

    result.header.status shouldBe (OK)
  }*/
}