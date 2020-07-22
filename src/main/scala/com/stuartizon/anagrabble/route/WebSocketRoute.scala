package com.stuartizon.anagrabble.route

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Source}
import com.stuartizon.anagrabble.entity.{Join, PlayerCommand, PlayerCommandWithName}
import com.stuartizon.anagrabble.service.GameFlow
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class WebSocketRoute(gameFlow: GameFlow) {
  val connect: Route =
    path("connect") {
      parameter("player") { player =>
        handleWebSocketMessages {
          Flow[Message]
            .collect {
              // Handle only the strict messages
              case TextMessage.Strict(json) => decode[PlayerCommand](json)
            }
            .log("Receive")
            .collect {
              // Ignore anything which didn't deserialize correctly
              case Right(command) => PlayerCommandWithName(command, player)
            }
            .prepend(Source.single(PlayerCommandWithName(Join, player)))
            .via(gameFlow.gameFlow())
            .map { gameStateUpdate =>
              TextMessage.Strict(gameStateUpdate.asJson.toString)
            }
            .addAttributes {
              Attributes.logLevels(onElement = Attributes.LogLevels.Info)
            }
        }
      }
    }
}
