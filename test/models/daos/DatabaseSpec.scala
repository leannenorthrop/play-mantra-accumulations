package models.daos

import org.scalatest._
import play.api._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.language.existentials

trait Database extends BeforeAndAfterAll { this: Suite =>
  val app = FakeApplication()
  var db : slick.jdbc.JdbcBackend#DatabaseDef = null

  override def beforeAll() {
    Play.start(app)
    val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("test")(app)  
    import dbConfig.driver.api._
    db = dbConfig.db 
    super.beforeAll() // To be stackable, must call super.beforeEach
  }

  override def afterAll() {
    try super.afterAll() // To be stackable, must call super.afterEach
    finally Play.stop(app)
  }
}

abstract class DatabaseSpec extends FlatSpec with Database {   
}