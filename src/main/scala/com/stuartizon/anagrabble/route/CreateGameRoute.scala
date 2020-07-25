package com.stuartizon.anagrabble.route

import java.util.UUID

import akka.actor.Props
import akka.http.scaladsl.model.headers.`Access-Control-Allow-Origin`
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import com.stuartizon.anagrabble.entity.Game
import com.stuartizon.anagrabble.service.{Dictionary, GameEventBus, GameManager}

import scala.concurrent.duration._

class CreateGameRoute(initialGameState: Game, dictionary: Dictionary, gameEventBus: GameEventBus) extends Directives {
  implicit val timeout: Timeout = 10.seconds
  val createGame: Route =
    path("create") {
      get {
        respondWithHeaders(`Access-Control-Allow-Origin`.*) {
          extractActorSystem { system =>
            val uuid = UUID.randomUUID().toString
            system.actorOf(Props(classOf[GameManager], initialGameState, dictionary, gameEventBus), uuid)
            complete(uuid)
          }
        }
      }
    }
}