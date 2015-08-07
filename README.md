# play-mantra-accumulations
Play based web site for supporting on-line community and individual mantra accumulations.

To run play console:

```
activator
> console
play.core.server.ProdServerStart.main(Array())
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.language.existentials
val dbConfig = DatabaseConfigProvider.get[JdbcProfile](play.api.Play.current)
import dbConfig.driver.api._
val db = dbConfig.db
import scala.concurrent.duration._
import scala.concurrent._
import models.daos._
val a = new AccumulationDAOImpl().slickAccumulations
val q = a.length
Await.result(db.run(q.result), Duration(1000, MILLISECONDS))
```
