package com.stuartizon.anagrabble

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.{Game, LetterBag}
import com.stuartizon.anagrabble.route.WebSocketRoute
import com.stuartizon.anagrabble.service.{Dictionary, GameFlow}

object Main extends AnagrabbleConfig {
  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem()
    implicit val log: LoggingAdapter = system.log

    val initialGameState = Game(Nil, Nil, Nil, LetterBag.build(letterCounts))
    val dictionary = new Dictionary
    val gameFlow = new GameFlow(initialGameState, dictionary)
    val webSocketRoute = new WebSocketRoute(gameFlow.gameFlow)

    Http().bindAndHandle(webSocketRoute.connect, host, port)
  }
}