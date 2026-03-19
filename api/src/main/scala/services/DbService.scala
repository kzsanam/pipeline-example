package services

trait DbService {

  def init(): Unit

  def getPerson(get: String): String
}
