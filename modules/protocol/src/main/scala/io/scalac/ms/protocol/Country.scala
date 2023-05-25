package io.scalac.ms.protocol

import zio.json._

final case class Country(name: String, capital: String, region: String, subregion: String, population: Long)

object Country {
  implicit val codec: JsonCodec[Country] = DeriveJsonCodec.gen[Country]
}
