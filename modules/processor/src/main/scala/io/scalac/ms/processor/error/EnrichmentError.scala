package io.scalac.ms.processor.error

import zio.http._

sealed trait EnrichmentError { self =>
  import EnrichmentError._
  override def toString: String =
    self match {
      case CountryApiUnreachable(error)     => s"Country API unreachable: ${error.getMessage}"
      case UnexpectedResponse(status, body) => s"Response error. Status: $status, body: $body"
      case ResponseExtraction(error)        => s"Response extraction error: ${error.getMessage}"
      case ResponseParsing(message)         => s"Response parsing error: $message"
    }
}

object EnrichmentError {
  final case class CountryApiUnreachable(error: Throwable)          extends EnrichmentError
  final case class UnexpectedResponse(status: Status, body: String) extends EnrichmentError
  final case class ResponseExtraction(error: Throwable)             extends EnrichmentError
  final case class ResponseParsing(message: String)                 extends EnrichmentError
}
