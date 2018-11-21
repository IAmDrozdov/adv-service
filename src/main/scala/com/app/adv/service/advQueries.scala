package com.app.adv.services

import cats.effect._
import com.app.adv.models.Advertiser
import com.typesafe.config.ConfigFactory
import doobie._
import doobie.implicits._

import scala.concurrent.ExecutionContext

object advQueries {
  private implicit val cs = IO.contextShift(ExecutionContext.global)
  private val dbCfg = ConfigFactory.load().getConfig("adv-service.db")
  private val xa = Transactor.fromDriverManager[IO](
    dbCfg.getString("driver"),
    dbCfg.getString("url"),
    dbCfg.getString("user"),
    dbCfg.getString("pass")
  )
  private val y = xa.yolo

  import y._

  sql"""CREATE TABLE IF NOT EXISTS ADVERTISER
        (
        NAME VARCHAR(255) NOT NULL,
        CONTACT_NAME VARCHAR(255) NOT NULL,
        CREDIT_LIMIT DECIMAL NOT NULL,
        ID NUMBER NOT NULL AUTO_INCREMENT,
        PRIMARY KEY (ID)
        );
    """.update.quick.unsafeRunSync

  def getAdvById(id: Long): Option[Advertiser] = {
    sql"SELECT * FROM ADVERTISER WHERE ID = $id"
      .query[Advertiser]
      .option
      .transact(xa)
      .unsafeRunSync
  }

  def getAllAdvs: Seq[Advertiser] = {
    sql"SELECT * FROM ADVERTISER"
      .query[Advertiser]
      .to[Seq]
      .transact(xa)
      .unsafeRunSync
  }

  //
  def deleteAdv(id: Long): Unit = {
    sql"DELETE FROM ADVERTISER WHERE ID = $id"
      .update.quick
      .unsafeRunSync
  }

  def updateAdv(id: Long,
                name: String,
                contactName: String,
                creditLimit: Double): Unit = {
    sql"""UPDATE ADVERTISER
          SET NAME=$name, CONTACT_NAME=$contactName, CREDIT_LIMIT=$creditLimit
          WHERE ID = $id
      """
      .update
      .quick
      .unsafeRunSync
  }

  def addAdv(name: String,
             contactName: String,
             creditLimit: Double): Advertiser = {
    val query = for {
      _ <-
        sql"""INSERT INTO ADVERTISER (NAME, CONTACT_NAME, CREDIT_LIMIT)
              VALUES ($name, $contactName, $creditLimit)
           """.update.run
      id <- sql"SELECT LASTVAL()".query[Long].unique
      adv <- sql"SELECT * FROM ADVERTISER WHERE ID = $id"
        .query[Advertiser]
        .unique
    } yield adv
    query.transact(xa).unsafeRunSync
  }
}
