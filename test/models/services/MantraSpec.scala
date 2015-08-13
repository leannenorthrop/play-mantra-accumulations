package models.services

import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import models.daos.MantraDAO
import org.scalatest._
import models.Mantra
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._

class MantraSpec extends ServiceSpec with Matchers with BeforeAndAfter {
  var service: MantraService = null
  val dao = mock[MantraDAO]

  before {
    val injector = new GuiceInjectorBuilder()
      .bindings(Seq(bind[MantraDAO].toInstance(dao),
        bind[MantraService].to[MantraServiceImpl]))
      .injector

    service = injector.instanceOf[MantraService]
  }

  after {
  }

  "Saving a mantra" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantra = Mantra(None, "name", "description", "http://url", 2015, 8, 19)

    (dao.save _).expects(mantra).returning(Future { mantra })

    whenReady(service.save(mantra)) { m =>
      m shouldBe (mantra)
    }
  }

  it should "fail when exception occurs" taggedAs (ServiceTest) in {
    val mantra = Mantra(None, "name", "description", "http://url", 2015, 8, 19)

    (dao.save _).expects(mantra).returning(Future { throw new RuntimeException("Failed") })

    val f = service.save(mantra)

    intercept[RuntimeException] {
      Await.result(f, Duration(1000, MILLISECONDS))
    }
  }

  "Find all" should "delegate to dao" taggedAs (ServiceTest) in {
    (dao.findAll _).expects().returning(Future {
      Seq(Mantra(None, "name", "description", "http://url", 2015, 8, 19))
    })

    whenReady(service.findAll()) { found =>
      found.length shouldBe (1)
    }
  }

  "Find by id" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantra = Mantra(Some(1), "name", "description", "http://url", 2015, 8, 19)

    (dao.findById _).expects(1).returning(Future { mantra })

    whenReady(service.find(1)) { found =>
      found shouldBe (mantra)
    }
  }

  "Delete" should "delegate to dao" taggedAs (ServiceTest) in {
    val mantra = Mantra(Some(1), "name", "description", "http://url", 2015, 8, 19)

    (dao.delete _).expects(mantra).returning(Future { true })

    whenReady(service.delete(mantra)) { isDeleted =>
      isDeleted shouldBe (true)
    }
  }
}