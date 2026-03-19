import scala.collection.Seq

version := "0.1.0-SNAPSHOT"
name := "api"
scalaVersion := "2.13.18"

lazy val zioVersion = "2.1.24"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) =>
    xs map { _.toLowerCase } match {
      case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil =>
        MergeStrategy.discard
      case ps @ x :: xs if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}

val zio = Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "dev.zio" %% "zio-http" % "3.10.1",
)
val conf = Seq(
  "com.typesafe.play" %% "play-json" % "2.10.8",
  "com.github.pureconfig" %% "pureconfig" % "0.17.10"
).map(_.exclude("io.netty", "*")
  .exclude("org.apache.commons", "commons-codec")
  .exclude("com.fasterxml.jackson.core", "jackson-core")
)

val aws = Seq(
  "software.amazon.awssdk" % "dynamodb" % "2.42.16",
)

libraryDependencies := zio ++ conf ++ aws
