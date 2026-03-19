version := "0.1.0-SNAPSHOT"
name := "flink"
scalaVersion := "3.8.2"
scalaBinaryVersion := "3"

val flinkVersion = "2.2.0"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "versions", xs @ _*) => MergeStrategy.discard
  case "module-info.class"                       => MergeStrategy.discard
  case "META-INF/io.netty.versions.properties"   => MergeStrategy.first
  case "META-INF/MANIFEST.MF"                    => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

val flinkDependencies = Seq(
  "org.apache.flink" % "flink-streaming-java" % flinkVersion,
  "org.apache.flink" % "flink-clients" % flinkVersion
)

val flinkConnectors = Seq(
  "org.apache.flink" % "flink-connector-kafka" % "4.0.1-2.0",
  "org.apache.flink" % "flink-connector-base" % flinkVersion,
  ("software.amazon.awssdk" % "dynamodb" % "2.42.13")
    .excludeAll(
      ExclusionRule(organization = "io.netty")
    )
)

val jsonVersion = "0.14.15"

val json = Seq(
  "io.circe" %% "circe-core" % jsonVersion,
  "io.circe" %% "circe-generic" % jsonVersion,
  "io.circe" %% "circe-parser" % jsonVersion
)

val conf = Seq("com.github.pureconfig" %% "pureconfig-core" % "0.17.10")

val logs = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.6",
  "ch.qos.logback" % "logback-classic" % "1.5.32"
)

libraryDependencies ++= flinkDependencies ++ flinkConnectors ++ json ++ conf ++ logs
