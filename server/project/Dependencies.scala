import sbt._

object Dependencies {
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.5"
  lazy val akkaJson = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5"
  lazy val akkaSlf4j = "com.typesafe.akka" % "akka-slf4j_2.12" % "2.4.17"

  lazy val slick = "com.typesafe.slick" %% "slick" % "3.2.0"
  lazy val flyway = "org.flywaydb" % "flyway-core" % "4.1.2"
  lazy val h2Database = "com.h2database" % "h2" % "1.4.194"

  lazy val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.25"
  lazy val logbackCore = "ch.qos.logback" % "logback-core" % "1.2.3"
  lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

  lazy val guice = "com.google.inject" % "guice" % "4.1.0"
  lazy val scalaGuice = "net.codingwell" % "scala-guice_2.12" % "4.1.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"
  lazy val akkaTest = "com.typesafe.akka" %% "akka-testkit" % "2.4.17"
}
