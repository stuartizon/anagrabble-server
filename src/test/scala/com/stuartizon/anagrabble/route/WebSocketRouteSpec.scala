package com.stuartizon.anagrabble.route

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.MissingQueryParamRejection
import akka.http.scaladsl.testkit.{RouteTestTimeout, Specs2RouteTest, WSProbe}
import akka.stream.scaladsl.Flow
import com.stuartizon.anagrabble.entity.{Join, _}
import com.stuartizon.anagrabble.service.{GameFlow, GameManager}
import io.circe.generic.auto._
import io.circe.syntax._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scala.concurrent.duration._

class WebSocketRouteSpec extends Specification with Mockito with Specs2RouteTest {
  def state1(player: String): Game = Game(List(player), Nil, Nil, LetterBag())

  def state2: Game = Game(Nil, Nil, List('h'), LetterBag())

  def state3(word: Word): Game = Game(Nil, List(word), Nil, LetterBag())

  val gameManager: ActorRef = system.actorOf(Props(classOf[GameManager], null, null, null), "123")
  val gameFlow: GameFlow = mock[GameFlow]
  gameFlow.gameFlow(gameManager) returns Flow.fromFunction {
    case PlayerCommandWithName(Join, playerId) => state1(playerId)
    case PlayerCommandWithName(TurnLetter, _) => state2
    case PlayerCommandWithName(GuessWord(word), player) => state3(Word(word, word, player.hashCode))
  }

  val webSocketRoute: WebSocketRoute = new WebSocketRoute(gameFlow)
  val wsClient: WSProbe = WSProbe()

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(5.seconds)

  "Web socket route" should {
    "fail if game id is invalid" in {
      WS("/connect/456?player=Tom", wsClient.flow) ~> webSocketRoute.connect ~> check {
        status must beEqualTo(StatusCodes.BadRequest)
      }
    }

    "fail if player param is not provided" in {
      WS("/connect/123", wsClient.flow) ~> webSocketRoute.connect ~> check {
        rejection must beEqualTo(MissingQueryParamRejection("player"))
      }
    }

    "connect to a websocket and send/receive messages" in {
      WS("/connect/123?player=Tom", wsClient.flow) ~> webSocketRoute.connect ~> check {
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
