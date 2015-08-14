package models.daos

import models.Mantra
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{ Try, Success, Failure }
import java.text.SimpleDateFormat
import java.util.Calendar

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
    db.run(mantrasTable.filter(_.isArchived === 0).result).map(_.map { row =>
      Mantra(Some(row.id), row.name, row.description, row.imgUrl, row.year, row.month, row.day)
    })
  }

  /**
   * Finds a mantra by its ID.
   *
   * @param mantraID The ID of the mantra to find.
   * @return The found mantra or None if no mantra for the given ID could be found.
   */
  def findById(mantraID: Long): Future[Mantra] = {
    db.run(mantrasTable.filter(_.id === mantraID).filter(_.isArchived === 0).result).map { result =>
      val list = result.map { row =>
        Mantra(Some(row.id), row.name, row.description, row.imgUrl, row.year, row.month, row.day)
      }
      if (list.length > 0) list.head else throw new java.util.NoSuchElementException(s"Mantra with id ${mantraID} not found.")
    }
  }

  /**
   * Saves a mantra.
   *
   * @param mantra The mantra to save.
   * @return The saved mantra.
   */
  def save(mantra: models.Mantra): Future[Mantra] = {
    val id = if (mantra.id == None) -1 else mantra.id.get
    val dbMantra = MantraRow(id, mantra.name, mantra.description, mantra.imgUrl, mantra.year, mantra.month, mantra.day, 0)
    val actions = (for {
      result <- (mantrasTable returning mantrasTable.map(_.id)).insertOrUpdate(dbMantra)
    } yield result).transactionally
    val f = db.run(actions).map { id =>
      id match {
        case Some(newId) => mantra.copy(id = Some(newId)) // Some is returned for insert
        case None => mantra // None is returned for update
      }
    }
    f
  }

  /**
   * 'Deletes' a mantra.
   *
   * @param mantra The mantra to delete
   * @return true if deleted false otherwise
   */
  def delete(mantra: Mantra): Future[Boolean] = {
    val today = Calendar.getInstance.getTime
    val f = new SimpleDateFormat("dd-MM-YYYY kk:hh:ss")

    val id = if (mantra.id == None) -1 else mantra.id.get
    val dbMantra = MantraRow(id, s"Archived '${mantra.name}' at ${f.format(today)}", mantra.description, mantra.imgUrl, mantra.year, mantra.month, mantra.day, 1)
    val f2 = db.run(mantrasTable.insertOrUpdate(dbMantra)).map { _ =>
      true
    } recover {
      case _: Throwable => false
    }
    f2
  }
}