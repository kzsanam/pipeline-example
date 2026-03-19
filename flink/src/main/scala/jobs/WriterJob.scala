package jobs

import scala.jdk.CollectionConverters.*

import configs.Config
import configs.DbConfig
import configs.DynamoDbConfig
import configs.NoOpDbConfig
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.connector.sink2.Sink
import org.apache.flink.api.connector.sink2.SinkWriter
import org.apache.flink.api.connector.sink2.SinkWriter.Context
import org.apache.flink.api.connector.sink2.WriterInitContext
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import utils.PersonUtil.TransformedPerson
import utils.PersonUtil.TransformedPersonDeserializer

final class WriterJob(config: Config) extends Job {
  override def run(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val kafkaSource = KafkaSource
      .builder[TransformedPerson]()
      .setBootstrapServers(config.kafkaConfig.bootstrapServers)
      .setTopics(config.kafkaConfig.inputTopic.get)
      .setGroupId("writer")
      .setStartingOffsets(OffsetsInitializer.earliest())
      .setValueOnlyDeserializer(new TransformedPersonDeserializer())
      .build()

    val peopleStream = env.fromSource(
      kafkaSource,
      WatermarkStrategy.noWatermarks(),
      "Kafka Source"
    )

    config.dbConfig match {
      case ddb: DynamoDbConfig =>
        val sink = new DynamoDbSink(ddb)
        peopleStream.sinkTo(sink)
        peopleStream.sinkTo(sink)
        env.execute("Kafka Sink Example")
      case NoOpDbConfig =>
        throw new IllegalArgumentException("Invalid db config")
    }
  }
}

class DynamoDbSink(dbConfig: DynamoDbConfig)
    extends Sink[TransformedPerson]
    with Serializable {

  def createWriter(
      context: WriterInitContext
  ): SinkWriter[TransformedPerson] = {
    val credentialsProvider = StaticCredentialsProvider.create(
      AwsBasicCredentials.create(dbConfig.key, dbConfig.secret)
    )

    val client = DynamoDbClient
      .builder()
      .region(Region.EU_CENTRAL_1)
      .endpointOverride(new java.net.URI(dbConfig.endpoint))
      .credentialsProvider(credentialsProvider)
      .build()

    new SinkWriter[TransformedPerson]() {

      def close(): Unit = {
        client.close()
      }

      override def write(person: TransformedPerson, context: Context): Unit = {
        val key = Map(
          "name" -> AttributeValue.builder().s(person.name).build()
        ).asJava

        // get item
        val getRequest = GetItemRequest
          .builder()
          .tableName(dbConfig.table)
          .key(key)
          .build()
        val itemOpt = Option(client.getItem(getRequest).item())

        // new count and avg
        val oldCount =
          itemOpt
            .map(
              _.getOrDefault(
                "person_count",
                AttributeValue.builder().n("0").build()
              )
            )
            .map(_.n().toInt)
            .getOrElse(0)

        val oldAvg = itemOpt
          .map(
            _.getOrDefault("avg_age", AttributeValue.builder().n("0.0").build())
          )
          .map(_.n().toDouble)
          .getOrElse(0.0)
        val newCount = oldCount + 1
        val newAvg = ((oldAvg * oldCount) + person.age) / newCount

        // update
        val updateRequest = UpdateItemRequest
          .builder()
          .tableName("PersonTable")
          .key(key)
          .updateExpression("SET person_count = :c, avg_age = :a")
          .expressionAttributeValues(
            Map(
              ":c" -> AttributeValue.builder().n(newCount.toString).build(),
              ":a" -> AttributeValue.builder().n(newAvg.toString).build()
            ).asJava
          )
          .build()
        client.updateItem(updateRequest)

        client.updateItem(updateRequest)
      }

      override def flush(endOfInput: Boolean): Unit = {
        if (endOfInput) {
          client.close()
        }
      }
    }
  }
}
