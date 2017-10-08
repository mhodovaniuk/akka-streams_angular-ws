package server.module.akka

import akka.actor._
import com.google.inject.Injector

class GuiceAkkaExtension extends Extension {
  private var injector: Injector = _

  def initialize(injector: Injector) {
    this.injector = injector
  }

  def props(actorName: String) = Props(classOf[GuiceActorProducer], injector, actorName)
}

object GuiceAkkaExtension extends ExtensionId[GuiceAkkaExtension] with ExtensionIdProvider {

  override def lookup() = GuiceAkkaExtension

  override def createExtension(system: ExtendedActorSystem) = new GuiceAkkaExtension

  override def get(system: ActorSystem): GuiceAkkaExtension = super.get(system)

}
