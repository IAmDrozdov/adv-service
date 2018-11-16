package com.app.adv

import cats.effect.IO
import io.circe.generic.auto._
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._

object endpoints {
  final val basePath = "api" :: "advertiser"

  val homepage: Endpoint[IO, String] = get(basePath) {
    Ok("Hello!")
  }
  //
  //  val getAdvertiserById: Endpoint[IO, Option[Advertiser]] = get(basePath :: path[Int]) {
  //    Ok(Advertiser("sa", "sa", 2))
  //  }
  //
  //  val getAllAdvertisers: Endpoint[IO, Unit] = get(basePath :: "all") {
  //
  //  }
  //
  //  val deleteAdvertiser: Endpoint[IO, Unit] = delete(basePath :: path[Int]) {
  //
  //  }
  //
  //  val updateAdvertiser: Endpoint[IO, String] = put(basePath :: path[Int] :: jsonBody[Advertiser]) { (id: Int, adv: Advertiser) =>
  //    Ok(adv.name + id)
  //  }
  //
  //  val checkCredits: Endpoint[IO, Unit] = get(basePath :: path[Int] :: "validate") {
  //
  //  }
  //
  //  val deductAmount: Endpoint[IO, Unit] = get(basePath :: path[Int] :: "deduct" :: path[Int]) {
  //
  //  }
  //
  //  val addAdvertiser: Endpoint[IO, Advertiser] = post(basePath :: jsonBody[Advertiser]) { a: Advertiser =>
  //    Created(a)
  //  }
}
