ThisBuild / version      := "1.0"
ThisBuild / scalaVersion := "3.7.3"
ThisBuild / scalacOptions := Seq("-unchecked", "-feature", "-deprecation")

val logbackVersion = "1.5.20"

lazy val root = (project in file("."))
  .settings(
    name := "frp-ue1",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion
    )
  )