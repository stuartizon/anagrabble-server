package com.stuartizon.anagrabble.route

import akka.NotUsed
import akka.http.scaladsl.server.MissingQueryParamRejection
import akka.http.scaladsl.testkit.{Specs2RouteTest, WSProbe}
import akka.stream.scaladsl.Flow
import com.stuartizon.anagrabble.entity.{Join, _}
import io.circe.generic.auto._
import io.circe.syntax._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class WebSocketRouteSpec extends Specification with Mockito with Specs2RouteTest {
  def state1(player: String): Game = Game(List(player), Nil, Nil, LetterBag())

  def state2: Game = Game(Nil, Nil, List('h'), LetterBag())

  def state3(word: Word): Game = Game(Nil, List(word), Nil, LetterBag())

  val gameFlow: Flow[PlayerCommandWithName, Game, NotUsed] = Flow.fromFunction {
    case PlayerCommandWithName(Join, playerId) => state1(playerId)
    case PlayerCommandWithName(TurnLetter, _) => state2
    case PlayerCommandWithName(GuessWord(word), player) => state3(Word(word, word, player.hashCode))
  }
  val webSocketRoute: WebSocketRoute = new WebSocketRoute(gameFlow)
  val wsClient: WSProbe = WSProbe()

  "Web socket route" should {
    "fail if player param is not provided" in {
      WS("/connect", wsClient.flow) ~> webSocketRoute.connect ~> check {
        rejection must beEqualTo(MissingQueryParamRejection("player"))
      }
    }

    "connect to a websocket and send/receive messages" in {
      WS("/connect?player=Tom", wsClient.flow) ~> webSocketRoute.connect ~> check {
        isWebSocketUpgrade must beTrue
        wsClient.expectMessage(state1("Tom").asJson.toString)

        wsClient.sendMessage("""{"key": "TURN_LETTER"}""")
        wsClient.expectMessage(state2.asJson.toString)

        wsClient.sendMessage("""{"key": "GUESS_WORD", "word": "pig"}""")
        wsClient.expectMessage(state3(Word("pig", "pig", "Tom".hashCode)).asJson.toString)

        wsClient.sendCompletion()
        wsClient.expectCompletion()
      }
      ok
    }
  }
}
