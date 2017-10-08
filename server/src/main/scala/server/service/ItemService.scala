package server.service

import akka.actor.ActorRef
import server.model.Item

import scala.collection.mutable.Set

trait ItemService {

  def subscribe(actor: ActorRef): Unit

  def unsubscribe(actor: ActorRef): Unit

  def getItems: IndexedSeq[Item]

  def getSubscribersCount: Int

  def insertItem(iniciator: ActorRef, position: Int, item: Item): (Int, Item)

  def removeItem(iniciator: ActorRef, id: Long): Option[(Int, Item)]
}
