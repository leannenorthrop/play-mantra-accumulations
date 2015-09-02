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
import models.{ Mantra, User, Gathering, Goal }
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

class GatheringRestControllerSpec extends ControllerSpec with BeforeAndAfter {
  val mantraService: MantraService = mock[MantraService]
  val gatheringService: GatheringService = mock[GatheringService]
  val accumulationService: AccumulationService = mock[AccumulationService]
  var controller: GatheringRestController = null
  val injector = new GuiceInjectorBuilder()
    .bindings(Seq(bind[MantraService].toInstance(mantraService),
      bind[GatheringService].toInstance(gatheringService),
      bind[AccumulationService].toInstance(accumulationService),
      bind[RestEnvironment].toInstance(RestEnvironment(env)),
      bind[play.api.i18n.MessagesApi].toInstance(msgApi)))
    .injector

  before {
    controller = injector.instanceOf[GatheringRestController]
  }

  after {
  }

  "Index" should "return list of all Gatherings in json" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    val uuid = UUID.randomUUID()
    val gathering = Gathering(Some(9), uuid, "Test Gathering", "dedication", false, false, 2015, 8, 30)
    (gatheringService.find _).expects().returning(Future { Seq(gathering) })

    val f = controller.index(request)

    val result = await { f }

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsArray(Seq(JsObject(Seq("id" -> JsNumber(9),
        "owner" -> JsString(uuid.toString()),
        "name" -> JsString("Test Gathering"),
        "dedication" -> JsString("dedication"),
        "isAchieved" -> JsBoolean(false),
        "isPrivate" -> JsBoolean(false),
        "year" -> JsNumber(2015),
        "month" -> JsNumber(8),
        "day" -> JsNumber(30)))))))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.index(_))
  }

  it should "return error message if unable to find gatherings" in {
    (gatheringService.find _).expects().returning(Future { throw new IllegalArgumentException("Something bad happened") })

    testInternalServerError("Unable to find gatherings. Something bad happened", controller.index(_))
  }

  "Find by mantra" should "return list of all Gatherings in json" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    val uuid = UUID.randomUUID()
    val gathering = Gathering(Some(9), uuid, "Test Gathering", "dedication", false, false, 2015, 8, 30)
    (gatheringService.findByMantra _).expects(12L).returning(Future { Seq(gathering) })

    val f = controller.find(12L)(request)

    val result = await { f }

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsArray(Seq(JsObject(Seq("id" -> JsNumber(9),
        "owner" -> JsString(uuid.toString()),
        "name" -> JsString("Test Gathering"),
        "dedication" -> JsString("dedication"),
        "isAchieved" -> JsBoolean(false),
        "isPrivate" -> JsBoolean(false),
        "year" -> JsNumber(2015),
        "month" -> JsNumber(8),
        "day" -> JsNumber(30)))))))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.find(12L)(_))
  }

  it should "return error message if unable to find gatherings" in {
    (gatheringService.findByMantra _).expects(12L).returning(Future { throw new IllegalArgumentException("Something bad happened") })

    testInternalServerError("Unable to find gatherings. Something bad happened", controller.find(12L)(_))
  }

  "Delete" should "delegate to gathering service" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    (gatheringService.delete _).expects(12L).returning(Future { true })

    val f = controller.delete(12L)(request)

    val result = await { f }

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsString("Gathering successfully archived")))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.delete(12L)(_))
  }

  it should "return error message if unable to find gatherings" in {
    (gatheringService.delete _).expects(12L).returning(Future { throw new IllegalArgumentException("Something bad happened") })

    testInternalServerError("Unable to delete gathering. Something bad happened", controller.delete(12L)(_))
  }

  "Remove goal" should "delegate to gathering service" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    (gatheringService.remove _).expects(1L, 2L).returning(Future { true })

    val f = controller.removeGoal(1L, 2L)(request)

    val result = await { f }

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsString("Gathering goal was deleted.")))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.removeGoal(1L, 2L)(_))
  }

  it should "return error message if unable to find gatherings" in {
    (gatheringService.remove _).expects(1L, 2L).returning(Future { throw new IllegalArgumentException("Something bad happened") })

    testInternalServerError("Database error: Trying to delete gathering goal. Something bad happened", controller.removeGoal(1L, 2L)(_))
  }

  "Find goal" should "delegate to gathering service" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    (gatheringService.findGoal _).expects(1L, 2L).returning(Future { Goal(1L, 2L, 30000L, false) })

    val f = controller.findGoal(1L, 2L)(request)

    val result = await { f }

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsObject(Seq("gatheringId" -> JsNumber(1L),
        "mantraId" -> JsNumber(2L),
        "goal" -> JsNumber(30000L),
        "isAchieved" -> JsBoolean(false)))))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.findGoal(1L, 2L)(_))
  }

  it should "return error message if unable to find gatherings" in {
    (gatheringService.findGoal _).expects(1L, 2L).returning(Future { throw new IllegalArgumentException("Something bad happened") })

    testInternalServerError("Unable to find goal: Something bad happened", controller.findGoal(1L, 2L)(_))
  }

  "Find gathering" should "delegate to gathering service" in {
    val request = FakeRequest().withAuthenticator(identity.loginInfo)

    val uuid = UUID.randomUUID()
    val gathering = Gathering(Some(9), uuid, "Test Gathering", "dedication", false, false, 2015, 8, 30)
    (gatheringService.find(_: Long, _: Long)).expects(1L, 2L).returning(Future { gathering })

    val f = controller.findGathering(1L, 2L)(request)

    val result = await { f }

    val body = contentAsJson(f)

    body shouldBe JsObject(Seq("status" -> JsString("OK"),
      "message" -> JsObject(Seq("id" -> JsNumber(9),
        "owner" -> JsString(uuid.toString()),
        "name" -> JsString("Test Gathering"),
        "dedication" -> JsString("dedication"),
        "isAchieved" -> JsBoolean(false),
        "isPrivate" -> JsBoolean(false),
        "year" -> JsNumber(2015),
        "month" -> JsNumber(8),
        "day" -> JsNumber(30)))))
    result.header.status shouldBe (OK)
  }

  it should "return error message if not authenticated" in {
    testSecured(controller.findGathering(1L, 2L)(_))
  }

  it should "return error message if unable to find gatherings" in {
    (gatheringService.find(_: Long, _: Long)).expects(1L, 2L).returning(Future { throw new IllegalArgumentException("Something bad happened") })

    testInternalServerError("Unable to find gathering: Something bad happened", controller.findGathering(1L, 2L)(_))
  }

  "Save gathering" should "delegate to gathering service" in {
    val uuid = UUID.randomUUID()
    val gathering = Gathering(None, uuid, "Some name", "Some dedication", false, false, 2015, 8, 16)
    val json = s"""{"id": null,
        |  "owner": "${uuid.toString()}",
        |  "name": "Some name",
        |  "dedication": "Some dedication",
        |  "isAchieved": false,
        |  "isPrivate": false,
        |  "year" : 2015,
        |  "month" : 8,
        |  "day" : 16
        |}""".stripMargin
    val jsonBody = Json.parse(json)

    val fr = FakeRequest()
      .withAuthenticator(identity.loginInfo)
      .withBody(jsonBody)

    (gatheringService.save _).expects(gathering).returning(Future { gathering.copy(id = Some(9)) })

    val future = controller.save()(fr)
    val result = await { future }

    result.header.status shouldBe (OK)
    contentAsJson(future) shouldBe Json.obj("status" -> "OK", "message" -> "Gathering 'Some name' saved with id '9'.")
  }

  it should "return error message if not authenticated" in {
    testSecuredJson(controller.save()(_))
  }

  it should "return error messages if invalid json provided" in {
    val uuid = UUID.randomUUID()
    val json = s"""{"id": null,
        |  "owner": "${uuid.toString()}",
        |  "name": "S",
        |  "dedication": "S",
        |  "isAchieved": false,
        |  "year" : 2015,
        |  "month" : 8,
        |  "day" : 16
        |}""".stripMargin
    val jsonBody = Json.parse(json)

    val fr = FakeRequest()
      .withAuthenticator(identity.loginInfo)
      .withBody(jsonBody)

    val future = controller.save()(fr)
    val result = await { future }

    result.header.status shouldBe (400)
    contentAsJson(future) shouldBe Json.obj("status" -> "KO", "message" -> "JSON error", "errors" -> JsArray(Seq(JsString("/isPrivate: Is missing"), JsString("/dedication: Minimum length is 2"), JsString("/name: Minimum length is 2"))))
  }
}