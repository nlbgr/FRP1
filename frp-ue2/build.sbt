ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "3.7.3"
ThisBuild / scalacOptions := Seq("-unchecked", "-feature", "-deprecation")

val pekkoVersion = "1.3.0"
val logbackVersion = "1.5.21"

lazy val root = (project in file("."))
  .settings(
    name := "frp-ue2",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed"  % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream"       % pekkoVersion,
      "org.apache.pekko" %% "pekko-pki"          % pekkoVersion,
	  
      "ch.qos.logback" % "logback-classic" % logbackVersion
    )
  )
