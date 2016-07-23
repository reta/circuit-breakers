lazy val main = (project in file("."))
  .settings(
    name := "akka-circuit-breaker",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-core" % "1.1.7",
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "com.typesafe.akka" %% "akka-stream" % "2.4.8",
      "com.typesafe.akka" %% "akka-http-core" % "2.4.8",
      "com.typesafe.akka" %% "akka-actor" % "2.4.8",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.8",
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.8",
      "com.typesafe.akka" %% "akka-testkit" % "2.4.8" % "test",
      "org.specs2" %% "specs2-core" % "3.8.4" % "test"
    )
  )
