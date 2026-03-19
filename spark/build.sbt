version := "0.1.0-SNAPSHOT"
name := "spark"
scalaVersion := "2.13.18"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _                             => MergeStrategy.first
}

val sparkVersion = "4.0.1"
val hadoopVersion = "3.4.1"

val spark = Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" %% "spark-sql" % sparkVersion,
    "org.apache.spark" %% "spark-hadoop-cloud" % sparkVersion,
    "org.apache.hadoop" % "hadoop-aws" % hadoopVersion,
    "org.apache.hadoop" % "hadoop-common" % hadoopVersion
)

val conf = Seq("com.github.pureconfig" %% "pureconfig" % "0.17.10")

val testDeps = Seq("org.scalatest" %% "scalatest" % "3.2.19" % Test)

libraryDependencies ++= spark ++ conf ++ testDeps
