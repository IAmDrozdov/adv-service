package com.app.adv

import com.app.adv.endpoints._
import com.twitter.finagle.Http
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

object Main extends App {

  val api =
    (
      addAdvertiser :+:
      getAllAdvertisers :+:
      getAdvertiserById :+:
      deleteAdvertiser :+:
      updateAdvertiser :+:
      checkCredits :+:
      deductAmount).handle {
      case e: Exception => InternalServerError(e)
    }

  Await.ready(
    Http.server
      .serve(":8080", api.toServiceAs[Application.Json]))
}