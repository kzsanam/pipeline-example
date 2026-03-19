package loaders

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession

final class TransformedLoader(private val spark: SparkSession) extends Loader {
  override def load(): DataFrame = {
    spark.read
      .format("json")
      .option("inferSchema", "true")
      .load("s3a://transformed-data")

  }
}
