package io.scalac.ms.processor.config

import zio._
import zio.config.read
import zio.config.magnolia.DeriveConfigDescriptor
import zio.config.typesafe.TypesafeConfigSource
import com.typesafe.config.ConfigFactory

final case class AppConfig(consumer: Consumer, producer: Producer, enrichmentConfig: EnrichmentConfig)

final case class Consumer(bootstrapServers: String, topic: String, groupId: String) {
  def brokers: List[String] = bootstrapServers.split(",").toList
}

final case class Producer(bootstrapServers: String, topic: String) {
  def brokers: List[String] = bootstrapServers.split(",").toList
}

final case class EnrichmentConfig(baseEndpoint: String)

object AppConfig {
  private val descriptor = DeriveConfigDescriptor.descriptor[AppConfig]

  def load(): Task[AppConfig] =
    for {
      rawConfig    <- ZIO.effect(ConfigFactory.load().getConfig("processor"))
      configSource <- ZIO.fromEither(TypesafeConfigSource.fromTypesafeConfig(rawConfig))
      config       <- ZIO.fromEither(read(AppConfig.descriptor.from(configSource)))
    } yield config
}
