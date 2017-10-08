package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.typesafe.config.Config
import net.codingwell.scalaguice.InjectorExtensions._
import org.flywaydb.core.Flyway
import org.h2.tools.Server
import server.module.akka.{AkkaModule, GuiceAkkaExtension}
import server.module.{ConfigModule, DatabaseModule, ServiceModule}
import server.router.WSRouter

import scala.io.StdIn

object Application {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new ConfigModule, new DatabaseModule, new ServiceModule, new AkkaModule)

    val flyway = injector.instance[Flyway]
    flyway.migrate()

    val config = injector.instance[Config]
    val webServer = if (config.getBoolean("h2.enable-console")) {
      println("H2 console is available at http://localhost:8082")
      val tmp = Server.createWebServer()
      tmp.start()
      Some(tmp)
    } else
      None

    implicit val actorSystem = injector.instance[ActorSystem]
    implicit val actorMaterializer = injector.instance[ActorMaterializer]
    implicit val executionContext = actorSystem.dispatcher

    val wsRouter = injector.instance[WSRouter]
    val host = config.getString("app.host")
    val port = config.getInt("app.port")
    val bindingFuture = Http().bindAndHandle(wsRouter.route, host, port)

    println(s"Server online at http://$host:$port/")
  }
}
