package com.stuartizon.anagrabble.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.PlayerCommand.GuessWord
import com.stuartizon.anagrabble.entity.{Game, LetterBag, Word}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class GameManagerSpec extends Specification with Mockito {
  implicit val system: ActorSystem = ActorSystem()
  val initialGameState = Game(Nil, Nil, List('c', 'a', 't'), LetterBag(Nil))

  class GameManagerSpecContext extends Scope {
    val gameStateListener = TestProbe()
    val config: AnagrabbleConfig = mock[AnagrabbleConfig]
    val gameManager: ActorRef = system.actorOf(Props(classOf[GameManager], initialGameState, gameStateListener.ref))
  }

  "Game manager" should {
    "send an updated game state when a new word is built" in new GameManagerSpecContext {
      val cat = Word("cat", 123)

      gameManager ! GuessWord(cat)
      gameStateListener.expectMsg(Game(Nil, List(cat), Nil, LetterBag(Nil)))
      ok
    }

    "not send an update when a new word is not possible" in new GameManagerSpecContext {
      val dog = Word("dog", 123)

      gameManager ! GuessWord(dog)
      gameStateListener.expectNoMessage
      ok
    }
  }
}
