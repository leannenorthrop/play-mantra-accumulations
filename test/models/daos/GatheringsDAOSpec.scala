package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import models.daos._
import models._
import slick.driver.PostgresDriver.api._
import scala.concurrent._
import scala.concurrent.duration._
import java.util.UUID
import java.util.Calendar

class GatheringsDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  //var dao :AccumulationDAO = null

	before {
    //dao = new AccumulationDAOImpl()
		Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
	}

	after {
		Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
	}
}