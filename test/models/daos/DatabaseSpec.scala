import org.scalatest._
import org.scalatestplus.play._

import play.api._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.language.existentials
import scala.concurrent.duration._
import scala.concurrent._
import models.daos._

trait App extends BeforeAndAfterAll { this: Suite =>

  val app = FakeApplication()
  val db : slick.jdbc.JdbcBackend#DatabaseDef = null

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

class DatabaseSpec extends FlatSpec with App with MustMatchers with OptionValues {
    "1" should "be easy" in {
      val q = new AccumulationDAOImpl().slickAccumulations.length
      val v = Await.result(db.run(q.result), Duration(1000, MILLISECONDS))
      v mustBe 1
    }

    "2" should "b" in {
      val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("test")(app)  
      import dbConfig.driver.api._
      val theDB = dbConfig.db 

      val q = new AccumulationDAOImpl().slickAccumulations.length
      val v = Await.result(db.run(q.result), Duration(1000, MILLISECONDS))
      v mustBe 1
    }    
}