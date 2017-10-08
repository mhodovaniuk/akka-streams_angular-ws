package server.repository

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import server.model.User
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.DatabaseDef
import slick.lifted.ProvenShape.proveShapeOf
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.H2Profile.api._


class UserRepositoryImpl @Inject()(val db: DatabaseDef) extends UserRepository with LazyLogging {

  class UserItem(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def username = column[String]("USERNAME")

    def password = column[String]("PASSWORD")

    def role = column[String]("ROLE")

    def * = (id.?, username, password, role) <> ((User.apply _).tupled, User.unapply)
  }

  val users = TableQuery[UserItem]

  override def findByUsername(username: String): Future[Option[User]] = db.run(users.filter(_.username === username).result.headOption)
}
