package io.scalac.ms.protocol

import io.circe.Codec
import io.circe.generic.semiauto._

final case class TransactionEnriched(userId: Long, country: Country, amount: BigDecimal)

object TransactionEnriched {
  implicit val codec: Codec[TransactionEnriched] = deriveCodec[TransactionEnriched]
}
