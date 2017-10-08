package server.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.Inject
import server.actor.UserActor._
import server.model.{Role, Item, User}
import server.repository.UserRepository
import server.service.ItemService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class UserActor @Inject()(userRepository: UserRepository, itemService: ItemService) extends Actor with ActorLogging {
  @volatile var user: Option[User] = None

  override def receive: Receive = {
    case Connected(outgoing) =>
      context.become(connected(outgoing))
  }

  def connected(outgoing: ActorRef): Receive = {
    case r: UnknownRequest =>
      log.debug("Unknown request.")
    case r: PingRequest =>
      log.debug(s"Ping Request $r received.")
      outgoing ! PingResponse(seq = r.seq)
    case r: AuthRequest =>
      log.debug(s"Auth Request $r received.")
      userRepository.findByUsername(r.username)
        .filter({
          case Some(user) => user.username == r.username && user.password == r.password
          case _ => false
        })
        .onComplete({
          case Success(Some(user)) =>
            this.user = Some(user)
            log.info(s"User ${user.username} logged in as '${user.role}'.")
            outgoing ! AuthSuccessResponse(user_type = user.role)
          case _ =>
            log.debug(s"Unable to find user with username '${r.username}'.")
            outgoing ! AuthFailureResponse()
        })
    case r: SubscribeRequest =>
      log.debug(s"Subscribe Request $r received.")
      itemService.subscribe(self)
      val items = itemService.getItems
      user match {
        case Some(u) => log.info("User '{}' with id {} was subscribed.", u.username, u.id.get)
        case _ => log.info("Anonymous user was subscribed.")
      }
      outgoing ! SubscribeResponse(items = items)
    case r: UnsubscribeRequest =>
      log.debug(s"Unsubscribe Request $r received.")
      itemService.unsubscribe(self)
      user match {
        case Some(u) => log.info("User '{}' with id {} was unsubscribed.", u.username, u.id.get)
        case _ => log.info("Anonymous user was unsubscribed.")
      }
    case r: AddUpdateItemRequest =>
      log.debug(s"Add Update Item Request $r received.")
      withAdminRights {
        val (position, item) = itemService.insertItem(self, r.position, r.item)
        log.info(s"Item '${item.name}' with id ${item.id.get} was added by user '${user.get.username}' with id ${user.get.id.get}.")
        outgoing ! ItemAddedUpdatedResponse(position = position, item = item)
      }(outgoing)
    case r: RemoveItemRequest =>
      withAdminRights {
        itemService.removeItem(self, r.id) match {
          case Some((_, item)) =>
            log.info(s"Item '${item.name}' with id ${item.id.get} was removed by by user '${user.get.username}' with id ${user.get.id.get}.")
            outgoing ! ItemRemovedResponse(id = item.id.get)
          case _ =>
        }
      }(outgoing)
    case r: WSPushResponse =>
      user match {
        case Some(u) => log.debug(s"User '${u.username}' with id ${u.id.get} was notified.")
        case _ => log.debug("Anonymous user was notified.")
      }

      outgoing ! r
  }

  def withAdminRights(block: => Unit)(outgoing: ActorRef): Unit = {
    if (user.exists(u => u.role.equalsIgnoreCase(Role.ADMIN.toString.toLowerCase)))
      block
    else {
      user match {
        case Some(u) => log.info(s"User '${u.username}' with id ${u.id.get} has no privileges to manage items.")
        case _ => log.info("Anonymous has no privileges to manage items.")
      }
      outgoing ! NotEnoughPrivilegesResponse()
    }
  }
}

object UserActor extends NamedActor {
  override final val name = "UserActor"

  sealed trait WSRequest

  sealed trait WSResponse

  sealed trait WSPushResponse extends WSResponse


  case class Connected(outgoing: ActorRef) extends WSRequest


  case class PingRequest($type: String = "ping", seq: Long) extends WSRequest

  case class PingResponse($type: String = "pong", seq: Long) extends WSResponse


  case class AuthRequest($type: String = "login", username: String, password: String) extends WSRequest

  case class AuthSuccessResponse($type: String = "login_successful", user_type: String) extends WSResponse

  case class AuthFailureResponse($type: String = "login_failed") extends WSResponse

  case class SubscribeRequest($type: String = "subscribe_items") extends WSRequest


  case class SubscribeResponse($type: String = "subscribe_items", items: Seq[Item]) extends WSResponse

  case class UnsubscribeRequest($type: String = "unsubscribe_items") extends WSRequest


  case class AddUpdateItemRequest($type: String = "add_update_item", position: Int, item: Item) extends WSRequest

  case class RemoveItemRequest($type: String = "remove_item", id: Long) extends WSRequest


  case class ItemAddedUpdatedResponse($type: String = "item_added_updated", position: Int, item: Item) extends WSPushResponse

  case class ItemRemovedResponse($type: String = "item_removed", id: Long) extends WSPushResponse


  case class NotEnoughPrivilegesResponse($type: String = "not_authorized") extends WSResponse

  case class UnknownRequest() extends WSRequest

  case class BadRequest() extends WSRequest

}
