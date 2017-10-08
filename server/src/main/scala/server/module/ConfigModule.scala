package server.module

import com.google.inject.AbstractModule
import com.typesafe.config.{Config, ConfigFactory}
import net.codingwell.scalaguice.ScalaModule

class ConfigModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    val config = loadConfig()
    bind[Config].toInstance(loadConfig())
  }

  protected[this] def loadConfig(): Config = {
    ConfigFactory.load()
  }
}
