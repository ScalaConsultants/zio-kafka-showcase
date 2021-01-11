package io.scalac.ms.protocol

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Country(name: String, capital: String, region: String, subregion: String, population: Long)

object Country {
  implicit val codec: Codec[Country] = deriveCodec[Country]
}
