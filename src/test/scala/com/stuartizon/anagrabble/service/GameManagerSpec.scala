package com.stuartizon.anagrabble.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.stuartizon.anagrabble.entity.PlayerCommand.GuessWord
import com.stuartizon.anagrabble.entity.{Game, Word}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class GameManagerSpec extends Specification with Mockito {
  implicit val system: ActorSystem = ActorSystem()
  val cat = Word("cat", 123)

  class GameManagerSpecContext extends Scope {
    val gameStateListener = TestProbe()
    val wordBuildingService: WordBuildingService = mock[WordBuildingService]
    val gameManager: ActorRef = system.actorOf(Props(classOf[GameManager], wordBuildingService, gameStateListener.ref))
  }


  "Game manager" should {
    "send an updated game state when a new word is built" in new GameManagerSpecContext {
      val newGameState = Game(Nil, List(cat), Nil)
      wordBuildingService.buildWord(any(), ===(cat)) returns Some(newGameState)

      gameManager ! GuessWord(cat)
      gameStateListener.expectMsg(newGameState)
      ok
    }

    "not send an update when a new word is not possible" in new GameManagerSpecContext {
      wordBuildingService.buildWord(any(), ===(cat)) returns None

      gameManager ! GuessWord(cat)
      gameStateListener.expectNoMessage
      ok
    }
  }
}
