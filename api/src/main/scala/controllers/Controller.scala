package controllers

import zio.http.Routes

trait Controller {

  def routes: Routes[Any, Nothing]
}
