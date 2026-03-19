package writers

import org.apache.spark.sql.DataFrame

final class AggregatorWriter extends Writer {
  override def write(df: DataFrame): Unit = {
    df.write.mode("overwrite").parquet("s3a://aggregated-data/aggregated")
  }
}
