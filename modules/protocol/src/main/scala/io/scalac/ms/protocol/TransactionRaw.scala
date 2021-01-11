package io.scalac.ms.protocol

import io.circe.Codec
import io.circe.generic.semiauto._

final case class TransactionRaw(userId: Long, country: String, amount: BigDecimal)

object TransactionRaw {
  implicit val codec: Codec[TransactionRaw] = deriveCodec[TransactionRaw]
}
