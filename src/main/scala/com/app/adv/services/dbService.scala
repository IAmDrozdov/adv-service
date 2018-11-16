package com.app.adv.services

import com.app.adv.models.Advertiser

object dbService {
  def getAdvertiserById(id: Int): Advertiser = {
    Advertiser("mock", "mock", 0)
  }

  def getAllAdvertisers: Seq[Advertiser] = {
    Seq(Advertiser("mock", "mock", 0))
  }

  def deleteAdvertiser(id: Int): Unit = {

  }

  def updateAdvertiser(id: Int, updated: Advertiser): Advertiser = {
    Advertiser("mock", "mock", 0)
  }

  def addAdvertiser(adv: Advertiser): Advertiser = {
    Advertiser("mock", "mock", 0)
  }

  def checkCredits(id: Int, amount: BigDecimal): Boolean = {
    true
  }

  def deductAmount(id:  Int, amount: BigDecimal): Advertiser = {
    Advertiser("mock", "mock", 0)
  }
}