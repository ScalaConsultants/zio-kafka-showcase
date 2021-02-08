import sbt._

object Dependencies {

  private object Versions {
    val zio        = "1.0.3"
    val zioKafka   = "0.13.0"
    val zioLogging = "0.4.0"
    val circe      = "0.13.0"
    val sttp       = "2.2.9"
    val log4j      = "2.13.3"
    val disruptor  = "3.4.2"
    val jackson    = "2.12.0"
    val kafka      = "2.4.1.1"
  }

  object Libraries {
    val zio = Seq(
      "dev.zio" %% "zio"         % Versions.zio,
      "dev.zio" %% "zio-streams" % Versions.zio,
      "dev.zio" %% "zio-kafka"   % Versions.zioKafka
    )

    val zioLogging = Seq(
      "dev.zio" %% "zio-logging"       % Versions.zioLogging,
      "dev.zio" %% "zio-logging-slf4j" % Versions.zioLogging
    )

    val circe = Seq(
      "io.circe" %% "circe-core"    % Versions.circe,
      "io.circe" %% "circe-generic" % Versions.circe
    )

    val sttp = Seq(
      "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % Versions.sttp,
      "com.softwaremill.sttp.client" %% "circe"                         % Versions.sttp
    )

    val logging = Seq(
      "org.apache.logging.log4j" % "log4j-core"       % Versions.log4j,
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % Versions.log4j,
      "com.lmax"                 % "disruptor"        % Versions.disruptor
    )

    val jackson       = Seq("com.fasterxml.jackson.core" % "jackson-databind" % Versions.jackson)
    val embeddedKafka = Seq("io.github.embeddedkafka"    %% "embedded-kafka"  % Versions.kafka)
  }
}
