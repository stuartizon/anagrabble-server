package com.stuartizon.anagrabble.route

import akka.http.scaladsl.server.{Directives, Route}

trait PingRoute extends Directives {
  val ping: Route =
    path("ping") {
      complete("pong")
    }
}
