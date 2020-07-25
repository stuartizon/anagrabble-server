package com.stuartizon.anagrabble

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RejectionHandler
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.{Game, LetterBag}
import com.stuartizon.anagrabble.route.{CreateGameRoute, PingRoute, RouteLogging, WebSocketRoute}
import com.stuartizon.anagrabble.service.{Dictionary, GameEventBus, GameFlow}

object Main extends AnagrabbleConfig with PingRoute with RouteLogging {
  implicit val system: ActorSystem = ActorSystem()
  implicit val log: LoggingAdapter = system.log

  def main(args: Array[String]): Unit = {
    val initialGameState = Game(Nil, Nil, Nil, LetterBag.build(letterCounts))
    val dictionary = new Dictionary
    val gameEventBus = new GameEventBus
    val gameFlow = new GameFlow(gameEventBus)

    val createGameRoute = new CreateGameRoute(initialGameState, dictionary, gameEventBus)
    val webSocketRoute = new WebSocketRoute(gameFlow)

    val route = ping ~ logRequestResponse {
      handleRejections(RejectionHandler.default) {
        createGameRoute.createGame ~
        webSocketRoute.connect
      }
    }

    Http().bindAndHandle(route, host, port)
  }
}