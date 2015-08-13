package models.services

import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import models.daos.GatheringDAO
import org.scalatest._
import models.Gathering
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import java.util.UUID

class GatheringServiceSpec extends ServiceSpec with Matchers with BeforeAndAfter {
  var service: GatheringService = null
  val dao = mock[GatheringDAO]

  before {
    val injector = new GuiceInjectorBuilder()
      .bindings(Seq(bind[GatheringDAO].toInstance(dao),
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

  "Find by mantra" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantraId = 1L
    val gathering = Gathering(None, UUID.randomUUID(), "A Gathering", "A Dedication", false, true, 2015, 8, 1)

    (dao.find _).expects(mantraId).returning(Future { Seq(gathering) })

    whenReady(service.findByMantra(mantraId)) { found =>
      found.length shouldBe (1)
    }
  }
}