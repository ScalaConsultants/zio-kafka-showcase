package io.scalac.ms.producer

import zio.config.magnolia._

final case class AppConfig(bootstrapServers: String, topic: String) { self =>
  lazy val brokers: List[String] = self.bootstrapServers.split(",").toList
}

object AppConfig {
  lazy val config = deriveConfig[AppConfig]
}
