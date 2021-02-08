# zio-kafka-showcase
Example project that demonstrates how to build Kafka based microservices with Scala and ZIO.

System is build as multi-module sbt project.

Modules:

1. [Kafka](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/kafka/src/main/scala/embedded/kafka) module is used to provide an in-memory instance of Kafka, so before starting other services, make sure that Kafka is running.
2. [Protocol](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/protocol/src/main/scala/io/scalac/ms/protocol) module contains messages and its JSON codecs that are used to communicate between microservices.
3. [Producer](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/producer/src/main/scala/io/scalac/ms/producer) module is used to simulate incoming messages that are sent to Kafka.
3. [Processor](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/processor/src/main/scala/io/scalac/ms/processor) module is responsible for consuming, enriching, and producing those enriched messages to the new Kafka topic.

# How to start
1. Start [kafka](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/kafka/src/main/scala/embedded/kafka)
2. Start [producer](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/producer/src/main/scala/io/scalac/ms/producer)
3. Start [processor](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/processor/src/main/scala/io/scalac/ms/processor)

# System Architecture

![Alt text](architecture-diagram.png?raw=true "Title")