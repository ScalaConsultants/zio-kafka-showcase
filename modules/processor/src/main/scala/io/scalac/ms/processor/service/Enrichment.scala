package io.scalac.ms.processor.service

import zio._
import zio.logging._
import sttp.model.Uri
import sttp.client.circe._
import sttp.client.{ basicRequest, UriContext }
import sttp.client.asynchttpclient.zio.SttpClient
import io.scalac.ms.protocol._
import io.scalac.ms.processor.cache.CountryCache
import io.scalac.ms.processor.error.ProcessorError
import io.scalac.ms.processor.error.ProcessorError._

object Enrichment {

  trait Service {
    def enrich(transactionRaw: TransactionRaw): IO[ProcessorError, TransactionEnriched]
  }

  def enrich(transactionRaw: TransactionRaw): ZIO[Enrichment, ProcessorError, TransactionEnriched] =
    ZIO.accessM(_.get.enrich(transactionRaw))

  lazy val live: ZLayer[
    Logging with CountryCache with SttpClient,
    Nothing,
    Enrichment
  ] = ZLayer.fromFunction { env =>
    new Service {
      override def enrich(
        transactionRaw: TransactionRaw
      ): IO[ProcessorError, TransactionEnriched] =
        (for {
          _       <- log.info(s"Getting country details from cache for ${transactionRaw.country}.")
          country <- CountryCache.get(transactionRaw.country)
          result <- country.fold(
                     fetchAndCacheCountryDetails(transactionRaw.country)
                   )(ZIO.succeed(_))
        } yield toTransactionEnriched(
          transactionRaw,
          result
        )).provide(env)
    }
  }

  private def fetchAndCacheCountryDetails(
    countryName: String
  ): ZIO[Logging with CountryCache with SttpClient, ProcessorError, Country] =
    for {
      _       <- log.info(s"Cache miss. Fetching country details from external API for ${countryName}.")
      country <- fetchCountryDetails(countryName)
      _       <- CountryCache.put(country)
    } yield country

  private def fetchCountryDetails(
    countryName: String
  ): ZIO[SttpClient, ProcessorError, Country] =
    for {
      req <- ZIO.succeed(
              basicRequest.get(urlOf(countryName)).response(asJson[List[Country]])
            )
      res <- SttpClient.send(req).orElseFail(CountryApiUnreachable)
      country <- res.body.fold(
                  _ => ZIO.fail(ResponseExtractionError),
                  res => ZIO.succeed(res.head)
                )
    } yield country

  private def urlOf(countryName: String): Uri =
    uri"https://restcountries.eu/rest/v2/name/$countryName"

  private def toTransactionEnriched(transactionRaw: TransactionRaw, country: Country) =
    TransactionEnriched(transactionRaw.userId, country, transactionRaw.amount)
}
