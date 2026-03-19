package transformers

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions
import org.apache.spark.sql.functions.col

final class AggregatorTransformer extends Transformer {
  override def transform(df: DataFrame): DataFrame = {
    df.groupBy(col("name"))
      .agg(
        functions.count("name") as "count",
        functions.avg("age") as "avg_age",
        functions.max("age") as "max_age"
      )
  }
}
