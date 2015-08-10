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

class AccumulationDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
	before {
		Await.result(db.run(sqlu"delete from accumulations"), Duration(1000, MILLISECONDS))
	}

	after {
		//Await.result(db.run(sqlu"delete from accumulations"), Duration(1000, MILLISECONDS))
	}

    "Saving a new non-existant Accumulation" should "save and return Accumulation with the primary key" taggedAs(DbTest) in {
      
      val dao = new AccumulationDAOImpl()
      val accumulation = Accumulation(None, 2015, 8, 7, 100, 1, UUID.randomUUID(), -1)

      whenReady(dao.save(accumulation)) { updatedAccumulation =>
      	val id : Option[Long] = updatedAccumulation.get.id
      	id.value should be >= 1L
      }
    }	

    "Saving an existing Accumulation" should "save and return Accumulation with the primary key" taggedAs(DbTest) in {
      val uid = UUID.randomUUID()
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

    "Find Accumulation for today" should "return None if no entry exists for today" in {
    	val uid = UUID.randomUUID()
    	val dao = new AccumulationDAOImpl()

    	whenReady(dao.findForToday(uid, -1L, 1L)) { acc =>
    		assert(acc === None)
    	}
    }	

    it should "return Accumulation if exists for today" in {
   	  val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)
      val uid = UUID.randomUUID()
      val mantraId = 1l
      val gatheringId = -1L
      val count = 200L
      val dao = new AccumulationDAOImpl()
      val accumulation = Accumulation(None, year, month, day, count, mantraId, uid, gatheringId)

      whenReady(dao.save(accumulation)) { a =>
      	whenReady(dao.findForToday(uid, gatheringId, mantraId)) { acc =>
    		acc match {
    			case Some(a) => 
    				assert(a.year == year)
    				assert(a.month == month)
    				assert(a.day == day)
    				assert(a.count == count)
    				assert(a.mantraId == mantraId)
    				assert(a.userId == uid)
    				assert(a.gatheringId == gatheringId)
    			case None => fail("Didn't return Accumulation for today")
    		}
    	}
      }     	
    }    
}