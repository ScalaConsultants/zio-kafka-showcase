package io.scalac.ms.processor

import zio.Has
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.consumer.Consumer
import zio.kafka.producer.Producer
import zio.logging.Logging

package object service {
  type Enrichment = Has[Enrichment.Service]
  type Pipeline   = Has[Pipeline.Service]
  type PipelineEnvironment = Clock
    with Blocking
    with Logging
    with Consumer
    with Producer[Any, Long, String]
    with Enrichment
}
