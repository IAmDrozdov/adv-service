package com.app.adv

object models {

  case class Deduction(amount: Double)

  case class Advertiser(
      name: String,
      contactName: String,
      var creditLimit: Double,
      id: Option[Long] = None
    ) {
    def deduct(amount: Double): Unit = {
      creditLimit = creditLimit - amount
    }
  }

}
