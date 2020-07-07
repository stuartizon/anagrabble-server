package com.stuartizon.anagrabble.route

import akka.NotUsed
import akka.http.scaladsl.testkit.{Specs2RouteTest, WSProbe}
import akka.stream.scaladsl.Flow
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, TurnLetter}
import com.stuartizon.anagrabble.entity.{Game, LetterBag, PlayerCommand, Word}
import io.circe.generic.auto._
import io.circe.syntax._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class WebSocketRouteSpec extends Specification with Mockito with Specs2RouteTest {
  def state1 = Game(Nil, Nil, List('H'), LetterBag())

  def state2(word: Word) = Game(Nil, List(word), Nil, LetterBag())

  val gameFlow: Flow[PlayerCommand, Game, NotUsed] = Flow.fromFunction {
    case TurnLetter => state1
    case GuessWord(word) => state2(word)
  }
  val webSocketRoute: WebSocketRoute = new WebSocketRoute(gameFlow)
  val wsClient = WSProbe()

  "Web socket route" should {
    "connect to a websocket and send/receive messages" in {
      WS("/connect", wsClient.flow) ~> webSocketRoute.connect ~>
        check {
          isWebSocketUpgrade must beTrue

          wsClient.sendMessage("""{"key": "TURN_LETTER"}""")
          wsClient.expectMessage(state1.asJson.toString)

          wsClient.sendMessage("""{"key": "GUESS_WORD", "word": "PIG", "playerId": 123}""")
          wsClient.expectMessage(state2(Word("PIG", 123)).asJson.toString)

          wsClient.sendCompletion()
          wsClient.expectCompletion()
        }
      ok
    }
  }
}
