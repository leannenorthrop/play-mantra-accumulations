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
import java.text.SimpleDateFormat
import java.util.Calendar

class GatheringsDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  var dao: GatheringDAO = null

  before {
    dao = new GatheringDAOImpl()

    val f = for {
      _ <- db.run(sqlu"delete from goals")
      _ <- db.run(sqlu"delete from accumulations")
      _ <- db.run(sqlu"delete from gatherings")
      _ <- db.run(sqlu"delete from mantra")
    } yield ()

    whenReady(f) { _ =>
    }
  }

  after {
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

  "Finding Gatherings" should "return empty list when none are found" taggedAs (DbTest) in {
    whenReady(dao.find()) { found =>
      found.length shouldBe (0)
    }
  }

  it should "only find non-archived gatherings" taggedAs (DbTest) in {
    cleanInsert("GatheringsDAOSpec")
    whenReady(dao.find()) { found =>
      found.length shouldBe (6)
    }
  }

  "Finding Gatherings by mantra" should "only return Gatherings related to the mantra" taggedAs (DbTest) in {
    cleanInsert("GatheringsDAOSpec")

    whenReady(dao.find(3L)) { foundGatherings =>
      foundGatherings.length shouldBe (4)
    }
  }

  "Exists" should "return false if no gathering exists with given name" taggedAs (DbTest) in {
    whenReady(dao.exists("jdjaljlj")) { result =>
      result shouldBe (false)
    }
  }

  it should "return true if gathering exists with given name" taggedAs (DbTest) in {
    val name = "A gathering"
    val gathering = Gathering(None, UUID.randomUUID(), name, "dedicated to all", false, false, 2015, 8, 12)

    whenReady(dao.save(gathering)) { updatedGathering =>
      whenReady(dao.exists(name)) { result =>
        result shouldBe (true)
      }
    }
  }

  "Deleting an existant Gathering" should "set is achieved to 1" taggedAs (DbTest) in {
    cleanInsert("GatheringsDAOSpec")
    whenReady(dao.delete(1L)) { isDeleted =>
      isDeleted shouldBe (true)
    }
  }

  it should "not be available in find results" taggedAs (DbTest) in {
    cleanInsert("GatheringsDAOSpec")
    whenReady(dao.delete(1L)) { isDeleted =>
      whenReady(dao.find(2L)) { found =>
        found.length shouldBe (0)
      }
    }
  }

  it should "rename mantra to include date archived" taggedAs (DbTest) in {
    cleanInsert("GatheringsDAOSpec")

    whenReady(dao.delete(1L)) { result =>
      result shouldBe (true)
      val f = for {
        name <- db.run(sql"select name from gatherings where id = 1".as[String])
      } yield name

      whenReady(f) { name =>
        val f = new SimpleDateFormat("dd-MM-YYYY kk:hh")
        val nowStr = f.format(Calendar.getInstance.getTime)
        assert(name.head.contains(nowStr))
      }
    }
  }
}