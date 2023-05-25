package io.scalac.ms.processor.cache

import io.scalac.ms.protocol.Country
import zio._
import zio.macros._

@accessible
trait CountryCache {
  def get(countryName: String): UIO[Option[Country]]

  def put(country: Country): UIO[Unit]
}

final case class CountryCacheLive(ref: Ref[Map[String, Country]]) extends CountryCache { self =>
  override def get(countryName: String): UIO[Option[Country]] =
    (for {
      _      <- ZIO.logInfo(s"Getting country details from cache.")
      cache  <- self.ref.get
      result <- ZIO.succeed(cache.get(countryName))
    } yield result) @@ ZIOAspect.annotated("countryName", countryName)

  override def put(country: Country): UIO[Unit] =
    (ZIO.logInfo("Caching country.") *>
      self.ref.update(_ + (country.name -> country))) @@ ZIOAspect.annotated("countryName", country.name)
}
object CountryCacheLive {
  lazy val layer: ULayer[CountryCache] =
    ZLayer {
      Ref.make(Map.empty[String, Country]).map(CountryCacheLive(_))
    }
}
