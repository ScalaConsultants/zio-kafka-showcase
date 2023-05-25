package io.scalac.ms.processor.config

import zio.config.magnolia._

final case class AppConfig(consumer: AppConfig.Consumer, producer: AppConfig.Producer, enrichment: AppConfig.Enrichment)
object AppConfig {
  lazy val config = deriveConfig[AppConfig]

  final case class Consumer(bootstrapServers: String, topic: String, groupId: String) { self =>
    lazy val brokers: List[String] = self.bootstrapServers.split(",").toList
  }

  final case class Producer(bootstrapServers: String, topic: String) { self =>
    lazy val brokers: List[String] = self.bootstrapServers.split(",").toList
  }

  final case class Enrichment(host: String)
}
