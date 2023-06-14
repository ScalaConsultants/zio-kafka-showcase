package io.scalac.ms.producer

import zio.config.magnolia._

final case class AppConfig(bootstrapServers: List[String], topic: String)

object AppConfig {
  lazy val config = deriveConfig[AppConfig]
}
