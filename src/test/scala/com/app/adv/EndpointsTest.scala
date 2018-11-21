package com.app.adv

import com.app.adv.endpoints._
import com.app.adv.models.{Advertiser, Deduction}
import com.app.adv.services.advQueries.getAdvById
import com.twitter.finagle.http.Status
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._
import org.scalatest.FunSuite

class EndpointsTest extends FunSuite {
  private val basePath = "/api/advertisers"
  private val rightAdv1 = Advertiser("Sasha", "Sasha", 1D)
  private val rightAdv2 = Advertiser("Gleb", "Gleb", 13D)

  test("Using db for test") {
    assert(
      ConfigFactory.load().getConfig("adv-service.db").getString("url")
        .equals("jdbc:h2:mem:adv-service-test;DB_CLOSE_DELAY=-1")
    )
  }

  test("Get all advertisers when empty") {
    assert(
      getAllAdvertisers(Input.get(basePath))
        .awaitOutputUnsafe().map(_.status).contains(Status.NoContent)
    )
  }
  test("Add advertiser") {
    val badAdv1 = Advertiser("", "No Name", 1D)
    val badAdv2 = Advertiser("No surname", "", 1D)
    val badAdv3 = Advertiser("Negative", "Credits", -1D)

    val rightAdv1Output =
      addAdvertiser(Input.post(basePath).withBody[Application.Json](rightAdv1)).awaitOutputUnsafe()
    val rightAdv2Output =
      addAdvertiser(Input.post(basePath).withBody[Application.Json](rightAdv2)).awaitOutputUnsafe()
    assert(
      rightAdv1Output.map(_.value).contains(rightAdv1.copy(id = Some(1))) &&
        rightAdv1Output.map(_.status).contains(Status.Created)
    )
    assert(
      rightAdv2Output.map(_.value).contains(rightAdv2.copy(id = Some(2))) &&
        rightAdv2Output.map(_.status).contains(Status.Created)
    )
    assert(
      addAdvertiser(Input.post(basePath).withBody[Application.Json](badAdv1)).awaitOutputUnsafe()
        .map(_.status).contains(Status.BadRequest)
    )
    assert(
      addAdvertiser(Input.post(basePath).withBody[Application.Json](badAdv2)).awaitOutputUnsafe()
        .map(_.status).contains(Status.BadRequest)
    )
    assert(
      addAdvertiser(Input.post(basePath).withBody[Application.Json](badAdv3)).awaitOutputUnsafe()
        .map(_.status).contains(Status.BadRequest)
    )
  }

  test("Get all advertisers when not empty") {
    val rightOutput = getAllAdvertisers(Input.get(basePath)).awaitOutputUnsafe()
    assert(
      rightOutput.map(_.status).contains(Status.Ok) &&
        rightOutput.map(_.value).map(_.length).contains(2)
    )
  }
  test("Get advertiser by ID") {
    val rightAdv1Output =
      getAdvertiserById(Input.get(basePath + "/1")).awaitOutputUnsafe()
    val rightAdv2Output =
      getAdvertiserById(Input.get(basePath + "/2")).awaitOutputUnsafe()

    assert(
      rightAdv1Output.map(_.value).contains(rightAdv1.copy(id = Some(1))) &&
        rightAdv1Output.map(_.status).contains(Status.Ok)
    )
    assert(
      rightAdv2Output.map(_.value).contains(rightAdv2.copy(id = Some(2))) &&
        rightAdv2Output.map(_.status).contains(Status.Ok)
    )
  }

  test("Delete advertiser") {
    val rightOutput = deleteAdvertiser(Input.delete(basePath + "/2")).awaitOutputUnsafe()
    val badOutput = deleteAdvertiser(Input.delete(basePath + "/100")).awaitOutputUnsafe()
    assert(
      rightOutput.map(_.status).contains(Status.Ok) &&
        badOutput.map(_.status).contains(Status.NotFound)
    )
  }

  test("Update advertiser") {
    val url = basePath + "/1"

    val rightOutput = updateAdvertiser(Input.put(url)
      .withBody[Application.Json](rightAdv2.copy(id = Some(1)))).awaitOutputUnsafe()

    val badOutput1 = updateAdvertiser(Input.put(url)
      .withBody[Application.Json](rightAdv1.copy(contactName = ""))).awaitOutputUnsafe()
    val badOutput2 = updateAdvertiser(Input.put(url)
      .withBody[Application.Json](rightAdv1.copy(id = Some(2)))).awaitOutputUnsafe()
    val badOutput3 = updateAdvertiser(Input.put(url)
      .withBody[Application.Json](rightAdv1.copy(creditLimit = -100D))).awaitOutputUnsafe()
    val badOutput4 = updateAdvertiser(Input.put(url)
      .withBody[Application.Json](rightAdv1.copy(name = ""))).awaitOutputUnsafe()

    assert(
      rightOutput.map(_.status).contains(Status.Ok) &&
        getAdvById(1).contains(rightAdv2.copy(id = Some(1)))
    )
    assert(
      badOutput1.map(_.status).contains(Status.BadRequest) &&
        badOutput2.map(_.status).contains(Status.BadRequest) &&
        badOutput3.map(_.status).contains(Status.BadRequest) &&
        badOutput4.map(_.status).contains(Status.BadRequest)
    )
  }

  test("Validate advertiser") {
    //13
    assert(
      checkCredits(Input.post(basePath + "/1/validate")
        .withBody[Application.Json](Deduction(10D))).awaitOutputUnsafe()
        .map(_.value).contains(true)
    )

    assert(
      checkCredits(Input.post(basePath + "/1/validate")
        .withBody[Application.Json](Deduction(100D))).awaitOutputUnsafe()
        .map(_.value).contains(false)
    )
  }

  test("Deduct amount") {
    //13
    assert(
      deductAmount(Input.post(basePath + "/1/deduct")
        .withBody[Application.Json](Deduction(10D))).awaitOutputUnsafe()
        .map(_.status).contains(Status.Ok) &&
        getAdvById(1).contains(rightAdv2.copy(creditLimit = 3D, id = Some(1)))
    )
    // 3
    assert(
      deductAmount(Input.post(basePath + "/1/deduct")
        .withBody[Application.Json](Deduction(100D))).awaitOutputUnsafe()
        .map(_.status).contains(Status.BadRequest)
    )
  }

}