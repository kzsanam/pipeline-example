package jobs

import loaders.Loader
import transformers.Transformer
import writers.Writer

abstract class Job(loader: Loader, transformer: Transformer, writer: Writer) {
  def run(): Unit = {
    val df = loader.load()
    val transformed = transformer.transform(df)
    writer.write(transformed)
  }
}
