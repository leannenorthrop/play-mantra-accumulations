package models.daos

import models.Mantra
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{ Try, Success, Failure }

/**
 * Mantra Data Access Object implementation using Slick to persist to/from default database.
 * Provides access to persistence layer for all Mantra objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
class MantraDAOImpl extends MantraDAO with DAOSlick {
  import driver.api._

  /**
   * Finds all mantra.
   *
   * @return The found mantra or empty list if no mantra could be found.
   */
  def findAll() = {
    try {
      val f = db.run(slickMantras.filter(_.isArchived === 0).result).map(_.map { row =>
        Mantra(Some(row.id), row.name, row.description, row.imgUrl, row.year, row.month, row.day)
      })

      val v = Await.result(f, Duration(1000, MILLISECONDS))

      v.toList
    } catch {
      case _: Throwable => List[Mantra]()
    }
  }

  /**
   * Finds a mantra by its ID.
   *
   * @param mantraID The ID of the mantra to find.
   * @return The found mantra or None if no mantra for the given ID could be found.
   */
  def findById(mantraID: Long): Option[Mantra] = {
    try {
      val f = db.run(slickMantras.filter(_.id === mantraID).filter(_.isArchived === 0).result).map(_.map { row =>
        Mantra(Some(row.id), row.name, row.description, row.imgUrl, row.year, row.month, row.day)
      })

      val v = Await.result(f, Duration(1000, MILLISECONDS))

      Some(v.head)
    } catch {
      case _: Throwable => None
    }
  }

  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: models.Mantra): Option[Mantra] = {
    try {
      val id = if (mantra.id == None) -1 else mantra.id.get
      val dbMantra = MantraRow(id, mantra.name, mantra.description, mantra.imgUrl, mantra.year, mantra.month, mantra.day, 0)
      val actions = (for {
        result <- (slickMantras returning slickMantras.map(_.id)).insertOrUpdate(dbMantra)
      } yield result).transactionally
      val f = db.run(actions).map { id =>
        id.map { id2 => mantra.copy(id = Some(id2)) }
      }

      val v = Await.result(f, Duration(1000, MILLISECONDS))

      v
    } catch {
      case _: Throwable => None
    }
  }
}