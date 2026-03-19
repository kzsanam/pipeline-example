package controllers

import services.DbService
import zio.http.Body
import zio.http.Method
import zio.http.Request
import zio.http.Response
import zio.http.Routes
import zio.http.Status
import zio.http.handler

class DbController(dbService: DbService) extends Controller {

  override def routes: Routes[Any, Nothing] = Routes(
    Method.GET / "person" -> handler { (req: Request) =>
      if (req.hasJsonContentType) {
        Response(
          status = Status.BadRequest,
          body = Body.fromString("json is not implemented yet")
        )
      } else {
        val name = req.queryParam("name")
        if (name.isDefined) {
          Response.text(s"${dbService.getPerson(name.get)}")
        } else {
          Response.text(s"please give person name")
        }
      }
    }
  )
}
