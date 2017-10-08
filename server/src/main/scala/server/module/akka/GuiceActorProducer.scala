package server.module.akka

import akka.actor.{Actor, IndirectActorProducer}
import com.google.inject.name.Names
import com.google.inject.{Injector, Key}

class GuiceActorProducer(val injector: Injector, val actorName: String) extends IndirectActorProducer {

  override def actorClass: Class[Actor] = classOf[Actor]

  override def produce(): Actor =
    injector.getBinding(Key.get(classOf[Actor], Names.named(actorName))).getProvider.get()

}
