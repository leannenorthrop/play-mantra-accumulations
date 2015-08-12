package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import scala.concurrent.duration._
import scala.concurrent._
import models.Gathering
import models.Goal
import slick.driver.PostgresDriver.api._
import java.util.UUID
import java.util.Calendar

class GatheringsDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  var dao: GatheringDAO = null
  var goalDAO: GoalDAO = null

  before {
    dao = new GatheringDAOImpl()
    goalDAO = new GoalDAOImpl()
    Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
    Await.result(db.run(sqlu"delete from goals"), Duration(2000, MILLISECONDS))
  }

  after {
    //Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
  }

  "Saving a new non-existant Gathering" should "save and return Gathering with the primary key" taggedAs (DbTest) in {
    val gathering = Gathering(None, UUID.randomUUID(), "A gathering", "dedicated to all", false, false, 2015, 8, 12)

    whenReady(dao.save(gathering)) { updatedGathering =>
      val id: Option[Long] = updatedGathering.id
      id.value should be >= 1L
    }
  }

  "Saving an existing Gathering" should "save and return Gathering with the primary key" taggedAs (DbTest) in {
    val gathering = Gathering(None, UUID.randomUUID(), "A gathering", "dedicated to all", false, false, 2015, 8, 12)

    whenReady(dao.save(gathering)) { updatedGathering =>
      val id: Option[Long] = updatedGathering.id
      val idValue = id.value

      val accumulation2 = updatedGathering.copy(name = "updated name")
      whenReady(dao.save(accumulation2)) { updatedGathering2 =>
        val id2: Option[Long] = updatedGathering2.id
        assert(id2.value === idValue)
      }
    }
  }

  "Finding Gatherings by mantra" should "only return Gatherings related to the mantra" taggedAs (DbTest) in {
    val f = for {
      g1 <- dao.save(Gathering(None, UUID.randomUUID(), "A gathering 1", "dedicated to all", false, false, 2015, 8, 12))
      g2 <- dao.save(Gathering(None, UUID.randomUUID(), "A gathering 2", "dedicated to all", false, false, 2015, 8, 12))
      g3 <- dao.save(Gathering(None, UUID.randomUUID(), "A gathering 3", "dedicated to all", false, false, 2015, 8, 12))
      g4 <- dao.save(Gathering(None, UUID.randomUUID(), "A gathering 4", "dedicated to all", false, false, 2015, 8, 12))
      g5 <- dao.save(Gathering(None, UUID.randomUUID(), "A gathering 5", "dedicated to all", false, false, 2015, 8, 12))
    } yield (g1, g2, g3, g4, g5)

    whenReady(f) { gatherings =>
      val f2 = for {
        _ <- goalDAO.save(Goal(gatherings._1.id.get, 2, 10000, false))
        _ <- goalDAO.save(Goal(gatherings._1.id.get, 3, 11000, false))
        _ <- goalDAO.save(Goal(gatherings._1.id.get, 4, 12000, false))
        _ <- goalDAO.save(Goal(gatherings._2.id.get, 3, 13000, false))
        _ <- goalDAO.save(Goal(gatherings._3.id.get, 3, 14000, false))
        _ <- goalDAO.save(Goal(gatherings._4.id.get, 3, 15000, false))
      } yield ()

      whenReady(f2) { _ =>
        whenReady(dao.find(3)) { foundGatherings =>
          foundGatherings.length shouldBe (4)
        }
      }
    }
  }
}