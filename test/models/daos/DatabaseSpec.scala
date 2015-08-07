import collection.mutable.Stack
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

class DatabaseSpec extends PlaySpec {
  "A Stack" must {
    "pop values in last-in-first-out order" in {
      val app = FakeApplication()
      var v = -1
      try {
        Play.start(app)
        val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("test")(app)  
        import dbConfig.driver.api._
        val theDB = dbConfig.db 
        val q = new AccumulationDAOImpl().slickAccumulations.length
        v = Await.result(theDB.run(q.result), Duration(1000, MILLISECONDS))

      } finally {
        Play.stop(app)
      }
      v mustBe 1
    }
  }

}