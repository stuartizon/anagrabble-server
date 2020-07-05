name := "anagrabble-server"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.12",
  "com.typesafe.akka" %% "akka-stream" % "2.6.5",
  "io.circe" %% "circe-core" % "0.12.3",
  "io.circe" %% "circe-generic" % "0.12.3",
  "io.circe" %% "circe-parser" % "0.12.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.6.5" % Test,
  "org.specs2" %% "specs2-core" % "4.10.0" % Test,
  "org.specs2" %% "specs2-mock" % "4.10.0" % Test,
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
)
