package io.scalac.ms.processor.cache

import zio._
import io.scalac.ms.protocol.Country

object CountryCache {

  trait Service {
    def get(countryName: String): UIO[Option[Country]]
    def put(country: Country): UIO[Unit]
  }

  def get(countryName: String): URIO[CountryCache, Option[Country]] =
    ZIO.accessM(_.get.get(countryName))

  def put(country: Country): URIO[CountryCache, Unit] =
    ZIO.accessM(_.get.put(country))

  lazy val live: ULayer[CountryCache] =
    Ref.make(Map.empty[String, Country]).map(new Live(_)).toLayer

  final class Live(ref: Ref[Map[String, Country]]) extends Service {
    override def get(countryName: String): UIO[Option[Country]] =
      for {
        cache  <- ref.get
        result <- ZIO.succeed(cache.get(countryName))
      } yield result

    override def put(country: Country): UIO[Unit] =
      ref.update(_ + (country.name -> country))
  }
}
