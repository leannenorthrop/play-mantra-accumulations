package models.services

import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import models.daos.{ GatheringDAO, GoalDAO }
import org.scalatest._
import models.{ Gathering, Goal }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import java.util.UUID

class GatheringServiceSpec extends ServiceSpec with Matchers with BeforeAndAfter {
  var service: GatheringService = null
  val dao = mock[GatheringDAO]
  val goalDao = mock[GoalDAO]

  before {
    val injector = new GuiceInjectorBuilder()
      .bindings(Seq(bind[GatheringDAO].toInstance(dao),
        bind[GoalDAO].toInstance(goalDao),
        bind[GatheringService].to[GatheringServiceImpl]))
      .injector

    service = injector.instanceOf[GatheringService]
  }

  after {
  }

  "Saving an gathering" should "delegate to dao" taggedAs (ServiceTest) in {
    val gathering = Gathering(None, UUID.randomUUID(), "A Gathering", "A Dedication", false, true, 2015, 8, 1)

    (dao.save _).expects(gathering).returning(Future { gathering })

    whenReady(service.save(gathering)) { m =>
      m shouldBe (gathering)
    }
  }

  it should "fail when exception occurs" taggedAs (ServiceTest) in {
    val gathering = Gathering(None, UUID.randomUUID(), "A Gathering", "A Dedication", false, true, 2015, 8, 1)

    (dao.save _).expects(gathering).returning(Future { throw new RuntimeException("Failed") })

    val f = service.save(gathering)

    intercept[RuntimeException] {
      Await.result(f, Duration(1000, MILLISECONDS))
    }
  }

  "Find" should "delegate to dao" taggedAs (ServiceTest) in {
    val gathering = Gathering(None, UUID.randomUUID(), "A Gathering", "A Dedication", false, true, 2015, 8, 1)
    (dao.find _).expects().returning(Future { Seq(gathering) })

    whenReady(service.find()) { found =>
      found.length shouldBe (1)
    }
  }

  "Find by mantra" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantraId = 1L
    val gathering = Gathering(None, UUID.randomUUID(), "A Gathering", "A Dedication", false, true, 2015, 8, 1)

    (dao.find(_: Long)).expects(mantraId).returning(Future { Seq(gathering) })

    whenReady(service.findByMantra(mantraId)) { found =>
      found.length shouldBe (1)
    }
  }

  "Delete" should "delegate to dao" taggedAs (ServiceTest) in {
    (dao.delete _).expects(1L).returning(Future { true })

    whenReady(service.delete(1L)) { isDeleted =>
      isDeleted shouldBe (true)
    }
  }

  "Add goal" should "save goal if it doesn't already exisit" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L
    val goal = Goal(gatheringId, mantraId, 100, false)

    (goalDao.find(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { throw new java.util.NoSuchElementException() })
    (goalDao.save _).expects(goal).returning(Future { goal })

    whenReady(service.add(goal)) { result =>
      result shouldBe (true)
    }
  }

  it should "not save goal if it already exists" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L
    val goal = Goal(gatheringId, mantraId, 100, false)

    (goalDao.find(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { goal })

    whenReady(service.add(goal)) { result =>
      result shouldBe (false)
    }
  }

  it should "pass on error if fails to save" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L
    val goal = Goal(gatheringId, mantraId, 100, false)

    (goalDao.find(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { throw new java.util.NoSuchElementException() })
    (goalDao.save _).expects(goal).returning(Future { throw new RuntimeException() })

    intercept[RuntimeException] {
      Await.result(service.add(goal), Duration(5, SECONDS))
    }
  }

  "Remove goal" should "delete goal if exists" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L

    (goalDao.delete(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { true })

    whenReady(service.remove(gatheringId, mantraId)) { result =>
      result shouldBe (true)
    }
  }

  it should "return false if goal doesn't exist" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L

    (goalDao.delete(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { false })

    whenReady(service.remove(gatheringId, mantraId)) { result =>
      result shouldBe (false)
    }
  }

  "Find goal" should "delegate to goal dao" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L
    val goal = Goal(gatheringId, mantraId, 100, false)

    (goalDao.find(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { goal })

    whenReady(service.findGoal(gatheringId, mantraId)) { result =>
      result shouldBe (goal)
    }
  }

  "Find gathering by id and mantra id" should "delegate to goal dao" taggedAs (ServiceTest) in {
    val gatheringId = 1L
    val mantraId = 2L
    val gathering = Gathering(Some(gatheringId), UUID.randomUUID(), "A Gathering", "A Dedication", false, true, 2015, 8, 1)

    (dao.find(_: Long, _: Long)).expects(gatheringId, mantraId).returning(Future { gathering })

    whenReady(service.find(gatheringId, mantraId)) { result =>
      result shouldBe (gathering)
    }
  }
}