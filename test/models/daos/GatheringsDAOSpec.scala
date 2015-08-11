package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import scala.concurrent.duration._
import scala.concurrent._
import models.Gathering
import slick.driver.PostgresDriver.api._
import java.util.UUID
import java.util.Calendar

class GatheringsDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
    var dao :GatheringDAO = null

	before {
    	dao = new GatheringDAOImpl()
		Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
	}

	after {
		//Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
	}

    "Saving a new non-existant Gathering" should "save and return Gathering with the primary key" taggedAs(DbTest) in {
      val gathering = Gathering(None, UUID.randomUUID(), "A gathering", "dedicated to all", false, false, 2015, 8, 12)

      whenReady(dao.save(gathering)) { updatedGathering =>
      	val id : Option[Long] = updatedGathering.id
      	id.value should be >= 1L
      }
    }	

    "Saving an existing Gathering" should "save and return Gathering with the primary key" taggedAs(DbTest) in {
      val gathering = Gathering(None, UUID.randomUUID(), "A gathering", "dedicated to all", false, false, 2015, 8, 12)

      whenReady(dao.save(gathering)) { updatedGathering =>
      	val id : Option[Long] = updatedGathering.id
      	val idValue = id.value

      	val accumulation2 = updatedGathering.copy(name="updated name")
      	whenReady(dao.save(accumulation2)) { updatedGathering2 =>
      		val id2 : Option[Long] = updatedGathering2.id
      		assert(id2.value === idValue)
      	}
      }     
    }	
}