package io.scalac.ms.processor

import zio.Has

package object cache {
  type CountryCache = Has[CountryCache.Service]
}
