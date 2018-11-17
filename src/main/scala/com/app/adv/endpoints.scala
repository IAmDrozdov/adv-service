package com.app.adv

import cats.effect.IO
import com.app.adv.models.Advertiser
import com.app.adv.services.advQueries._
import io.circe.generic.auto._
import io.finch.{Ok, _}
import io.finch.catsEffect._
import io.finch.circe._

object endpoints {
  final val basePath = "api" :: "advertisers"


  val getAdvertiserById: Endpoint[IO, Option[Advertiser]] = get(basePath :: path[Long]) { id: Long =>
    val adv = getAdvById(id)
    adv match {
      case None => NoContent
      case _ => Ok(adv)
    }
  }

  val getAllAdvertisers: Endpoint[IO, Seq[Advertiser]] = get(basePath) {
    val advs = getAllAdvs
    if (advs.isEmpty) NoContent else Ok(advs)
  }

  val deleteAdvertiser: Endpoint[IO, Boolean] = delete(basePath :: path[Long]) { id: Long =>
    val adv = getAdvById(id)
    adv match {
      case None => NoContent
      case _ =>
        deleteAdv(id)
        Ok(true)
    }
  }

  val updateAdvertiser: Endpoint[IO, Unit] = put(basePath :: path[Long] :: jsonBody[Advertiser]) { (id: Long, adv: Advertiser) =>
    val advToChange = getAdvById(id)
    advToChange match {
      case None => BadRequest(new IllegalArgumentException)
      case _ => updateAdv(id, adv.name, adv.contactName, adv.creditLimit)
    }
    Ok()
  }

  //    val checkCredits: Endpoint[IO, Unit] = get(basePath :: path[Int] :: "validate") {
  //
  //    }
  //
  //    val deductAmount: Endpoint[IO, Unit] = get(basePath :: path[Int] :: "deduct" :: jsonBody[Deduction]) {
  //
  //    }

  val addAdvertiser: Endpoint[IO, Advertiser] = post(basePath :: jsonBody[Advertiser]) { adv: Advertiser =>
    if (adv.creditLimit > 0)
      Created(addAdv(adv.name, adv.contactName, adv.creditLimit))
    else
      BadRequest(new IllegalArgumentException)
  }
}
