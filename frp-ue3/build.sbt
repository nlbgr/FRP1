ThisBuild / version      := "1.0"
ThisBuild / scalaVersion := "3.7.3"
ThisBuild / scalacOptions := Seq("-unchecked", "-feature", "-deprecation")

val pekkoVersion    = "1.3.0"
val logbackVersion  = "1.5.21"
val circeVersion    = "0.14.15"

lazy val root = (project in file("."))
  .settings(
    name := "frp-ue3",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream"      % pekkoVersion,

      "io.circe" %% "circe-core"    % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser"  % circeVersion,

      "ch.qos.logback" % "logback-classic" % logbackVersion,
    )
  )