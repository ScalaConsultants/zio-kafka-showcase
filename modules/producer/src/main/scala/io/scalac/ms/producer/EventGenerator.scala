package io.scalac.ms.producer

import io.scalac.ms.protocol.TransactionRaw

object EventGenerator {

  lazy val transactions = List(
    TransactionRaw(1, "Serbia", BigDecimal(12.99)),
    TransactionRaw(1, "Serbia", BigDecimal(23.99)),
    TransactionRaw(1, "Serbia", BigDecimal(11.99)),
    TransactionRaw(2, "Serbia", BigDecimal(24.99)),
    TransactionRaw(2, "Serbia", BigDecimal(31.99)),
    TransactionRaw(3, "Poland", BigDecimal(99.99)),
    TransactionRaw(3, "Poland", BigDecimal(11.99)),
    TransactionRaw(3, "Poland", BigDecimal(99.99)),
    TransactionRaw(3, "Poland", BigDecimal(11.99)),
    TransactionRaw(4, "Poland", BigDecimal(22.99)),
    TransactionRaw(5, "Germany", BigDecimal(22.99)),
    TransactionRaw(5, "Germany", BigDecimal(69.99)),
    TransactionRaw(6, "Belgium", BigDecimal(22.99)),
    TransactionRaw(7, "Austria", BigDecimal(99.99)),
    TransactionRaw(7, "Austria", BigDecimal(99.99)),
    TransactionRaw(7, "Austria", BigDecimal(99.99)),
    TransactionRaw(8, "Austria", BigDecimal(99.99)),
    TransactionRaw(9, "Sweden", BigDecimal(123.99)),
    TransactionRaw(10, "Sweden", BigDecimal(100.00)),
    TransactionRaw(10, "Sweden", BigDecimal(200.00)),
    TransactionRaw(10, "Sweden", BigDecimal(100.00))
  )
}
