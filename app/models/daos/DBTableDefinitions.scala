package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

/**
 * Mix-in of Slick Tables, Table Queries and Table Row classes.
 */
trait DBTableDefinitions {

  protected val driver: JdbcProfile
  import driver.api._

  /** Represents a single row in the user table. From play-slick-postgres seed template. */
  case class DBUser(
    userID: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String])

  /** Represents User table. From play-slick-postgres seed template.*/
  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def id = column[String]("userID", O.PrimaryKey)
    def firstName = column[Option[String]]("firstName")
    def lastName = column[Option[String]]("lastName")
    def fullName = column[Option[String]]("fullName")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatarURL")
    def * = (id, firstName, lastName, fullName, email, avatarURL) <> (DBUser.tupled, DBUser.unapply)
  }

  /** Represents a single row in the logininfo table.  From play-slick-postgres seed template.*/
  case class DBLoginInfo(
    id: Option[Long],
    providerID: String,
    providerKey: String)

  /** Represents the LoginInfo database table.  From play-slick-postgres seed template.*/
  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  /** Represents a single row in the UserLoginInfo table.  From play-slick-postgres seed template.*/
  case class DBUserLoginInfo(
    userID: String,
    loginInfoId: Long)

  /** Represents UserLoginInfo table.  From play-slick-postgres seed template.*/
  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
    def userID = column[String]("userID")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  /** Represents a single row in the PasswordInfo table.  From play-slick-postgres seed template. */
  case class DBPasswordInfo(
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long)

  /** Represents PasswordInfo database table.  From play-slick-postgres seed template. */
  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  /** Represents a single row in the OAuth1Info database table. From play-slick-postgres seed template.*/
  case class DBOAuth1Info(
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long)

  /** Represents OAuth1Info database table. From play-slick-postgres seed template.*/
  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "oauth1info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  /** Represents a single row in the OAuth2Info database table. From play-slick-postgres seed template.*/
  case class DBOAuth2Info(
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long)

  /** Represents OAuth2Info database table. From play-slick-postgres seed template.*/
  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "oauth2info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("logininfoid")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  /** Represents a single row in the OpenIDInfo database table. From play-slick-postgres seed template. */
  case class DBOpenIDInfo(
    id: String,
    loginInfoId: Long)

  /** Represents OpenIDInfo database table. From play-slick-postgres seed template.*/
  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "openidinfo") {
    def id = column[String]("id", O.PrimaryKey)
    def loginInfoId = column[Long]("logininfoid")
    def * = (id, loginInfoId) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }

  /** Represents a single row in the OpenIDAttributes database table. From play-slick-postgres seed template.*/
  case class DBOpenIDAttribute(
    id: String,
    key: String,
    value: String)

  /** Represents OpenIDAttributes database table.  From play-slick-postgres seed template.*/
  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "openidattributes") {
    def id = column[String]("id")
    def key = column[String]("key")
    def value = column[String]("value")
    def * = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)
  }

  /**
   * Represents a single row in the Mantra database table.
   *
   * @constructor A new mantra row representation.
   *
   * @param id If not previously saved -1 or positive number representing primary key value
   * @param name Mantra name
   * @param description Descriptive text regarding this mantra
   * @param imgUrl Full URL to image to use on full-page display
   * @param year Year this was created. Expected to be 2015 or later
   * @param month Month this was created.
   * @param day Day this was created.
   * @param isArchived 1 if 'deleted' otherwise 0
   */
  case class MantraRow(
    id: Long,
    name: String,
    description: String,
    imgUrl: String,
    year: Int,
    month: Int,
    day: Int,
    isArchived: Int)

  /** Represents Mantra database table. */
  class MantraTable(tag: Tag) extends Table[MantraRow](tag, "mantra") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def description = column[String]("description")
    def imgUrl = column[String]("image_url")
    def year = column[Int]("y")
    def month = column[Int]("m")
    def day = column[Int]("d")
    def isArchived = column[Int]("is_archived")
    def * = (id, name, description, imgUrl, year, month, day, isArchived) <> (MantraRow.tupled, MantraRow.unapply)
  }

  /**
   * Represents a single row in the Accumulations database table.
   *
   * @constructor A new accumulation row representation.
   *
   * @param id If not previously saved -1 or positive number representing primary key value
   * @param mantraId Primary key of Mantra this refers to
   * @param userId UUID as string of owning user
   * @param gatheringId Primary key of Gathering this refers to
   * @param count Number of accumulations gathered on this date
   * @param year Year this accumulation was gathered. Expected to be 2015 or later
   * @param month Month this accumulation was gathered.
   * @param day Day this accumulation was gathered.
   */
  case class AccumulationRow(
    id: Long,
    mantraId: Long,
    userId: String,
    gatheringId: Long,
    count: Long,
    year: Int,
    month: Int,
    day: Int)

  /** Repressents Accumulations database table. */
  class AccumulationTable(tag: Tag) extends Table[AccumulationRow](tag, "accumulations") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def mantraId = column[Long]("mantra_id")
    def userId = column[String]("user_id")
    def gatheringId = column[Long]("gathering_id")
    def count = column[Long]("amount")
    def year = column[Int]("y")
    def month = column[Int]("m")
    def day = column[Int]("d")
    def * = (id, mantraId, userId, gatheringId, count, year, month, day) <> (AccumulationRow.tupled, AccumulationRow.unapply)
  }

  /**
   * Represents a single row in the Gatherings database table.
   *
   * @constructor A new gathering row representation.
   *
   * @param id If not previously saved -1 or positive number representing primary key value
   * @param userId UUID as string of owning user
   * @param name Name of this gathering
   * @param dedication Textual description of dedication
   * @param isAchieved 1 if achieved 0 otherwise
   * @param isPrivate 1 if only owning user may contribute accumulations 0 otherwise
   * @param isArchived 1 if 'deleted' 0 otherwise
   * @param year Year this gathering was created. Expected to be 2015 or later
   * @param month Month this gathering was created.
   * @param day Day this gathering was created.
   */
  case class GatheringRow(
    id: Long,
    userId: String,
    name: String,
    dedication: String,
    isAchieved: Int,
    isPrivate: Int,
    isArchived: Int,
    year: Int,
    month: Int,
    day: Int)

  /** Represents Gatherings database table. */
  class GatheringTable(tag: Tag) extends Table[GatheringRow](tag, "gatherings") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def userId = column[String]("user_id")
    def name = column[String]("name")
    def dedication = column[String]("dedication")
    def isAchieved = column[Int]("is_achieved")
    def isPrivate = column[Int]("is_private")
    def isArchived = column[Int]("is_archived")
    def year = column[Int]("y")
    def month = column[Int]("m")
    def day = column[Int]("d")
    def * = (id, userId, name, dedication, isAchieved, isPrivate, isArchived, year, month, day) <> (GatheringRow.tupled, GatheringRow.unapply)
  }

  /**
   * Represents a single row in the Goals database table.
   *
   * @constructor A new goal row representation.
   *
   * @param gatheringId Primary key value of gathering this belongs to
   * @param mantraId Primary key value of mantra this refers to
   * @param goal Number of mantras to be accumulated
   * @param isAchieved 1 if achieved 0 otherwise
   */
  case class GoalRow(gatheringId: Long, mantraId: Long, goal: Long, isAchieved: Int)

  /** Represents Goals database table. */
  class GoalTable(tag: Tag) extends Table[GoalRow](tag, "goals") {
    def gatheringId = column[Long]("gathering_id", O.PrimaryKey)
    def mantraId = column[Long]("mantra_id", O.PrimaryKey)
    def goal = column[Long]("goal")
    def isAchieved = column[Int]("is_achieved")
    def * = (gatheringId, mantraId, goal, isAchieved) <> (GoalRow.tupled, GoalRow.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickOpenIDInfos = TableQuery[OpenIDInfos]
  val slickOpenIDAttributes = TableQuery[OpenIDAttributes]
  val mantrasTable = TableQuery[MantraTable]
  val accumulationsTable = TableQuery[AccumulationTable]
  val gatheringsTable = TableQuery[GatheringTable]
  val goalsTable = TableQuery[GoalTable]

  /** queries used in multiple places. From play-slick-postgres seed template.*/
  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
