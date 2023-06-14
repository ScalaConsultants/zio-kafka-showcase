package io.scalac.ms.processor.config

import zio.config.magnolia._

final case class AppConfig(consumer: AppConfig.Consumer, producer: AppConfig.Producer, enrichment: AppConfig.Enrichment)
object AppConfig {
  lazy val config = deriveConfig[AppConfig]

  final case class Consumer(bootstrapServers: List[String], topic: String, groupId: String)

  final case class Producer(bootstrapServers: List[String], topic: String)

  final case class Enrichment(host: String)
}
