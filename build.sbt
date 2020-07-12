import ReleaseTransformations._

name := "anagrabble-server"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.12",
  "com.typesafe.akka" %% "akka-stream" % "2.6.5",
  "io.circe" %% "circe-core" % "0.12.3",
  "io.circe" %% "circe-generic" % "0.12.3",
  "io.circe" %% "circe-parser" % "0.12.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.12" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.5",
  "com.typesafe.akka" %% "akka-testkit" % "2.6.5" % Test,
  "org.specs2" %% "specs2-core" % "4.10.0" % Test,
  "org.specs2" %% "specs2-mock" % "4.10.0" % Test,
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
)

dockerUsername := Some("stuartizon")
dockerExposedPorts := List(8080)

enablePlugins(DockerPlugin, JavaServerAppPackaging)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  releaseStepTask(publish in Docker),
  setNextVersion,
  commitNextVersion,
  pushChanges
)