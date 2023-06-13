import sbt._

object Dependencies {

  private object Versions {
    val disruptor  = "3.4.4"
    val kafka      = "3.4.0"
    val log4j      = "2.20.0"
    val zio        = "2.0.15"
    val zioClippy  = "0.0.1"
    val zioConfig  = "4.0.0-RC16"
    val zioHttp    = "3.0.0-RC2"
    val zioJson    = "0.5.0"
    val zioKafka   = "2.3.2"
    val zioLogging = "2.1.13"
  }

  object Libraries {
    val zio = Seq(
      "dev.zio" %% "zio"                 % Versions.zio,
      "dev.zio" %% "zio-config-magnolia" % Versions.zioConfig,
      "dev.zio" %% "zio-config-typesafe" % Versions.zioConfig,
      "dev.zio" %% "zio-kafka"           % Versions.zioKafka,
      "dev.zio" %% "zio-logging"         % Versions.zioLogging,
      "dev.zio" %% "zio-logging-slf4j"   % Versions.zioLogging,
      "dev.zio" %% "zio-macros"          % Versions.zio,
      "dev.zio" %% "zio-streams"         % Versions.zio
    )

    val zioHttp = Seq(
      "dev.zio" %% "zio-http" % Versions.zioHttp
    )

    val zioJson = Seq(
      "dev.zio" %% "zio-json" % Versions.zioJson
    )

    val logging = Seq(
      "com.lmax"                 % "disruptor"        % Versions.disruptor, // Needed for log4j's AsyncLogger
      "org.apache.logging.log4j" % "log4j-core"       % Versions.log4j,
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % Versions.log4j
    )

    val embeddedKafka = Seq("io.github.embeddedkafka" %% "embedded-kafka" % Versions.kafka)

    val compilerPlugins = Seq(
      "com.hmemcpy" %% "zio-clippy" % Versions.zioClippy
    ).map(compilerPlugin)
  }
}
