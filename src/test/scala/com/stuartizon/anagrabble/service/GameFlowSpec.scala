package com.stuartizon.anagrabble.service

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import com.stuartizon.anagrabble.entity.PlayerCommand.TurnLetter
import com.stuartizon.anagrabble.entity.{Game, LetterBag}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class GameFlowSpec(implicit env: ExecutionEnv) extends Specification {
  implicit val system: ActorSystem = ActorSystem()
  val dictionary = new Dictionary
  val initialGameState = Game(Nil, Nil, Nil, LetterBag('b'))
  val expectedGameState = Game(Nil, Nil, List('b'), LetterBag())

  "Game flow" should {
    "handle commands and return game state updates" in {
      val flow = new GameFlow(initialGameState, dictionary)
      val gameStateUpdate = Source.single(TurnLetter).via(flow.gameFlow).runWith(Sink.head)

      gameStateUpdate must beEqualTo(expectedGameState).await
    }

    "return updates when the game state is changed by another source" in {
      val flow = new GameFlow(initialGameState, dictionary)
      val gameStateUpdate = Source.empty.via(flow.gameFlow).runWith(Sink.head)
      Source.single(TurnLetter).via(flow.gameFlow).run

      gameStateUpdate must beEqualTo(expectedGameState).await
    }
  }
}
