package io.scalac.ms.protocol

import zio.json._

final case class TransactionRaw(userId: Long, country: String, amount: BigDecimal)

object TransactionRaw {
  implicit val codec: JsonCodec[TransactionRaw] = DeriveJsonCodec.gen[TransactionRaw]

  lazy val serde = deriveSerde[TransactionRaw]
}
