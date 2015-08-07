package models.daos

import org.scalatest._
import scala.concurrent.duration._
import scala.concurrent._
import models.daos._
import models._
import slick.driver.PostgresDriver.api._

class AccumulationDAOSpec extends DatabaseSpec with Matchers with OptionValues {
    "Accumulation DAO save new Accumulation" should "save return Accumulation with primary key" in {
      
      val dao = new AccumulationDAOImpl()
      val accumulation = Accumulation(None, 2015, 8, 7, 200, 1, java.util.UUID.randomUUID(), -1)

      val q = dao.slickAccumulations.length
      val tableLength = Await.result(db.run(q.result), Duration(1000, MILLISECONDS))

      val updatedAccumulation : Option[Accumulation] = dao.save(accumulation)

	  val newTableLength = Await.result(db.run(q.result), Duration(1000, MILLISECONDS))  	      
      newTableLength shouldBe (1 + tableLength)

      val id : Option[Long] = updatedAccumulation.get.id
      id.value should be > 1L
    }	
}