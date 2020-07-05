package com.stuartizon.anagrabble

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.stuartizon.anagrabble.config.AnagrabbleConfig


object Main extends AnagrabbleConfig {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val log: LoggingAdapter = system.log

    val route =
      get {
        complete("Hello")
      }

    Http().bindAndHandle(route, host, port)
  }

}