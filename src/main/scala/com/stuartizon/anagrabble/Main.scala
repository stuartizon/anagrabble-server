package com.stuartizon.anagrabble

import akka.actor.{ActorSystem, Props}
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RejectionHandler
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.{Game, LetterBag}
import com.stuartizon.anagrabble.route.{PingRoute, RouteLogging, WebSocketRoute}
import com.stuartizon.anagrabble.service.{Dictionary, GameEventBus, GameFlow, GameManager}

object Main extends AnagrabbleConfig with PingRoute with RouteLogging {
  implicit val system: ActorSystem = ActorSystem()
  implicit val log: LoggingAdapter = system.log

  def main(args: Array[String]): Unit = {
    val initialGameState = Game(Nil, Nil, Nil, LetterBag.build(letterCounts))
    val dictionary = new Dictionary
    val gameEventBus = new GameEventBus
    val gameManager = system.actorOf(Props(classOf[GameManager], initialGameState, dictionary, gameEventBus))

    val gameFlow = new GameFlow(gameEventBus, gameManager)
    val webSocketRoute = new WebSocketRoute(gameFlow)

    val route = ping ~ logRequestResponse {
      handleRejections(RejectionHandler.default) {
        webSocketRoute.connect
      }
    }

    Http().bindAndHandle(route, host, port)
  }
}