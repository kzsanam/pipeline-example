package jobs

import configs.Config
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.kafka.sink.KafkaSink
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import utils.PersonUtil.Person
import utils.PersonUtil.PersonDeserializer
import utils.PersonUtil.TransformedPerson
import utils.PersonUtil.TransformedPersonSerializer

final class TransformerJob(config: Config) extends Job {

  override def run(): Unit =
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val kafkaSource = KafkaSource
      .builder[Person]()
      .setBootstrapServers(config.kafkaConfig.bootstrapServers)
      .setTopics(config.kafkaConfig.inputTopic.get)
      .setGroupId("transformer")
      .setStartingOffsets(OffsetsInitializer.earliest())
      .setValueOnlyDeserializer(new PersonDeserializer)
      .build()

    val peopleStream: DataStream[Person] = env.fromSource(
      kafkaSource,
      WatermarkStrategy.noWatermarks(),
      "Kafka Source"
    )

    val transformedStream: DataStream[TransformedPerson] =
      peopleStream.map(p => TransformedPerson(p.name, p.age, p.age > 18))

    val kafkaSink =
      KafkaSink
        .builder[TransformedPerson]()
        .setBootstrapServers(config.kafkaConfig.bootstrapServers)
        .setRecordSerializer(
          new TransformedPersonSerializer(config.kafkaConfig.outputTopic.get)
        )
        .build()

    transformedStream.sinkTo(kafkaSink)

    env.execute("Kafka Transformer")
}
