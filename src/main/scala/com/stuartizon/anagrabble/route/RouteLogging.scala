package com.stuartizon.anagrabble.route

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.{Directive0, Directives}

trait RouteLogging extends Directives {
  implicit def log: LoggingAdapter

  def logRequestResponse: Directive0 = extractRequest.flatMap { request =>
    log.info(s"${request.method.value} ${request.uri.path} request")
    mapResponse { response =>
      log.info(s"${request.method.value} ${request.uri.path} response status=${response.status.intValue}")
      response
    }
  }
}