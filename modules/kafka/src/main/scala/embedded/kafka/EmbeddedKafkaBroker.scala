package embedded.kafka

import io.github.embeddedkafka._
import org.slf4j.LoggerFactory

object EmbeddedKafkaBroker extends App with EmbeddedKafka {
  val log = LoggerFactory.getLogger(this.getClass)

  val port = 9092

  implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = port, zooKeeperPort = 5555)

  val embeddedKafkaServer: EmbeddedK = EmbeddedKafka.start()

  createCustomTopic(topic = "transactions.raw", partitions = 3)
  createCustomTopic(topic = "transactions.enriched", partitions = 3)
  log.info(s"Kafka running: localhost:$port")

  embeddedKafkaServer.broker.awaitShutdown()
}
