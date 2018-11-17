package com.app.adv

import cats.effect.IO
import com.app.adv.models.{Advertiser, Deduction}
import com.app.adv.services.advQueries._
import io.circe.generic.auto._
import io.finch.{Ok, _}
import io.finch.catsEffect._
import io.finch.circe._

object endpoints {
  final val basePath = "api" :: "advertisers"
  val isValid = (credits: Double, amount: Double) => credits >= amount
  

  val getAdvertiserById: Endpoint[IO, Advertiser] = get(basePath :: path[Long]) { id: Long =>
    val adv = getAdvById(id)
    adv match {
      case None => NoContent
      case _ => Ok(adv.get)
    }
  }
  val getAllAdvertisers: Endpoint[IO, Seq[Advertiser]] = get(basePath) {
    val advs = getAllAdvs
    advs.isEmpty match {
      case true => NoContent
      case _ => Ok(advs)
    }
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
      case None => NoContent
      case _ => updateAdv(id, adv.name, adv.contactName, adv.creditLimit)
    }
    Ok()
  }
  val checkCredits: Endpoint[IO, Boolean] = post(basePath :: path[Long] :: "validate" :: jsonBody[Deduction]) {
    (id: Long, ded: Deduction) =>
      val adv = getAdvById(id)
      adv match {
        case None => NoContent
        case _ =>
          isValid(adv.get.creditLimit, ded.amount) match {
            case true => Ok(true)
            case false => Ok(false)
          }
      }
  }
  val deductAmount: Endpoint[IO, Unit] = post(basePath :: path[Long] :: "deduct" :: jsonBody[Deduction]) {
    (id: Long, ded: Deduction) =>
      val adv = getAdvById(id)
      adv match {
        case None => NoContent
        case _ =>
          if (isValid(adv.get.creditLimit, ded.amount))
      }
  }
  val addAdvertiser: Endpoint[IO, Advertiser] = post(basePath :: jsonBody[Advertiser]) { adv: Advertiser =>
    adv.creditLimit > 0 match {
      case true => Created(addAdv(adv.name, adv.contactName, adv.creditLimit))
      case false => BadRequest(new IllegalArgumentException)
    }
  }
}
