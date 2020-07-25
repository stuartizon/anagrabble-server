package com.stuartizon.anagrabble.service

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestProbe
import com.stuartizon.anagrabble.entity.{Game, LetterBag, PlayerCommandWithName, TurnLetter}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

class GameFlowSpec(implicit env: ExecutionEnv) extends Specification {
  implicit val system: ActorSystem = ActorSystem()
  val dictionary = new Dictionary
  val gameEventBus = new GameEventBus
  val gameManager: TestProbe = TestProbe()

  "Game flow" should {
    "send commands to the game manager actor" in {
      val flow = new GameFlow(gameEventBus).gameFlow(gameManager.ref)
      val command: PlayerCommandWithName = PlayerCommandWithName(TurnLetter, "Dave")
      Source.single(command).via(flow).runWith(Sink.head)
      gameManager.expectMsg(command)
      ok
    }

    "return updates from the event bus" in {
      val flow = new GameFlow(gameEventBus).gameFlow(gameManager.ref)
      val gameState = Game(Nil, Nil, List('b'), LetterBag())
      gameEventBus.publish(gameState)
      val result = Source.empty.via(flow).runWith(Sink.head)

      result must beEqualTo(gameState).await
    }
  }
}
