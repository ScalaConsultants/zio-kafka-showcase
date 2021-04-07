package io.scalac.ms.producer

import zio._
import zio.stream._
import zio.blocking._
import zio.kafka.serde._
import zio.kafka.producer._
import zio.logging._
import zio.logging.slf4j._
import io.circe.syntax._
import org.apache.kafka.clients.producer.ProducerRecord

object ProducerApp extends App {

  type ProducerEnv = Any with Blocking with Producer[Any, Long, String] with Logging

  override def run(args: List[String]) =
    AppConfig
      .load()
      .flatMap(config => program(config).provideSomeLayer[Any with Blocking](createLayer(config)))
      .exitCode

  private def program(appConfig: AppConfig): ZIO[ProducerEnv, Throwable, Unit] =
    ZStream
      .fromIterable(EventGenerator.transactions)
      .map(transaction => new ProducerRecord(appConfig.topic, transaction.userId, transaction.asJson.toString))
      .mapM { producerRecord =>
        log.info(s"Producing $producerRecord to Kafka...") *>
          Producer.produce[Any, Long, String](producerRecord)
      }
      .runDrain

  private def createLayer(appConfig: AppConfig) = {
    val producerSettings = ProducerSettings(appConfig.brokers)
    val producerLayer    = Producer.make[Any, Long, String](producerSettings, Serde.long, Serde.string).toLayer

    val loggingLayer = Slf4jLogger.make { (context, message) =>
      val correlationId = context(LogAnnotation.CorrelationId)
      "[correlation-id = %s] %s".format(correlationId, message)
    }

    loggingLayer ++ producerLayer
  }
}
