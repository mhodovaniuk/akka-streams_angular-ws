package server.service

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import server.actor.UserActor
import server.model.Item
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class ItemServiceSpec extends TestKit(ActorSystem("ItemServiceSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  "A Item Service" must {
    "insert item to the empty item list and return position as 0 and item with some id" in {
      val service = new ItemServiceImpl()
      val (position, item) = service.insertItem(TestActorRef[UserActor], -1, Item(None, "a"))
      service.getItems.length should be(1)
      position should be(0)
      item.id.isDefined should be(true)
    }
    "subscribe" in {
      val service = new ItemServiceImpl()
      service.subscribe(TestActorRef[UserActor])
      service.getSubscribersCount should be(1)
    }
    "unsubscribe" in {
      val service = new ItemServiceImpl()
      val actor = TestActorRef[UserActor]
      service.subscribe(actor)
      service.getSubscribersCount should be(1)
      service.unsubscribe(actor)
      service.getSubscribersCount should be(0)
    }
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
}
