package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import scala.concurrent.duration._
import scala.concurrent._
import models.Goal
import slick.driver.PostgresDriver.api._

class GoalDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  var dao: GoalDAO = null

  before {
    dao = new GoalDAOImpl()
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
}