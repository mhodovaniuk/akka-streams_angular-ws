package server.module.akka

import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Injector, Provides, Singleton}
import net.codingwell.scalaguice.ScalaModule
import server.actor.UserActor

class AkkaModule extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {
  override def configure(): Unit = {
    bind[Actor].annotatedWith(Names.named(UserActor.name)).to[UserActor]
  }

  @Provides
  @Singleton
  def provideActorSystem(injector: Injector): ActorSystem = {
    val actorSystem = ActorSystem("system")
    GuiceAkkaExtension(actorSystem).initialize(injector)
    actorSystem
  }

  @Provides
  @Singleton
  def provideActorMaterializer(implicit actorSystem: ActorSystem, injector: Injector): ActorMaterializer = {
    ActorMaterializer()
  }
}
