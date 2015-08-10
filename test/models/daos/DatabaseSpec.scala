package models.daos

import org.scalatest._
import play.api._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.language.existentials
import play.api.db.evolutions._
import org.scalatest.concurrent.{ ScalaFutures, Futures, PatienceConfiguration }
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.Tag

object DbTest extends Tag("org.northrop.leanne.play.mantra.tags.DbTest")

trait Database extends BeforeAndAfterAll with ScalaFutures { this: Suite =>
  val app = FakeApplication()
  var db : slick.jdbc.JdbcBackend#DatabaseDef = null

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  override def beforeAll() {
    Play.start(app)
    val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("test")(app)  
    import dbConfig.driver.api._
    db = dbConfig.db

    //Evolutions.applyEvolutions(db) 
    super.beforeAll() // To be stackable, must call super.beforeEach
  }

  override def afterAll() {
    try super.afterAll() // To be stackable, must call super.afterEach
    //Evolutions.cleanupEvolutions(database)
    finally Play.stop(app)
  }
}

abstract class DatabaseSpec extends FlatSpec with Database {   
}