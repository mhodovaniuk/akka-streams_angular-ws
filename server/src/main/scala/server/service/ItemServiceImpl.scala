package server.service

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import server.actor.UserActor.{ItemAddedUpdatedResponse, ItemRemovedResponse, WSPushResponse}
import server.model.Item

import scala.collection.mutable

class ItemServiceImpl extends ItemService with LazyLogging {
  private val lock = new ReentrantLock()
  private val subscribers = new mutable.HashSet[ActorRef]()
  private val itemSequence: AtomicLong = new AtomicLong()
  @volatile private var items: Vector[Item] = Vector[Item]()

  override def getItems: IndexedSeq[Item] = items

  override def getSubscribersCount: Int = subscribers.size

  override def subscribe(subscriber: ActorRef): Unit = {
    subscribers.synchronized {
      subscribers += subscriber
    }
  }

  override def unsubscribe(subscriber: ActorRef): Unit = {
    subscribers.synchronized {
      subscribers -= subscriber
    }
  }

  private def notifySubscribers(iniciator: ActorRef, message: WSPushResponse): Unit = {
    subscribers.synchronized {
      subscribers.filter(_ != iniciator).foreach(_ ! message)
    }
  }

  override def insertItem(iniciator: ActorRef, position: Int, item: Item): (Int, Item) = {
    lock.lock()
    try {
      if (position < 0) {
        if (getItems.isEmpty) {
          val newItem = if (item.id.isDefined) item else item.copy(id = Option(itemSequence.incrementAndGet()))
          items = items :+ newItem
          notifySubscribers(iniciator, ItemAddedUpdatedResponse(position = 0, item = newItem))
          (0, newItem)
        } else {
          val newItem = if (item.id.isDefined) item else item.copy(id = Option(itemSequence.incrementAndGet()))
          items = items :+ newItem
          notifySubscribers(iniciator, ItemAddedUpdatedResponse(position = items.size - 1, item = newItem))
          (items.size - 1, newItem)
        }
      } else {
        val newItem = if (item.id.isDefined) item else item.copy(id = Option(itemSequence.incrementAndGet()))
        items = items.patch(position, Vector(newItem), 0)
        notifySubscribers(iniciator, ItemAddedUpdatedResponse(position = position, item = newItem))
        (position, newItem)
      }
    } finally {
      lock.unlock()
    }
  }

  override def removeItem(iniciator: ActorRef, id: Long): Option[(Int, Item)] = {
    lock.lock()
    try {
      items.find(i => i.id.get == id) match {
        case Some(item) =>
          val position = items.indexOf(item)
          items = items.filter(i => i.id != item.id)
          notifySubscribers(iniciator, ItemRemovedResponse(id = item.id.get))
          Some(position, item)
        case None =>
          None
      }
    } finally {
      lock.unlock()
    }
  }
}
