package models.daos

import models.Gathering
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Gathering Data Access Object implementation using Slick to persist to/from default database.
 * Provides access to persistence layer for all Gathering objects.
 *
 * @author Leanne Northrop
 * @since 1.0.0
 */
class GatheringDAOImpl extends GatheringDAO with DAOSlick {
  import driver.api._

  /**
   * Saves given gathering. If supplied gathering id is set to None the id field the returned
   * gathering will be set after successful save.
   *
   * @param gathering The gathering to save
   * @return Updated gathering with updated id if supplied gathering id was None
   */
  def save(gathering: Gathering): Future[Gathering] = {
    val id = gathering.id.getOrElse(-1L)
    val dbRow = GatheringRow(id,
      gathering.userId.toString(),
      gathering.name,
      gathering.dedication,
      if (gathering.isAchieved) 1 else 0,
      if (gathering.isPrivate) 1 else 0,
      0,
      gathering.year,
      gathering.month,
      gathering.day)
    val actions = (for {
      result <- (gatheringsTable returning gatheringsTable.map(_.id)).insertOrUpdate(dbRow)
    } yield result).transactionally
    db.run(actions).map { id =>
      id match {
        case Some(newId) => gathering.copy(id = Some(newId)) // Some is returned for insert
        case None => gathering // None is returned for update
      }
    }
  }

  /**
   * Finds all Gatherings.
   *
   * @return Collection of found Gatherings.
   */
  def find(): Future[Seq[Gathering]] = {
    db.run(gatheringsTable.filter(_.isArchived === 0).result).map {
      _.map { row =>
        Gathering(Some(row.id), UUID.fromString(row.userId), row.name, row.dedication, (row.isAchieved == 1), (row.isPrivate == 1), row.year, row.month, row.day)
      }
    }
  }

  /**
   * Finds gatherings for specified mantra.
   *
   * @param mantraID Mantra id to find gatherings for
   * @return List of gatherings
   */
  def find(mantraId: Long): Future[Seq[Gathering]] = {
    val join = for {
      gathering <- gatheringsTable
      goals <- goalsTable if goals.gatheringId === gathering.id && goals.mantraId === mantraId && gathering.isArchived === 0
    } yield gathering
    db.run(join.result).map {
      _.map { row =>
        Gathering(Some(row.id), UUID.fromString(row.userId), row.name, row.dedication, (row.isAchieved == 1), (row.isPrivate == 1), row.year, row.month, row.day)
      }
    }
  }

  /**
   * Returns true if gathering with the given name exists, false otherwise.
   *
   * @param name Name of gathering to check existence for
   * @return true if exists in persistence layer, false otherwise
   */
  def exists(name: String): Future[Boolean] = {
    db.run(gatheringsTable.filter(_.name === name).result).map { list =>
      list.length > 0
    }
  }

  /**
   * 'Delete' gathering. Subsequent finds will not find given gathering.
   *
   * @param gatheringId The gathering id to hide/delete
   * @return true if successfully archived
   */
  def delete(gatheringId: Long): Future[Boolean] = {
    val today = Calendar.getInstance.getTime
    val f = new SimpleDateFormat("dd-MM-YYYY kk:hh:ss")

    val result = for {
      gathering <- db.run(gatheringsTable.filter(_.id === gatheringId).result)
      name <- Future { gathering.head.name }
      _ <- db.run(gatheringsTable.insertOrUpdate(gathering.head.copy(isArchived = 1, name = s"Archived '${name}' at ${f.format(today)}")))
      done <- Future { true }
    } yield done
    result recover {
      case _: UnsupportedOperationException => false
    }
  }
}