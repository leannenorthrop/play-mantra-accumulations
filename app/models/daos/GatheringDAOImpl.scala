package models.daos

import models.Gathering
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import java.util.UUID

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
}