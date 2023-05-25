package io.scalac.ms.protocol

import zio.json._

final case class TransactionEnriched(userId: Long, country: Country, amount: BigDecimal)

object TransactionEnriched {
  implicit val codec: JsonCodec[TransactionEnriched] = DeriveJsonCodec.gen[TransactionEnriched]

  lazy val serde = deriveSerde[TransactionEnriched]
}
