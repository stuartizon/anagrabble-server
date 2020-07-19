package com.stuartizon.anagrabble.route

import akka.NotUsed
import akka.http.scaladsl.server.MissingQueryParamRejection
import akka.http.scaladsl.testkit.{Specs2RouteTest, WSProbe}
import akka.stream.scaladsl.Flow
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, Join, TurnLetter}
import com.stuartizon.anagrabble.entity._
import io.circe.generic.auto._
import io.circe.syntax._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class WebSocketRouteSpec extends Specification with Mockito with Specs2RouteTest {
  def state1(player: Player): Game = Game(Set(player), Nil, Nil, LetterBag())

  def state2: Game = Game(Set.empty, Nil, List('h'), LetterBag())

  def state3(word: Word): Game = Game(Set.empty, List(word), Nil, LetterBag())

  val gameFlow: Flow[PlayerCommand, Game, NotUsed] = Flow.fromFunction {
    case Join(playerId) => state1(Player(playerId))
    case TurnLetter => state2
    case GuessWord(word, playerId) => state3(Word(word, word, playerId))
  }
  val webSocketRoute: WebSocketRoute = new WebSocketRoute(gameFlow)
  val wsClient: WSProbe = WSProbe()

  "Web socket route" should {
    "fail if player id is not provided" in {
      WS("/connect", wsClient.flow) ~> webSocketRoute.connect ~> check {
        rejection must beEqualTo(MissingQueryParamRejection("playerId"))
      }
    }

    "connect to a websocket and send/receive messages" in {
      WS("/connect?playerId=Tom", wsClient.flow) ~> webSocketRoute.connect ~> check {
        isWebSocketUpgrade must beTrue
        wsClient.expectMessage(state1(Player("Tom")).asJson.toString)

        wsClient.sendMessage("""{"key": "TURN_LETTER"}""")
        wsClient.expectMessage(state2.asJson.toString)

        wsClient.sendMessage("""{"key": "GUESS_WORD", "word": "pig", "playerId": 123}""")
        wsClient.expectMessage(state3(Word("pig", "pig", 123)).asJson.toString)

        wsClient.sendCompletion()
        wsClient.expectCompletion()
      }
      ok
    }
  }
}
