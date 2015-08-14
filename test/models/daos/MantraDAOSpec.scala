package models.daos

import org.scalatest._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalatest.concurrent._
import scala.concurrent.duration._
import scala.concurrent._
import models.Mantra
import slick.driver.PostgresDriver.api._
import java.text.SimpleDateFormat
import java.util.Calendar

class MantraDAOSpec extends DatabaseSpec with Matchers with OptionValues with BeforeAndAfter {
  var dao: MantraDAO = null

  before {
    dao = new MantraDAOImpl()

    val f = for {
      _ <- db.run(sqlu"delete from mantra")
    } yield ()

    whenReady(f) { _ =>
    }
  }

  after {
  }

  "Saving a new non-existant Mantra" should "save" taggedAs (DbTest) in {
    val mantra = Mantra(None, "name", "description", "http://url", 2015, 8, 19)

    whenReady(dao.save(mantra)) { savedMantra =>
      val length = Await.result(db.run(sql"select count(*) from mantra".as[Int]), Duration(2000, MILLISECONDS)).head
      length shouldBe (1)
    }
  }

  "Saving an existing Mantra" should "update" taggedAs (DbTest) in {
    val mantra = Mantra(None, "name", "description", "http://url", 2015, 8, 19)

    whenReady(dao.save(mantra)) { savedMantra =>
      val length = Await.result(db.run(sql"select count(*) from mantra".as[Int]), Duration(2000, MILLISECONDS)).head
      length shouldBe (1)

      val mantraUpdated = savedMantra.copy(name = "new name")
      whenReady(dao.save(mantraUpdated)) { savedMantra2 =>
        val length2 = Await.result(db.run(sql"select count(*) from mantra".as[Int]), Duration(2000, MILLISECONDS)).head
        length2 shouldBe (1)
      }
    }
  }

  "Finding all mantra" should "return all mantra" taggedAs (DbTest) in {
    val f = for {
      _ <- dao.save(Mantra(None, "name1", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name2", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name3", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name4", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name5", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name6", "description", "http://url", 2015, 8, 19))
    } yield ()

    whenReady(f) { _ =>
      whenReady(dao.findAll()) { foundMantras =>
        foundMantras.length shouldBe (6)
      }
    }
  }

  "Finding a mantra" should "return the mantra" taggedAs (DbTest) in {
    val f = for {
      _ <- dao.save(Mantra(None, "name1", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name2", "description", "http://url", 2015, 8, 19))
      m3 <- dao.save(Mantra(None, "name3", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name4", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name5", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name6", "description", "http://url", 2015, 8, 19))
    } yield m3

    whenReady(f) { mantra =>
      whenReady(dao.findById(mantra.id.get)) { foundMantra =>
        foundMantra.name shouldBe ("name3")
      }
    }
  }

  it should "fail if not found" taggedAs (DbTest) in {
    intercept[java.util.NoSuchElementException] {
      Await.result(dao.findById(234L), Duration(5, SECONDS))
    }
  }

  "Deleting mantra" should "hide mantra from findAll" taggedAs (DbTest) in {
    val f = for {
      _ <- dao.save(Mantra(None, "name1", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name2", "description", "http://url", 2015, 8, 19))
      m <- dao.save(Mantra(None, "name3", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name4", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name5", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name6", "description", "http://url", 2015, 8, 19))
    } yield (m)

    whenReady(f) { mantra =>
      whenReady(dao.delete(mantra)) { result =>
        result shouldBe (true)
        whenReady(dao.findAll()) { foundMantras =>
          foundMantras.length shouldBe (5)
        }
      }
    }
  }

  it should "rename mantra to include date archived" taggedAs (DbTest) in {
    val f = for {
      _ <- dao.save(Mantra(None, "name1", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name2", "description", "http://url", 2015, 8, 19))
      m <- dao.save(Mantra(None, "name3", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name4", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name5", "description", "http://url", 2015, 8, 19))
      _ <- dao.save(Mantra(None, "name6", "description", "http://url", 2015, 8, 19))
    } yield (m)

    whenReady(f) { mantra =>
      whenReady(dao.delete(mantra)) { result =>
        result shouldBe (true)
        val f = for {
          name <- db.run(sql"select name from mantra where id = ${mantra.id.get}".as[String])
        } yield name

        whenReady(f) { name =>
          val f = new SimpleDateFormat("dd-MM-YYYY kk:hh")
          val nowStr = f.format(Calendar.getInstance.getTime)
          assert(name.head.contains(nowStr))
        }
      }
    }
  }
}