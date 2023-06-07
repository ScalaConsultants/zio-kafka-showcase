package io.scalac.ms.processor.service

import io.scalac.ms.processor.config.AppConfig
import io.scalac.ms.processor.error.EnrichmentError
import io.scalac.ms.protocol._
import zio._
import zio.http._
import zio.json._
import zio.macros._

@accessible
trait Enrichment {
  def enrich(transactionRaw: TransactionRaw): IO[EnrichmentError, TransactionEnriched]
}

final case class EnrichmentLive(countryCache: CountryCache, httpClient: Client) extends Enrichment { self =>

  override def enrich(
    transactionRaw: TransactionRaw
  ): IO[EnrichmentError, TransactionEnriched] = {
    val TransactionRaw(userId, countryName, amount) = transactionRaw
    for {
      _       <- ZIO.logInfo("Enriching raw transaction.")
      country <- self.countryCache.get(countryName).someOrElseZIO(self.fetchAndCacheCountryDetails(countryName))
    } yield TransactionEnriched(userId, country, amount)
  }

  private def fetchAndCacheCountryDetails(
    countryName: String
  ): IO[EnrichmentError, Country] =
    for {
      _       <- ZIO.logInfo(s"Cache miss. Fetching country details from external API.")
      country <- self.fetchCountryDetails(countryName)
      _       <- self.countryCache.put(country)
    } yield country

  private def fetchCountryDetails(
    countryName: String
  ): IO[EnrichmentError, Country] =
    for {
      host <- ZIO.config(AppConfig.config.map(_.enrichment.host)).orDie
      response <- (self.httpClient @@ ZClientAspect.requestLogging())
                   .scheme(Scheme.HTTPS)
                   .host(host)
                   .path("/v2/name")
                   .get(countryName)
                   .mapError(EnrichmentError.CountryApiUnreachable)
      responseBody <- response.body.asString.mapError(EnrichmentError.ResponseExtraction)
      _ <- ZIO
            .fail(EnrichmentError.UnexpectedResponse(response.status, responseBody))
            .when(response.status != Status.Ok)
      country <- ZIO
                  .fromEither(responseBody.fromJson[NonEmptyChunk[Country]])
                  .mapBoth(EnrichmentError.ResponseParsing, _.head)
    } yield country
}

object EnrichmentLive {
  lazy val layer: URLayer[CountryCache with Client, Enrichment] = ZLayer.fromFunction(EnrichmentLive(_, _))
}
