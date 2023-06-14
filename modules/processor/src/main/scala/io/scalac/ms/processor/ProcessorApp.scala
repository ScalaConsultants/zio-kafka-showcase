package io.scalac.ms.processor

import io.scalac.ms.processor.config.AppConfig
import io.scalac.ms.processor.service._
import io.scalac.ms.protocol.{ TransactionEnriched, TransactionRaw }
import zio._
import zio.config.typesafe._
import zio.http._
import zio.kafka.consumer._
import zio.kafka.consumer.diagnostics.Diagnostics
import zio.kafka.producer._
import zio.kafka.serde._
import zio.logging.backend._

object ProcessorApp extends ZIOAppDefault {
  override val bootstrap =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath()) >>> Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override val run =
    (for {
      _         <- ZIO.logInfo("Starting processing pipeline")
      appConfig <- ZIO.config(AppConfig.config)
      _ <- Consumer
            .plainStream(
              subscription = Subscription.topics(appConfig.consumer.topic),
              keyDeserializer = Serde.long,
              valueDeserializer = TransactionRaw.serde
            )
            .mapZIO { committableRecord =>
              val offset = committableRecord.offset
              (for {
                transaction <- Enrichment.enrich(committableRecord.value)
                _           <- ZIO.logInfo("Producing enriched transaction to Kafka...")
                _ <- Producer.produce(
                      topic = appConfig.producer.topic,
                      key = transaction.userId,
                      value = transaction,
                      keySerializer = Serde.long,
                      valueSerializer = TransactionEnriched.serde
                    )
              } yield offset).catchAll { error =>
                ZIO.logError(s"Got error while processing: $error") *> ZIO.succeed(offset)
              } @@ ZIOAspect.annotated("userId", committableRecord.value.userId.toString)
            }
            .aggregateAsync(Consumer.offsetBatches)
            .mapZIO(_.commit)
            .runDrain
            .catchAll(error => ZIO.logError(s"Got error while consuming: $error"))
    } yield ()).provide(
      EnrichmentLive.layer,
      CountryCacheLive.layer,
      Client.default,
      consumerSettings,
      ZLayer.succeed(Diagnostics.NoOp),
      Consumer.live,
      producerSettings,
      Producer.live
    )

  private lazy val producerSettings =
    ZLayer {
      ZIO.config(AppConfig.config.map(_.producer.bootstrapServers)).map(ProducerSettings(_))
    }

  private lazy val consumerSettings =
    ZLayer {
      ZIO.config(AppConfig.config.map(_.consumer)).map { consumer =>
        ConsumerSettings(consumer.bootstrapServers)
          .withGroupId(consumer.groupId)
          .withClientId("client")
          .withCloseTimeout(30.seconds)
          .withPollTimeout(10.millis)
          .withProperty("enable.auto.commit", "false")
          .withProperty("auto.offset.reset", "earliest")
      }
    }
}
