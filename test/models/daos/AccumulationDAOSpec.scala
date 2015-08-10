package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import models.daos._
import models._
import slick.driver.PostgresDriver.api._
import scala.concurrent._
import scala.concurrent.duration._

class AccumulationDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
	before {
		Await.result(db.run(sqlu"delete from accumulations"), Duration(1000, MILLISECONDS))
	}

	after {
		Await.result(db.run(sqlu"delete from accumulations"), Duration(1000, MILLISECONDS))
	}

    "Saving a new non-existant Accumulation" should "save and return Accumulation with the primary key" taggedAs(DbTest) in {
      
      val dao = new AccumulationDAOImpl()
      val accumulation = Accumulation(None, 2015, 8, 7, 100, 1, java.util.UUID.randomUUID(), -1)

      whenReady(dao.save(accumulation)) { updatedAccumulation =>
      	val id : Option[Long] = updatedAccumulation.get.id
      	id.value should be >= 1L
      }
    }	

    "Saving an existing Accumulation" should "save and return Accumulation with the primary key" taggedAs(DbTest) in {
      val uid = java.util.UUID.randomUUID()
      val dao = new AccumulationDAOImpl()
      val accumulation = Accumulation(None, 2015, 8, 7, 200, 1, uid, -1)

      whenReady(dao.save(accumulation)) { updatedAccumulation =>
      	val id : Option[Long] = updatedAccumulation.get.id
      	val idValue = id.value

      	val accumulation2 = updatedAccumulation.get.copy(count=400)
      	whenReady(dao.save(accumulation2)) { updatedAccumulation2 =>
      		val id2 : Option[Long] = updatedAccumulation2.get.id
      		assert(id2.value === idValue)
      	} 

      }     
    }	    
}