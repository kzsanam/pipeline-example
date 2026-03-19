package jobs

import loaders.TransformedLoader
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions
import org.apache.spark.sql.functions.col
import transformers.AggregatorTransformer
import writers.AggregatorWriter

final class AggregatorJob(spark: SparkSession)
    extends Job(
      new TransformedLoader(spark),
      new AggregatorTransformer,
      new AggregatorWriter
    )
