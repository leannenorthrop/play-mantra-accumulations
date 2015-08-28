package models.daos

import org.scalatest._
import play.api._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick._
import slick.driver.JdbcProfile
import scala.language.existentials
import play.api.db.evolutions._
import org.scalatest.concurrent.{ ScalaFutures, Futures, PatienceConfiguration }
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.Tag
import play.api.db.slick.evolutions._
import org.dbunit.database._
import org.dbunit.util.fileloader._

object DbTest extends Tag("org.northrop.leanne.play.mantra.tags.DbTest")

trait Database extends BeforeAndAfterAll with ScalaFutures { this: Suite =>
  val dbName = "default"
  val app = FakeApplication()
  var db: slick.jdbc.JdbcBackend#DatabaseDef = null

  implicit val defaultPatience = PatienceConfig(timeout = Span(2, Seconds), interval = Span(5, Millis))

  private def playDB(application: Application, dbName: String): play.api.db.Database = {
    val slickApiCache = Application.instanceCache[SlickApi]
    val slickApi = slickApiCache(application)
    val dbApi = SlickDBApi(slickApi)
    val playDB = dbApi.database(dbName)
    playDB
  }

  private def slickDB(application: Application, dbName: String): slick.driver.JdbcProfile#Backend#Database = {
    val dbConfig = DatabaseConfigProvider.get[JdbcProfile](dbName)(app)
    import dbConfig.driver.api._
    val db = dbConfig.db
    db
  }

  override def beforeAll() {
    Play.start(app)
    db = slickDB(app, dbName)

    val playDb = playDB(app, dbName)
    Evolutions.applyEvolutions(playDb)
    playDb.shutdown
    super.beforeAll() // To be stackable, must call super.beforeEach
  }

  override def afterAll() {
    try {
      super.afterAll() // To be stackable, must call super.afterEach

      val playDb = playDB(app, dbName)
      Evolutions.cleanupEvolutions(playDb)
      playDb.shutdown
    } finally {
      db.close
      Play.stop(app)
    }
  }

  def getDataSet(name: String): org.dbunit.dataset.IDataSet = {
    val ldr = new CsvDataFileLoader()
    ldr.load("/data/" + name + "/")
  }

  def cleanInsert(name: String): Unit = {
    val session = db.createSession
    val connection = new org.dbunit.database.DatabaseConnection(session.conn)
    val dataSet = getDataSet(name)

    try {
      org.dbunit.operation.DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet)
    } finally {
      connection.close
      //session.close
    }
  }
}

abstract class DatabaseSpec extends FlatSpec with Database {
}