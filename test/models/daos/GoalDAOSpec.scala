package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import scala.concurrent.duration._
import scala.concurrent._
import models.Goal
import models.Gathering
import java.util.UUID
import slick.driver.PostgresDriver.api._

class GoalDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  var dao: GoalDAO = null

  before {
    dao = new GoalDAOImpl()
    Await.result(db.run(sqlu"delete from gatherings"), Duration(2000, MILLISECONDS))
    Await.result(db.run(sqlu"delete from goals"), Duration(2000, MILLISECONDS))
  }

  after {
    //Await.result(db.run(sqlu"delete from goals"), Duration(2000, MILLISECONDS))
  }

  "Saving a new non-existant Goal" should "save" taggedAs (DbTest) in {
    val goal = Goal(1, 2, 100, false)

    whenReady(dao.save(goal)) { savedGoal =>
      val length = Await.result(db.run(sql"select count(*) from goals".as[Int]), Duration(2000, MILLISECONDS)).head
      length shouldBe (1)
    }
  }

  "Saving an existing Goal" should "update" taggedAs (DbTest) in {
    val goal = Goal(1, 2, 100, false)

    whenReady(dao.save(goal)) { savedGoal =>
      val length = Await.result(db.run(sql"select count(*) from goals".as[Int]), Duration(2000, MILLISECONDS)).head
      length shouldBe (1)

      val goalUpdated = savedGoal.copy(goal = 500)
      whenReady(dao.save(goalUpdated)) { savedGoal2 =>
        val length2 = Await.result(db.run(sql"select count(*) from goals".as[Int]), Duration(2000, MILLISECONDS)).head
        length2 shouldBe (1)
      }
    }
  }

  "Finding Goals for a gathering" should "only return goals related to the gathering" taggedAs (DbTest) in {
    val gatheringDAO = new GatheringDAOImpl()
    val f = for {
      g1 <- gatheringDAO.save(Gathering(None, UUID.randomUUID(), "A gathering 1", "dedicated to all", false, false, 2015, 8, 12))
      g2 <- gatheringDAO.save(Gathering(None, UUID.randomUUID(), "A gathering 2", "dedicated to all", false, false, 2015, 8, 12))
    } yield (g1, g2)

    whenReady(f) { gatherings =>
      val f2 = for {
        _ <- dao.save(Goal(gatherings._1.id.get, 2, 10000, false))
        _ <- dao.save(Goal(gatherings._1.id.get, 3, 11000, false))
        _ <- dao.save(Goal(gatherings._1.id.get, 4, 12000, false))
        _ <- dao.save(Goal(gatherings._2.id.get, 3, 13000, false))
        _ <- dao.save(Goal(gatherings._2.id.get, 3, 14000, false))
        _ <- dao.save(Goal(gatherings._2.id.get, 3, 15000, false))
      } yield ()

      whenReady(f2) { _ =>
        whenReady(dao.find(gatherings._1.id.get)) { foundGoals =>
          foundGoals.length shouldBe (3)
        }
      }
    }
  }
}