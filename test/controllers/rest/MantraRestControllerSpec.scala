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

    val mantra = Mantra(None, "name", "description", "http://url", 2015, 8, 19)
    (service.findAll _).expects().returning(Future { Seq(mantra) })

    val f = controller.index(request)

    val result = Await.result(f, Duration(5, SECONDS))

    val body = contentAsString(f)
    body shouldBe ("{\"status\":\"OK\",\"message\":[{\"id\":null,\"name\":\"name\",\"description\":\"description\",\"imgUrl\":\"http://url\",\"year\":2015,\"month\":8,\"day\":19}]}")
    result.header.status shouldBe (OK)
  }
}