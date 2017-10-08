import Dependencies._

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    inThisBuild(List(
      organization := "mhodovaniuk",
      scalaVersion := "2.12.1",
      version := "0.1.0"
    )),
    name := "akkas-treams_angular-ws_server",
    dockerExposedPorts := Seq(8080),
    libraryDependencies ++= Seq(
      akkaHttp % Compile,
      akkaJson % Compile,
      akkaSlf4j % Compile,
      slick % Compile,
      flyway % Compile,
      h2Database % Compile,
      slf4jApi % Compile,
      logbackCore % Compile,
      logbackClassic % Compile,
      scalaLogging % Compile,
      guice % Compile,
      scalaGuice % Compile,
      scalaTest % Test,
      akkaTest % Test)
  )
