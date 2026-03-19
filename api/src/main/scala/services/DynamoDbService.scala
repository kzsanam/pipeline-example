package services

import scala.jdk.CollectionConverters._

import configs.DbConfig
import models.Person
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import zio.json.EncoderOps

class DynamoDbService(dbConfig: DbConfig) extends DbService {
  var db: Option[DynamoDbClient] = None

  override def init(): Unit = {
    val credentialsProvider = StaticCredentialsProvider.create(
      AwsBasicCredentials.create(dbConfig.key, dbConfig.secret)
    )

    val client =
      DynamoDbClient
        .builder()
        .region(Region.EU_CENTRAL_1)
        .endpointOverride(new java.net.URI(dbConfig.endpoint))
        .credentialsProvider(credentialsProvider)
        .build()

    db = Some(client)
  }

  override def getPerson(name: String): String = {
    val initDb = db match {
      case Some(db) => db
      case _ =>
        return "db not initialized"
    }

    val key = Map(
      "name" -> AttributeValue.builder().s(name).build()
    ).asJava

    val getRequest = GetItemRequest
      .builder()
      .tableName(dbConfig.table)
      .key(key)
      .build()

    val item = initDb.getItem(getRequest).hasItem match {
      case true  => initDb.getItem(getRequest).item()
      case false => return s"$name not found"
    }

    val count = Option(item.get("person_count")).map(_.n().toInt).getOrElse(0)
    val avgAge = Option(item.get("avg_age")).map(_.n().toDouble).getOrElse(0.0)
    Person(name, count, avgAge).toJson
  }
}
