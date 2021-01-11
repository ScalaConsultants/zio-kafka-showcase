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
import org.apache.kafka.clients.producer.ProducerRecord

object Pipeline {

  trait Service {
    def run(): IO[Throwable, Unit]
  }

  def run: ZIO[Pipeline, Throwable, Unit] =
    ZIO.accessM(_.get.run())

  lazy val live: ZLayer[PipelineEnvironment, Nothing, Pipeline] = ZLayer.fromFunction { env =>
    new Service {
      override def run(): IO[Throwable, Unit] =
        (log.info("Starting processing pipeline") *>
          Consumer
            .subscribeAnd(Subscription.topics("transactions.raw"))
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
                  log.info(s"Deserialization error $error") *> ZIO.succeed(committableRecord)
              }
            }
            .map(_.offset)
            .aggregateAsync(Consumer.offsetBatches)
            .mapM(_.commit)
            .runDrain)
          .provide(env)
    }
  }

  private def toProducerRecord(transaction: TransactionEnriched): ProducerRecord[Long, String] =
    new ProducerRecord("transactions.enriched", transaction.userId, transaction.asJson.toString)
}
