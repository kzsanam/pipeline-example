import org.scalatest.funspec.AnyFunSpec
import org.apache.spark.sql.SparkSession
import transformers.AggregatorTransformer

class AggregatorTransformerTest extends AnyFunSpec {

  val spark = SparkSession
    .builder()
    .appName("test")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._
  describe("AggregatorTransformer") {
    it("should aggregate name counts and age statistics") {
      val data = Seq(
        ("Alice", 30),
        ("Bob", 21),
        ("Alice", 22),
        ("Bob", 25)
      )

      val df = data.toDF("name", "age")

      val transformer = new AggregatorTransformer

      val result = transformer.transform(df)

      val rows = result.collect()

      // Find Alice and Bob results
      val aliceRow = rows.find(_.getAs[String]("name") == "Alice").get
      val bobRow = rows.find(_.getAs[String]("name") == "Bob").get

      assert(aliceRow.getAs[Long]("count") == 2)
      // average age for Alice: (30+22)/2 = 26.0
      assert(aliceRow.getAs[Double]("avg_age") == 26.0)
      assert(aliceRow.getAs[Int]("max_age") == 30)

      assert(bobRow.getAs[Long]("count") == 2)
      // average age for Bob: (21+25)/2 = 23.0
      assert(bobRow.getAs[Double]("avg_age") == 23.0)
      assert(bobRow.getAs[Int]("max_age") == 25)
    }
  }
  spark.stop()
}
