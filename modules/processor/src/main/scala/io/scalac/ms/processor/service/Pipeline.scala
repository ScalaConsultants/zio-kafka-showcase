package io.scalac.ms.processor.service

import io.scalac.ms.protocol.{ TransactionEnriched, TransactionRaw }
import zio._
import zio.clock._
import zio.blocking._
import zio.kafka.serde._
import zio.kafka.consumer._
import zio.kafka.producer._
import zio.logging._
import io.circe.syntax._
import io.circe.parser.decode
import io.scalac.ms.processor.config.AppConfig
import org.apache.kafka.clients.producer.ProducerRecord

object Pipeline {

  trait Service {
    def run(): IO[Throwable, Unit]
  }

  def run: ZIO[Pipeline, Throwable, Unit] =
    ZIO.accessM(_.get.run())

  def live(appConfig: AppConfig): ZLayer[PipelineEnvironment, Nothing, Pipeline] =
    ZLayer.fromFunction { env =>
      new Service {
        override def run(): IO[Throwable, Unit] =
          (log.info("Starting processing pipeline") *>
            Consumer
              .subscribeAnd(Subscription.topics(appConfig.consumer.topic))
              .plainStream(Serde.long, Serde.string)
              .mapM { committableRecord =>
                val parsed = decode[TransactionRaw](committableRecord.value)

                parsed match {
                  case Right(transactionRaw) =>
                    Enrichment
                      .enrich(transactionRaw)
                      .map(toProducerRecord)
                      .flatMap(Producer.produce[Any, Long, String](_))
                      .as(committableRecord)
                  case Left(error) =>
                    log.info(s"Deserialization error $error").as(committableRecord)
                }
              }
              .map(_.offset)
              .aggregateAsync(Consumer.offsetBatches)
              .mapM(_.commit)
              .runDrain)
            .provide(env)

        private def toProducerRecord(transaction: TransactionEnriched): ProducerRecord[Long, String] =
          new ProducerRecord(appConfig.producer.topic, transaction.userId, transaction.asJson.toString)
      }
    }
}
