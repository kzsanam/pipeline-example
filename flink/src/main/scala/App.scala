import configs.Config
import jobs.GeneratorJob
import jobs.TransformerJob
import jobs.WriterJob
import pureconfig.*

object App:

  @main def run(configFile: String = ""): Unit = {
    val config: Config = configFile match {
      case ""   => ConfigSource.default.loadOrThrow[Config]
      case file => ConfigSource.resources(file).loadOrThrow[Config]
    }

    val job = config.jobName.toLowerCase match
      case "generator"   => GeneratorJob(config)
      case "transformer" => TransformerJob(config)
      case "writer"      => WriterJob(config)
      case _ => throw new IllegalArgumentException("Invalid job name")

    job.run()
  }
