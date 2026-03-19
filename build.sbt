ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val api = project.in(file("api"))
lazy val spark = project.in(file("spark"))
lazy val flink = project.in(file("flink"))

lazy val root = (project in file("."))
  .aggregate(api, spark, flink)
  .settings(
    name := "pipeline-example",
    publish / skip := true
  )
