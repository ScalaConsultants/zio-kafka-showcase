package io.scalac.ms.producer

import io.scalac.ms.protocol.TransactionRaw
import zio._
import zio.config.typesafe._
import zio.kafka.producer._
import zio.kafka.serde._
import zio.logging.backend._
import zio.stream._

object ProducerApp extends ZIOAppDefault {
  override val bootstrap =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath()) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override val run =
    (for {
      topic <- ZIO.config(AppConfig.config.map(_.topic))
      _ <- ZStream
            .fromIterable(EventGenerator.transactions)
            .mapZIO { transaction =>
              (ZIO.logInfo("Producing transaction to Kafka...") *>
                Producer.produce(
                  topic = topic,
                  key = transaction.userId,
                  value = transaction,
                  keySerializer = Serde.long,
                  valueSerializer = TransactionRaw.serde
                )) @@ ZIOAspect.annotated("userId", transaction.userId.toString)
            }
            .runDrain
    } yield ()).provide(
      producerSettings,
      Producer.live
    )

  private lazy val producerSettings =
    ZLayer {
      ZIO.config(AppConfig.config.map(_.bootstrapServers)).map(ProducerSettings(_))
    }
}
