[![Travis Build](https://travis-ci.org/leannenorthrop/play-mantra-accumulations.svg?branch=master)](https://travis-ci.org/leannenorthrop/play-mantra-accumulations)
[![Coverage Status](https://coveralls.io/repos/leannenorthrop/play-mantra-accumulations/badge.svg?branch=master&service=github)](https://coveralls.io/github/leannenorthrop/play-mantra-accumulations?branch=master)


# play-mantra-accumulations
Play based web site for supporting on-line community and individual mantra accumulations.



#### Reminders
To run play with slick objects in the console set up environment using env.sh:

```
sbt
> console
> :paste
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
val a = new AccumulationDAOImpl().accumulationsTable
val q = a.length
Await.result(db.run(q.result), Duration(1000, MILLISECONDS))
```

To play with slick objects in the test console:

```
sbt
> test:console
> :paste
import play.api._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick._
import slick.driver.JdbcProfile
import scala.language.existentials
val app = new FakeApplication()
Play.start(app)
val dbConfig = DatabaseConfigProvider.get[JdbcProfile]("default")(app)
import dbConfig.driver.api._
val db = dbConfig.db
import scala.concurrent.duration._
import scala.concurrent._
import models.daos._
val a = new AccumulationDAOImpl().accumulationsTable
val q = a.length
Await.result(db.run(q.result), Duration(1000, MILLISECONDS))
```

##### Play Doc
https://www.playframework.com/documentation/2.4.2/api/scala/index.html#play.api.Play$

##### Slick Doc
http://slick.typesafe.com/doc/3.0.0/dbio.html
http://slick.typesafe.com/doc/3.0.0-M1/api/#scala.slick.jdbc.JdbcBackend$SessionDef

##### Silhouette Doc
http://silhouette.mohiva.com/docs/endpoints

##### Testing 
https://www.playframework.com/documentation/2.4.2/ScalaTestingWithScalaTest
https://www.playframework.com/documentation/2.4.x/ScalaFunctionalTestingWithScalaTest
https://www.playframework.com/documentation/2.4.2/ScalaTestingWithGuice
https://www.playframework.com/documentation/2.4.x/ScalaTestingWithDatabases
http://scalamock.org/user-guide/advanced_topics/#example-3---curried-methods
http://www.scalatest.org/user_guide/other_goodies

openssl des3 -in xxx -out xxx