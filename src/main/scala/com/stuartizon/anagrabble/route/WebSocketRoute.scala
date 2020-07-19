package com.stuartizon.anagrabble.route

import akka.NotUsed
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Source}
import com.stuartizon.anagrabble.entity.PlayerCommand.Join
import com.stuartizon.anagrabble.entity.{Game, PlayerCommand}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class WebSocketRoute(gameFlow: Flow[PlayerCommand, Game, NotUsed]) {
  val connect: Route =
    path("connect") {
      parameter("playerId") { playerId =>
        handleWebSocketMessages {
          Flow[Message]
            .collect {
              // Handle only the strict messages
              case TextMessage.Strict(json) => decode[PlayerCommand](json)
            }
            .collect {
              // Ignore anything which didn't deserialize correctly
              case Right(command) => command
            }
            .log("Receive")
            .prepend(Source.single(Join(playerId)))
            .via(gameFlow)
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
