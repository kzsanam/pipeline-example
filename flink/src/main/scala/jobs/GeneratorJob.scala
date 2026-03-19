package jobs

import configs.Config
import org.apache.flink.connector.kafka.sink.KafkaSink
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import utils.PersonUtil.Person
import utils.PersonUtil.PersonSerializer
import utils.PersonUtil.generatePerson

final class GeneratorJob(final val config: Config) extends Job {

  override def run(): Unit =
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val sleepTime = 1000 / config.generatePerSec.get

    val peopleStream = env
      .fromSequence(0, Long.MaxValue)
      .map { _ =>
        Thread.sleep(sleepTime)
        generatePerson
      }
      .setParallelism(1)

    val kafkaSink =
      KafkaSink
        .builder[Person]()
        .setBootstrapServers(config.kafkaConfig.bootstrapServers)
        .setRecordSerializer(
          new PersonSerializer(config.kafkaConfig.outputTopic.get)
        )
        .build()

    peopleStream.sinkTo(kafkaSink)

    env.execute("Kafka Sink Example")
}
