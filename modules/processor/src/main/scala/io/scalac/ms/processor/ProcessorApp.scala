package io.scalac.ms.processor

import zio._
import zio.clock._
import zio.console._
import zio.blocking._
import zio.duration._
import zio.kafka.serde._
import zio.kafka.consumer._
import zio.kafka.producer._
import zio.logging._
import zio.logging.slf4j._
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import io.scalac.ms.processor.service._
import io.scalac.ms.processor.cache.CountryCache

object ProcessorApp extends App {

  override def run(args: List[String]): URIO[Blocking with Clock with Console, ExitCode] =
    Pipeline.run.provideSomeLayer(appLayer).exitCode

  private lazy val appLayer = {
    val sttpClientLayer = AsyncHttpClientZioBackend.layer()
    val loggingLayer = Slf4jLogger.make { (context, message) =>
      val correlationId = LogAnnotation.CorrelationId.render(context.get(LogAnnotation.CorrelationId))
      "[correlation-id = %s] %s".format(correlationId, message)
    }
    val enrichmentLayer = (loggingLayer ++ CountryCache.live ++ sttpClientLayer) >>> Enrichment.live

    (Clock.live ++ Blocking.live ++ loggingLayer ++ kafkaLayer ++ enrichmentLayer) >>> Pipeline.live
  }

  private lazy val kafkaLayer = {
    val consumerSettings =
      ConsumerSettings(List("localhost:9092"))
        .withGroupId("group-0")
        .withClientId("client")
        .withCloseTimeout(30.seconds)
        .withPollTimeout(10.millis)
        .withProperty("enable.auto.commit", "false")
        .withProperty("auto.offset.reset", "earliest")

    val producerSettings = ProducerSettings(List("localhost:9092"))

    val consumerLayer = Consumer.make(consumerSettings).toLayer
    val producerLayer = Producer.make[Any, Long, String](producerSettings, Serde.long, Serde.string).toLayer

    consumerLayer ++ producerLayer
  }
}
