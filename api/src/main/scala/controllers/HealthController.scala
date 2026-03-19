package controllers

import zio.http.Method
import zio.http.Response
import zio.http.Root
import zio.http.Routes
import zio.http.handler

final class HealthController extends Controller {
  override def routes: Routes[Any, Nothing] = Routes(
    Method.GET / Root -> handler(Response.text("API at your service"))
  )

}
