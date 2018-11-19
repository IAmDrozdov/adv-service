package com.app.adv

import cats.effect.IO
import com.app.adv.models._
import com.app.adv.services.advQueries._
import io.circe.generic.auto._
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import shapeless.HNil

object endpoints {
  private final val basePath: Endpoint[IO, HNil] = "api" :: "advertisers"

  val getAdvertiserById: Endpoint[IO, Advertiser] =
    get(basePath :: path[Long]) { id: Long =>
      doIfExists[Advertiser](id, Ok)
    }
  val getAllAdvertisers: Endpoint[IO, Seq[Advertiser]] = get(basePath) {
    val advs = getAllAdvs
    advs.isEmpty match {
      case true => Ok(Seq.empty[Advertiser])
      case false => Ok(advs)
    }
  }
  val deleteAdvertiser: Endpoint[IO, Boolean] = delete(basePath :: path[Long]) {
    id: Long =>
      doIfExists[Boolean](id, { adv: Advertiser => {
        deleteAdv(adv.id.get)
        Ok(true)
      }
      })
  }
  val updateAdvertiser: Endpoint[IO, Boolean] =
    put(basePath :: path[Long] :: jsonBody[Advertiser]) {
      (id: Long, adv: Advertiser) =>
        doIfExists[Boolean](id, { _: Advertiser =>
          updateAdv(id, adv.name, adv.contactName, adv.creditLimit)
          Ok(true)
        })
    }
  val checkCredits: Endpoint[IO, Boolean] =
    post(basePath :: path[Long] :: "validate" :: jsonBody[Deduction]) {
      (id: Long, ded: Deduction) =>
        doIfExists(id, { adv: Advertiser =>
          isValid(adv.creditLimit, ded.amount) match {
            case true => Ok(true)
            case false => Ok(false)
          }
        })
    }
  val deductAmount: Endpoint[IO, Unit] =
    post(basePath :: path[Long] :: "deduct" :: jsonBody[Deduction]) {
      (id: Long, ded: Deduction) =>
        doIfExists[Unit](id, { adv: Advertiser =>
          isValid(adv.creditLimit, ded.amount) match {
            case true =>
              adv.deduct(ded.amount)
              updateAdv(id, adv.name, adv.contactName, adv.creditLimit)
              Ok()
            case false =>
              BadRequest(new RuntimeException)
          }
        })
    }
  val addAdvertiser: Endpoint[IO, Advertiser] =
    post(basePath :: jsonBody[Advertiser]) { adv: Advertiser =>
      adv.creditLimit > 0 && adv.name != "" && adv.contactName != "" match {
        case true => Created(addAdv(adv.name, adv.contactName, adv.creditLimit))
        case false => BadRequest(new RuntimeException)
      }
    }

  private def isValid(credits: Double, amount: Double): Boolean = credits >= amount

  private def doIfExists[A](id: Long, f: Advertiser => Output[A]): Output[A] = {
    val adv = getAdvById(id)
    adv match {
      case None => NotFound(new RuntimeException)
      case Some(adv: Advertiser) => f(adv)
    }
  }
}

