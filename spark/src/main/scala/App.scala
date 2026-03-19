import configs.Config
import jobs.AggregatorJob
import org.apache.spark.sql.SparkSession
import pureconfig._
import pureconfig.generic.auto._

object App extends App {
  val conf = ConfigSource.default.load[Config] match {
    case Right(value) => value
    case Left(error)  => throw new Exception(error.toString)
  }

  val spark = SparkSession
    .builder()
    .appName("example")
    .master("local[*]")
    .config("spark.hadoop.fs.s3a.endpoint", conf.aws.endpoint)
    .config("spark.hadoop.fs.s3a.access.key", conf.aws.accessKey)
    .config("spark.hadoop.fs.s3a.secret.key", conf.aws.secretKey)
    .config("spark.hadoop.fs.s3a.path.style.access", "true")
    .config(
      "spark.hadoop.fs.s3a.impl",
      "org.apache.hadoop.fs.s3a.S3AFileSystem"
    )
    .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false")
//      .config("spark.driver.host", "localhost")
//      .config("spark.driver.bindAddress", "127.0.0.1")
    .getOrCreate()

  private val job = new AggregatorJob(spark)
  job.run()
}
