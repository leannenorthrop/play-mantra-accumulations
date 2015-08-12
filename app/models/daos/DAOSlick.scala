package models.daos

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import play.api.db.slick.HasDatabaseConfig

/**
 * Trait that imports models.daos.DBTableDefinitions to provide access to
 * slick db handling code to be mixed in with DAOs.
 *
 * LN: Not fond of this design as it doesn't easily support different named databases.
 */
trait DAOSlick extends DBTableDefinitions with HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](play.api.Play.current)
  import driver.api._
}