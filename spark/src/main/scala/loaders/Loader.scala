package loaders

import org.apache.spark.sql.DataFrame

trait Loader {
  def load(): DataFrame
}
