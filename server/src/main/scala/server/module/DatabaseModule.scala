package server.module

import javax.sql.DataSource

import com.google.inject.{AbstractModule, Provides, Singleton}
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import org.flywaydb.core.Flyway
import server.repository.{UserRepository, UserRepositoryImpl}
import slick.jdbc.DriverDataSource
import slick.jdbc.JdbcBackend.{Database, DatabaseDef}

class DatabaseModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[UserRepository].to[UserRepositoryImpl].in[Singleton]
  }

  @Provides
  @Singleton
  def provideDataSource(config: Config): DataSource = {
    val url = config.getString("db.url")
    val driver = config.getString("db.driver")
    val username = config.getString("db.user")
    val password = config.getString("db.password")
    new DriverDataSource(url = url, driverClassName = driver, user = username, password = password)
  }

  @Provides
  @Singleton
  def provideFlyway(datasource: DataSource): Flyway = {
    val flyway = new Flyway()
    flyway.setDataSource(datasource)
    flyway
  }


  @Provides
  @Singleton
  def provideDatabase(datasource: DataSource): DatabaseDef = {
    Database.forDataSource(datasource, Some(10))
  }
}
