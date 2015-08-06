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

  def save(accumulation: Accumulation): Option[Accumulation] = {
    try {
      val id = if (accumulation.id == None) -1 else accumulation.id.get
      val dbAccumulationRow = AccumulationRow(id, accumulation.mantraId, accumulation.userId.toString(), accumulation.gatheringId, accumulation.count, accumulation.year, accumulation.month, accumulation.day)
      val actions = (for {
        result <- (slickAccumulations returning slickAccumulations.map(_.id)).insertOrUpdate(dbAccumulationRow)
      } yield result).transactionally
      val f = db.run(actions).map{ id =>
        id.map{id2 => accumulation.copy(id=Some(id2))}
      } 

      val v = Await.result(f, Duration(1000, MILLISECONDS))

      v
    } catch {
      case _ : Throwable => None   
    }
  }
  def findForToday(userId: UUID, gatheringId: Long, mantraId: Long): Option[Accumulation] = {
    try {
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

      val f = db.run(query.result).map(_.map { row =>
        models.Accumulation(Some(row.id), row.year, row.month, row.day, row.count, row.mantraId, UUID.fromString(row.userId), row.gatheringId)
      })

      val v = Await.result(f, Duration(1000, MILLISECONDS))

      if (v.isEmpty) 
        Some(models.Accumulation(None, year.get, month.get, day.get, 0, mantraId, userId, gatheringId))
      else
        Some(v.head)
    } catch {
      case t : Throwable => None
    }     
  }
  def counts(mantraId: Long) : (Long,Long,Long,Long) = {
    try {
      val q = slickAccumulations.filter(_.mantraId === mantraId)
      var action = q.map(_.count).sum.result
      var f = db.run(action).map(_.map{case (sum) => sum})
      var v = Await.result(f, Duration(1000, MILLISECONDS))
      val total : Long = v.getOrElse(0L)

      val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)

      val q2 = q.filter(_.year === year)
      action = q2.map(_.count).sum.result
      f = db.run(action).map(_.map{case (sum) => sum})
      v = Await.result(f, Duration(1000, MILLISECONDS))
      val yearTotal : Long = v.getOrElse(0L)

      val q3 = q2.filter(_.month === month)
      action = q3.map(_.count).sum.result
      f = db.run(action).map(_.map{case (sum) => sum})
      v = Await.result(f, Duration(1000, MILLISECONDS))
      val monthTotal : Long = v.getOrElse(0L)

      action = q3.filter(_.day === day).map(_.count).sum.result
      f = db.run(action).map(_.map{case (sum) => sum})
      v = Await.result(f, Duration(1000, MILLISECONDS))
      val dayTotal : Long = v.getOrElse(0L)

      (total,yearTotal,monthTotal,dayTotal)
    } catch {
      case t : Throwable => t.printStackTrace(); (0L,0L,0L,0L)
    }     
  }

  def counts(mantraId: Long, gatheringId: Long) : (Long,Long,Long,Long) = {
    try {
      val q = slickAccumulations.filter(_.mantraId === mantraId).filter(_.gatheringId === gatheringId)
      var action = q.map(_.count).sum.result
      var f = db.run(action).map(_.map{case (sum) => sum})
      var v = Await.result(f, Duration(1000, MILLISECONDS))
      val total : Long = v.getOrElse(0L)

      val cal = Calendar.getInstance()
      val year = cal.get(Calendar.YEAR)
      val month = cal.get(Calendar.MONTH) + 1
      val day = cal.get(Calendar.DAY_OF_MONTH)

      val q2 = q.filter(_.year === year)
      action = q2.map(_.count).sum.result
      f = db.run(action).map(_.map{case (sum) => sum})
      v = Await.result(f, Duration(1000, MILLISECONDS))
      val yearTotal : Long = v.getOrElse(0L)

      val q3 = q2.filter(_.month === month)
      action = q3.map(_.count).sum.result
      f = db.run(action).map(_.map{case (sum) => sum})
      v = Await.result(f, Duration(1000, MILLISECONDS))
      val monthTotal : Long = v.getOrElse(0L)

      action = q3.filter(_.day === day).map(_.count).sum.result
      f = db.run(action).map(_.map{case (sum) => sum})
      v = Await.result(f, Duration(1000, MILLISECONDS))
      val dayTotal : Long = v.getOrElse(0L)

      (total,yearTotal,monthTotal,dayTotal)
    } catch {
      case t : Throwable => t.printStackTrace(); (0L,0L,0L,0L)
    }      
  }  
}