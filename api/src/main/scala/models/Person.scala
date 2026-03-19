package models

import zio.json.DeriveJsonCodec
import zio.json.JsonCodec
import zio.json.jsonField

final case class Person(
    name: String,
    count: Int,
    @jsonField("avg_age")
    avgAge: Double
)

object Person {
  // Derive the encoder and decoder for the case class
  implicit val codec: JsonCodec[Person] = DeriveJsonCodec.gen[Person]
}
