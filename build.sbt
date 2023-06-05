import Dependencies.Libraries._

addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

def stdSettings(projectName: String) =
  Seq(
    name := projectName,
    version := "0.2",
    scalaVersion := "2.13.10",
    libraryDependencies += compilerPlugin("com.hmemcpy" %% "zio-clippy" % "0.0.1")
  )

lazy val kafka = project
  .in(file("modules/kafka"))
  .settings(stdSettings("kafka"))
  .settings(
    libraryDependencies ++=
      embeddedKafka ++ logging,
    cancelable := false
  )

lazy val protocol = project
  .in(file("modules/protocol"))
  .settings(stdSettings("protocol"))
  .settings(libraryDependencies ++= zio ++ zioJson)

lazy val producer = project
  .in(file("modules/producer"))
  .settings(stdSettings("producer"))
  .settings(
    libraryDependencies ++=
      zio ++ zioJson ++ logging
  )
  .dependsOn(protocol)

lazy val processor = project
  .in(file("modules/processor"))
  .settings(stdSettings("processor"))
  .settings(
    scalacOptions += "-Ymacro-annotations",
    libraryDependencies ++=
      zio ++ zioHttp ++ zioJson ++ logging
  )
  .dependsOn(protocol)
