package models.services

import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import models.daos.AccumulationDAO
import org.scalatest._
import models.Accumulation
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import java.util.UUID
import java.util.Calendar

class AccumulationServiceSpec extends ServiceSpec with Matchers with BeforeAndAfter {
  var service: AccumulationService = null
  val dao = mock[AccumulationDAO]

  before {
    val injector = new GuiceInjectorBuilder()
      .bindings(Seq(bind[AccumulationDAO].toInstance(dao),
        bind[AccumulationService].to[AccumulationServiceImpl]))
      .injector

    service = injector.instanceOf[AccumulationService]
  }

  after {
  }

  "Saving an accumulation" should "delegate to dao" taggedAs (ServiceTest) in {
    val accumulation = Accumulation(None, 2015, 8, 7, 100, 1L, UUID.randomUUID(), 1L)

    (dao.save _).expects(accumulation).returning(Future { accumulation })

    whenReady(service.save(accumulation)) { m =>
      m shouldBe (accumulation)
    }
  }

  it should "fail when exception occurs" taggedAs (ServiceTest) in {
    val accumulation = Accumulation(None, 2015, 8, 7, 100, 1L, UUID.randomUUID(), 1L)

    (dao.save _).expects(accumulation).returning(Future { throw new RuntimeException("Failed") })

    val f = service.save(accumulation)

    intercept[RuntimeException] {
      Await.result(f, Duration(1000, MILLISECONDS))
    }
  }

  "Find for today" should "delegate to dao" taggedAs (ServiceTest) in {
    val uuid = UUID.randomUUID()
    val gatheringID = 1L
    val mantraID = 2L
    val a = Accumulation(None, 2015, 8, 7, 100, mantraID, UUID.randomUUID(), gatheringID)
    (dao.findForToday _).expects(uuid, gatheringID, mantraID).returning(Future { a })

    whenReady(service.findForToday(uuid, gatheringID, mantraID)) { found =>
      found shouldBe (a)
    }
  }

  it should "fail if no accumulation exists for today" taggedAs (ServiceTest) in {
    val uuid = UUID.randomUUID()
    val gatheringID = 1L
    val mantraID = 2L
    val a = Accumulation(None, 2015, 8, 7, 100, mantraID, UUID.randomUUID(), gatheringID)
    (dao.findForToday _).expects(uuid, gatheringID, mantraID).returning(Future { throw new RuntimeException() })

    val f = service.findForToday(uuid, gatheringID, mantraID)

    intercept[RuntimeException] {
      Await.result(f, Duration(1000, MILLISECONDS))
    }
  }

  "Find or create for today" should "create accumulation if no accumulation exists for today" taggedAs (ServiceTest) in {
    val uuid = UUID.randomUUID()
    val gatheringID = 1L
    val mantraID = 2L
    val cal = Calendar.getInstance()
    val a = Accumulation(None, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH) + 1), cal.get(Calendar.DAY_OF_MONTH), 0, mantraID, uuid, gatheringID)

    (dao.findForToday _).expects(uuid, gatheringID, mantraID).returning(Future { throw new RuntimeException() })
    (dao.save _).expects(a).returning(Future { a })

    val f = service.findOrCreateForToday(uuid, gatheringID, mantraID)

    whenReady(f) { found =>
      found shouldBe (a)
    }
  }

  "Counts for mantra" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantraId = 1L
    (dao.counts(_: Long)(_: Option[Long])).expects(mantraId, None).returning(Future { (4L, 3L, 2L, 1L) })

    whenReady(service.counts(mantraId)) { found =>
      found shouldBe ((4L, 3L, 2L, 1L))
    }
  }

  "Counts for mantra and gathering" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantraId = 1L
    val gatheringId = 2L
    (dao.counts(_: Long)(_: Option[Long])).expects(mantraId, Some(gatheringId)).returning(Future { (4L, 3L, 2L, 1L) })

    whenReady(service.counts(mantraId, gatheringId)) { found =>
      found shouldBe ((4L, 3L, 2L, 1L))
    }
  }
}