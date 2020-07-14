package com.stuartizon.anagrabble

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RejectionHandler
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.{Game, LetterBag}
import com.stuartizon.anagrabble.route.{PingRoute, RouteLogging, WebSocketRoute}
import com.stuartizon.anagrabble.service.{Dictionary, GameFlow}

object Main extends AnagrabbleConfig with PingRoute with RouteLogging {
  implicit val system: ActorSystem = ActorSystem()
  implicit val log: LoggingAdapter = system.log

  def main(args: Array[String]): Unit = {
    val initialGameState = Game(Nil, Nil, Nil, LetterBag.build(letterCounts))
    val dictionary = new Dictionary
    val gameFlow = new GameFlow(initialGameState, dictionary)
    val webSocketRoute = new WebSocketRoute(gameFlow.gameFlow)

    val route = logRequestResponse {
      handleRejections(RejectionHandler.default) {
        webSocketRoute.connect ~
          ping
      }
    }

    Http().bindAndHandle(route, host, port)
  }
}