package models.daos

import models.Accumulation
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}
import java.util.Calendar
import java.util.UUID

class AccumulationDAOImpl extends AccumulationDAO with DAOSlick {
  import driver.api._

  def save(accumulation: Accumulation): Future[Option[Accumulation]] = {
      val id = accumulation.id.getOrElse(-1L)
      val dbAccumulationRow = AccumulationRow(id, accumulation.mantraId, accumulation.userId.toString(), accumulation.gatheringId, accumulation.count, accumulation.year, accumulation.month, accumulation.day)
      val actions = (for {
        result <- (slickAccumulations returning slickAccumulations.map(_.id)).insertOrUpdate(dbAccumulationRow)
      } yield result).transactionally
      db.run(actions).map{ id => 
        id match {
          case Some(newId) => Some(accumulation.copy(id=Some(newId))) // Some is returned for insert
          case None => Some(accumulation) // None is returned for update
        }
      } 
  }

  /*def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Future[Option[Accumulation]] = {
      val cal = Calendar.getInstance()
      val year = Option(cal.get(Calendar.YEAR))
      val month = Option(cal.get(Calendar.MONTH) + 1)
      val day = Option(cal.get(Calendar.DAY_OF_MONTH))
      val user = Option(userId.toString())
      val gathering = Option(gatheringId)
      val mantra = Option(mantraId)

      val query = slickAccumulations.filter { accumulation => 
        List(
            day.map(accumulation.day === _),
            month.map(accumulation.month === _),
            year.map(accumulation.year === _),
            year.map(accumulation.year === _),
            year.map(accumulation.year === _),
            user.map(accumulation.userId === _),
            gathering.map(accumulation.gatheringId === _),
            mantra.map(accumulation.mantraId === _)
        ).collect({case Some(criteria)  => criteria}).reduceLeftOption(_ && _).getOrElse(true:Rep[Boolean])
      }

      db.run(query.result).map(_.map { row =>
        Some(models.Accumulation(Some(row.id), row.year, row.month, row.day, row.count, row.mantraId, UUID.fromString(row.userId), row.gatheringId))
      })    
  }

  def counts(mantraId: Long) : Future[Option[(Long,Long,Long,Long)]] = {
      val q = slickAccumulations.filter(_.mantraId === mantraId)
      val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)      
      val actions = for {
        grandTotal <- q.map(_.count).sum
        yearTotal <- q.filter(_.year === year).map(_.count).sum
        monthTotal <- q.filter(_.year === year).filter(_.month === month).map(_.count).sum
        dayTotal <- q.filter(_.year === year).filter(_.month === month).filter(_.day === day).map(_.count).sum
      } yield ((grandTotal,yearTotal,monthTotal,dayTotal))
      
      db.run(actions.result)
  }

  def counts(mantraId: Long, gatheringId: Long) : Future[(Long,Long,Long,Long)] = {
    val q = slickAccumulations.filter(_.mantraId === mantraId).filter(_.gatheringId === gatheringId)
      val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)      
      val actions = for {
        grandTotal <- q.map(_.count).sum
        yearTotal <- q.filter(_.year === year).map(_.count).sum
        monthTotal <- q.filter(_.year === year).filter(_.month === month).map(_.count).sum
        dayTotal <- q.filter(_.year === year).filter(_.month === month).filter(_.day === day).map(_.count).sum
      } yield ((grandTotal,yearTotal,monthTotal,dayTotal))
      
      db.run(actions.result)  
  } */ 
}