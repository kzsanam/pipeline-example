package utils

import java.nio.charset.StandardCharsets

import scala.util.Random

import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto.*
import io.circe.jawn
import io.circe.syntax.*
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import utils.PersonUtil.Person
import utils.PersonUtil.logger

object PersonUtil extends LazyLogging:
  final case class Person(name: String, age: Int)

  final class PersonSerializer(private val topic: String)
      extends KafkaRecordSerializationSchema[Person]:

    override def serialize(
        element: Person,
        context: KafkaRecordSerializationSchema.KafkaSinkContext,
        timestamp: java.lang.Long
    ): ProducerRecord[Array[Byte], Array[Byte]] =

      val json = element.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)

      new ProducerRecord(
        topic,
        null,
        json
      )

  final class PersonDeserializer extends DeserializationSchema[Person] {
    override def deserialize(message: Array[Byte]): Person = {
      val decoded = jawn.decode[Person](String(message))

      decoded match {
        case Right(value) => value
        case Left(error) =>
          logger.warn("invalid input")
          Person("", 0)
      }
    }

    override def isEndOfStream(nextElement: Person): Boolean = false

    override def getProducedType: TypeInformation[Person] =
      TypeInformation.of(classOf[Person])
  }

  final case class TransformedPerson(name: String, age: Int, isAdult: Boolean) {
//    setName
  }

  final class TransformedPersonDeserializer
      extends DeserializationSchema[TransformedPerson] {
    override def deserialize(message: Array[Byte]): TransformedPerson = {
      val decoded = jawn.decode[TransformedPerson](String(message))

      decoded match {
        case Right(value) => value
        case Left(error) =>
          logger.warn("invalid input")
          TransformedPerson("", 0, false)
      }
    }

    override def isEndOfStream(nextElement: TransformedPerson): Boolean = false

    override def getProducedType: TypeInformation[TransformedPerson] =
      TypeInformation.of(classOf[TransformedPerson])
  }

  final class TransformedPersonSerializer(topic: String)
      extends KafkaRecordSerializationSchema[TransformedPerson]:

    override def serialize(
        element: TransformedPerson,
        context: KafkaRecordSerializationSchema.KafkaSinkContext,
        timestamp: java.lang.Long
    ): ProducerRecord[Array[Byte], Array[Byte]] =

      val json = element.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)

      new ProducerRecord(
        topic,
        null,
        json
      )

  val names = List(
    "Alice",
    "Bob",
    "Charlie",
    "Dave",
    "Eve",
    "Frank",
    "Grace",
    "Henry",
    "Ivy",
    "Jack"
  )

  def generatePerson: Person =
    Person(names(Random.between(0, names.length)), Random.between(1, 100))
