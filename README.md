[![Travis Build](https://travis-ci.org/leannenorthrop/play-mantra-accumulations.svg?branch=master)](https://travis-ci.org/leannenorthrop/scala-cart-kata)
[![Coverage Status](https://coveralls.io/repos/leannenorthrop/play-mantra-accumulations/badge.svg?branch=master&service=github)](https://coveralls.io/github/leannenorthrop/scala-cart-kata?branch=master)


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

# Play Doc
https://www.playframework.com/documentation/2.4.2/api/scala/index.html#play.api.Play$

# Slick Doc
http://slick.typesafe.com/doc/3.0.0/dbio.html
http://slick.typesafe.com/doc/3.0.0-M1/api/#scala.slick.jdbc.JdbcBackend$SessionDef

# Silhouette Doc
http://silhouette.mohiva.com/docs/endpoints

# Testing 
https://www.playframework.com/documentation/2.4.2/ScalaTestingWithScalaTest
https://www.playframework.com/documentation/2.4.x/ScalaFunctionalTestingWithScalaTest
https://www.playframework.com/documentation/2.4.2/ScalaTestingWithGuice
https://www.playframework.com/documentation/2.4.x/ScalaTestingWithDatabases