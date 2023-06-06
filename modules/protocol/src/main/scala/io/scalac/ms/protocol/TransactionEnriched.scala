package io.scalac.ms.protocol

import zio.json._
import zio.kafka.serde._

final case class TransactionEnriched(userId: Long, country: Country, amount: BigDecimal)

object TransactionEnriched {
  implicit val codec: JsonCodec[TransactionEnriched] = DeriveJsonCodec.gen[TransactionEnriched]

  lazy val serde: Serde[Any, TransactionEnriched] = deriveSerde[TransactionEnriched]
}
