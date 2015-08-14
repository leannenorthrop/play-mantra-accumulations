package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import scala.concurrent.duration._
import scala.concurrent._
import models.Goal
import models.Gathering
import models.Mantra
import java.util.UUID
import slick.driver.PostgresDriver.api._

class GoalDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  var dao: GoalDAO = null

  before {
    dao = new GoalDAOImpl()
    val f = for {
      _ <- db.run(sqlu"delete from goals")
      _ <- db.run(sqlu"delete from accumulations")
      _ <- db.run(sqlu"delete from gatherings")
      _ <- db.run(sqlu"delete from mantra")
    } yield ()

    whenReady(f) { _ =>
      cleanInsert("GoalDAOSpec")
    }
  }

  after {
  }

  "Saving a new non-existant Goal" should "save" taggedAs (DbTest) in {
    val goal = Goal(1L, 1L, 100, false)

    whenReady(dao.save(goal)) { savedGoal =>
      val length = Await.result(db.run(sql"select count(*) from goals".as[Int]), Duration(2000, MILLISECONDS)).head
      length shouldBe (1)
    }
  }

  "Saving an existing Goal" should "update" taggedAs (DbTest) in {
    val goal = Goal(1L, 1L, 100, false)

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
    val f = for {
      _ <- dao.save(Goal(1L, 1L, 10000, false))
      _ <- dao.save(Goal(1L, 3L, 11000, false))
      _ <- dao.save(Goal(1L, 4L, 12000, false))
      _ <- dao.save(Goal(2L, 3L, 13000, false))
      _ <- dao.save(Goal(2L, 3L, 14000, false))
      _ <- dao.save(Goal(2L, 3L, 15000, false))
    } yield ()

    whenReady(f) { _ =>
      whenReady(dao.find(1L)) { foundGoals =>
        foundGoals.length shouldBe (3)
      }
    }
  }

  "Deleting a non-existant goal" should "return false" taggedAs (DbTest) in {
    whenReady(dao.delete(-1L, -2L)) { result =>
      result shouldBe (false)
    }
  }

  it should "return true when deleting existing goal" taggedAs (DbTest) in {
    val goal = Goal(1L, 1L, 100, false)

    whenReady(dao.save(goal)) { savedGoal =>
      whenReady(dao.delete(1L, 1L)) { result =>
        result shouldBe (true)
      }
    }
  }
}