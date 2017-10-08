package server.module

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import server.service.{ItemService, ItemServiceImpl}

class ServiceModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[ItemService].to[ItemServiceImpl].asEagerSingleton()
  }
}
