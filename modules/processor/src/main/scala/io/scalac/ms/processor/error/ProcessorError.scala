package io.scalac.ms.processor.error

import scala.util.control.NoStackTrace

sealed trait ProcessorError extends NoStackTrace

object ProcessorError {
  case object CountryApiUnreachable   extends ProcessorError
  case object ResponseExtractionError extends ProcessorError
}
