# zio-kafka-showcase
Example project that demonstrates how to build Kafka-based microservices with Scala and ZIO.

The system is built as a multi-module [Bleep](https://bleep.build/docs/) project.

Modules:

1. [Kafka](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/kafka/src/main/scala/embedded/kafka) module is used to provide an in-memory instance of Kafka, so before starting other services, make sure that Kafka is running.
2. [Protocol](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/protocol/src/main/scala/io/scalac/ms/protocol) module contains messages and its JSON codecs that are used to communicate between microservices.
3. [Producer](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/producer/src/main/scala/io/scalac/ms/producer) module is used to simulate incoming messages that are sent to Kafka.
3. [Processor](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/processor/src/main/scala/io/scalac/ms/processor) module is responsible for consuming, enriching, and producing those enriched messages to the new Kafka topic.

# How to start

0. Install Bleep, following the instructions [here](https://bleep.build/docs/installing/)
1. Start [kafka](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/kafka/src/main/scala/embedded/kafka), by executing `bleep run kafka`
2. Start [producer](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/producer/src/main/scala/io/scalac/ms/producer), by executing `bleep run producer`
3. Start [processor](https://github.com/ScalaConsultants/zio-kafka-showcase/tree/main/modules/processor/src/main/scala/io/scalac/ms/processor), by executing `bleep run processor`

# System Architecture

![Alt text](architecture-diagram.png?raw=true "Title")