package configs

import pureconfig.ConfigReader

final case class Config(
    jobName: String,
    kafkaConfig: KafkaConfig,
    generatePerSec: Option[Int],
    dbConfig: DbConfig
) derives ConfigReader

final case class KafkaConfig(
    bootstrapServers: String,
    inputTopic: Option[String],
    outputTopic: Option[String]
)

sealed trait DbConfig

final case class DynamoDbConfig(
    endpoint: String,
    key: String,
    secret: String,
    table: String
) extends DbConfig

final case class NoOpDbConfig() extends DbConfig
