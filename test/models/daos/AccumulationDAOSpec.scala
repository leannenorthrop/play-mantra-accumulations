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
  var dao :AccumulationDAO = null

	before {
    dao = new AccumulationDAOImpl()
		Await.result(db.run(sqlu"delete from accumulations"), Duration(2000, MILLISECONDS))
	}

	after {
		Await.result(db.run(sqlu"delete from accumulations"), Duration(2000, MILLISECONDS))
	}

    "Saving a new non-existant Accumulation" should "save and return Accumulation with the primary key" taggedAs(DbTest) in {
      val accumulation = Accumulation(None, 2015, 8, 7, 100, 1, UUID.randomUUID(), -1)

      whenReady(dao.save(accumulation)) { updatedAccumulation =>
      	val id : Option[Long] = updatedAccumulation.id
      	id.value should be >= 1L
      }
    }	

    "Saving an existing Accumulation" should "save and return Accumulation with the primary key" taggedAs(DbTest) in {
      val uid = UUID.randomUUID()
      val accumulation = Accumulation(None, 2015, 8, 7, 200, 1, uid, -1)

      whenReady(dao.save(accumulation)) { updatedAccumulation =>
      	val id : Option[Long] = updatedAccumulation.id
      	val idValue = id.value

      	val accumulation2 = updatedAccumulation.copy(count=400)
      	whenReady(dao.save(accumulation2)) { updatedAccumulation2 =>
      		val id2 : Option[Long] = updatedAccumulation2.id
      		assert(id2.value === idValue)
      	} 

      }     
    }

    "Find Accumulation for today" should "throw java.util.NoSuchElementException if no entry exists for today" taggedAs(DbTest) in {
      intercept[java.util.NoSuchElementException]{
        Await.result(dao.findForToday(UUID.randomUUID(), -1L, 1L),Duration(5000, MILLISECONDS))
      }
    }	

    it should "return Accumulation if exists for today" taggedAs(DbTest) in {
   	  val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)
      val uid = UUID.randomUUID()
      val mantraId = 1l
      val gatheringId = -1L
      val count = 200L
      val accumulation = Accumulation(None, year, month, day, count, mantraId, uid, gatheringId)

      whenReady(dao.save(accumulation)) { a =>
      	whenReady(dao.findForToday(uid, gatheringId, mantraId)) { acc =>
          assert(a.year == year)
          assert(a.month == month)
          assert(a.day == day)
          assert(a.count == count)
          assert(a.mantraId == mantraId)
          assert(a.userId == uid)
          assert(a.gatheringId == gatheringId)
    	  }
      }     	
    }

    "Counts for mantra" should "return sum total for overall, year, month, and day" taggedAs(DbTest) in {
    	val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)

    	val mantraId = 1L
    	val setup = for {
    		_ <- dao.save(Accumulation(None, year, month, day, 1, mantraId, UUID.randomUUID(), -1))
    		_ <- dao.save(Accumulation(None, year, month, day+1, 2, mantraId, UUID.randomUUID(), -2))
    		_ <- dao.save(Accumulation(None, year, month, day+2, 3, mantraId, UUID.randomUUID(), -3))
    		_ <- dao.save(Accumulation(None, year, month-1, day+3, 4, mantraId, UUID.randomUUID(), -4))
    		_ <- dao.save(Accumulation(None, year-1, month, day, 5, mantraId, UUID.randomUUID(), -5))
    	} yield ("done")

      whenReady(setup) { i =>
      	whenReady(dao.counts(mantraId)(None)) { totals =>
          println(totals)
      		totals._1 shouldBe 15L
      		totals._2 shouldBe 10L
      		totals._3 shouldBe 6L
      		totals._4 shouldBe 1L
      	}
      }
    }  

    it should "return 0 for all totals if no values" taggedAs(DbTest) in {
    	whenReady(dao.counts(2378)(None)) { totals =>
    		totals._1 shouldBe 0L
    		totals._2 shouldBe 0L
    		totals._3 shouldBe 0L
    		totals._4 shouldBe 0L
    	}    	
    }  


    "Counts for mantra and gathering" should "return sum total for overall, year, month, and day" taggedAs(DbTest) in {
    	val cal = Calendar.getInstance()
    	val year = cal.get(Calendar.YEAR)
    	val month = cal.get(Calendar.MONTH) + 1
    	val day = cal.get(Calendar.DAY_OF_MONTH)
    	val gatheringId = -1L
    	val mantraId = 1L
    	val setup = for {
    		_ <- dao.save(Accumulation(None, year, month, day, 1, mantraId, UUID.randomUUID(), gatheringId))
    		_ <- dao.save(Accumulation(None, year, month, day+1, 2, mantraId, UUID.randomUUID(), gatheringId))
    		_ <- dao.save(Accumulation(None, year, month, day+2, 3, mantraId, UUID.randomUUID(), gatheringId))
    		_ <- dao.save(Accumulation(None, year, month-1, day+3, 4, mantraId, UUID.randomUUID(), gatheringId))
    		_ <- dao.save(Accumulation(None, year-1, month, day, 5, mantraId, UUID.randomUUID(), gatheringId))
    		_ <- dao.save(Accumulation(None, year, month, day, 5, mantraId, UUID.randomUUID(), -5L))
    	} yield ("done")

      whenReady(setup) { _ =>
        whenReady(dao.counts(mantraId)(Some(gatheringId))) { totals =>
          println(totals)
          totals._1 shouldBe 15L
          totals._2 shouldBe 10L
          totals._3 shouldBe 6L
          totals._4 shouldBe 1L
        }        
      }
    }  

    it should "return 0 for all totals if no values" taggedAs(DbTest) in {
    	whenReady(dao.counts(2378)(Some(-897897))) { totals =>
    		totals._1 shouldBe 0L
    		totals._2 shouldBe 0L
    		totals._3 shouldBe 0L
    		totals._4 shouldBe 0L
    	}    	
    }  
}