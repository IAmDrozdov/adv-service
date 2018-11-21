lazy val finchVersion = "0.26.0"
lazy val circeVersion = "0.10.1"
lazy val scalatestVersion = "3.0.5"
lazy val doobieVersion = "0.6.0"
lazy val typesafeVersion = "1.3.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.app",
    name := "adv-service",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core" % finchVersion,
      "com.github.finagle" %% "finchx-circe" % finchVersion,
      "io.circe" %% "circe-generic" % circeVersion,

      "org.scalatest" %% "scalatest" % scalatestVersion % "test",

      "org.tpolecat" %% "doobie-core"     % doobieVersion,
      "org.tpolecat" %% "doobie-h2" % doobieVersion,
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",
      "org.tpolecat" %% "doobie-specs2"   % doobieVersion,

      "com.typesafe" % "config" % typesafeVersion,

      "io.swagger" %% "swagger-scala-module" % "1.0.3"
    )
  )