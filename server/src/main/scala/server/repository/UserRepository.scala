package server.repository

import server.model.User

import scala.concurrent.Future

trait UserRepository {
  def findByUsername(username: String): Future[Option[User]]
}
