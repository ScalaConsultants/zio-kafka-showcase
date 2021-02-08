package io.scalac.ms.producer

import zio._
import zio.stream._
import zio.blocking._
import zio.kafka.serde._
import zio.kafka.producer._
import zio.logging._
import zio.logging.slf4j._
import io.circe.syntax._
import io.scalac.ms.protocol._
import org.apache.kafka.clients.producer.ProducerRecord

object ProducerApp extends App {

  override def run(args: List[String]) =
    program.provideSomeLayer[Any with Blocking](appLayer).exitCode

  private lazy val program =
    ZStream
      .fromIterable(EventGenerator.transactions)
      .map(toProducerRecord)
      .mapM { producerRecord =>
        log.info(s"Producing $producerRecord to Kafka...") *>
          Producer.produce[Any, Long, String](producerRecord)
      }
      .runDrain

  private lazy val appLayer = {
    val producerSettings = ProducerSettings(List("localhost:9092"))
    val producerLayer    = Producer.make[Any, Long, String](producerSettings, Serde.long, Serde.string).toLayer

    val loggingLayer = Slf4jLogger.make { (context, message) =>
      val correlationId = LogAnnotation.CorrelationId.render(
        context.get(LogAnnotation.CorrelationId)
      )
      "[correlation-id = %s] %s".format(correlationId, message)
    }

    loggingLayer ++ producerLayer
  }

  private def toProducerRecord(transactionRaw: TransactionRaw): ProducerRecord[Long, String] =
    new ProducerRecord("transactions.raw", transactionRaw.userId, transactionRaw.asJson.toString)
}
