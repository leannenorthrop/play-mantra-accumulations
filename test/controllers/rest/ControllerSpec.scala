package controllers.rest

import com.mohiva.play.silhouette.test._
import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatestplus.play._
import org.scalamock.scalatest.MockFactory

import play.api._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import modules.RestEnvironment
import java.util.UUID
import models.User

class ControllerSpec extends FlatSpec with ScalaFutures with MockFactory with Matchers with BeforeAndAfterAll {
  val app = FakeApplication()
  val identity = User(UUID.randomUUID(),
    LoginInfo("facebook", "apollonia.vanova@watchmen.com"),
    Some("apollonia"),
    Some("noone"),
    Some("apollonia noone"),
    Some("apollonia.vanova@watchmen.com"),
    None)

  implicit val env = FakeEnvironment[User, JWTAuthenticator](Seq(identity.loginInfo -> identity))
  val msgApi = new play.api.i18n.DefaultMessagesApi(play.api.Environment(app.path, app.classloader, app.mode), app.configuration, new play.api.i18n.DefaultLangs(app.configuration))

  override def beforeAll() {
    Play.start(app)
    super.beforeAll() // To be stackable, must call super.beforeEach
  }

  override def afterAll() {
    try {
      super.afterAll()
    } finally Play.stop(app)
  }
}