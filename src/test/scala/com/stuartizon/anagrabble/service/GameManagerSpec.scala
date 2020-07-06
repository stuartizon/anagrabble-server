package com.stuartizon.anagrabble.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, TurnLetter}
import com.stuartizon.anagrabble.entity.{Game, LetterBag, Word}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class GameManagerSpec extends Specification with Mockito {
  implicit val system: ActorSystem = ActorSystem()

  class GameManagerSpecContext(letterBag: LetterBag) extends Scope {
    val initialGameState = Game(Nil, Nil, List('c', 'a', 't'), letterBag)
    val gameStateListener = TestProbe()
    val config: AnagrabbleConfig = mock[AnagrabbleConfig]
    val gameManager: ActorRef = system.actorOf(Props(classOf[GameManager], initialGameState, gameStateListener.ref))
  }

  "Game manager" should {
    "send an updated game state when a new word is built" in new GameManagerSpecContext(LetterBag('d')) {
      val cat = Word("cat", 123)

      gameManager ! GuessWord(cat)
      gameStateListener.expectMsg(Game(Nil, List(cat), Nil, LetterBag('d')))
    }

    "not send an update when a new word is not possible" in new GameManagerSpecContext(LetterBag('d')) {
      val dog = Word("dog", 123)

      gameManager ! GuessWord(dog)
      gameStateListener.expectNoMessage
    }

    "send an updated game state when a letter is turned" in new GameManagerSpecContext(LetterBag('d')) {
      gameManager ! TurnLetter
      gameStateListener.expectMsg(Game(Nil, Nil, List('d', 'c', 'a', 't'), LetterBag()))
    }


    "not send an update when there are no letters to turn" in new GameManagerSpecContext(LetterBag()) {
      gameManager ! TurnLetter
      gameStateListener.expectNoMessage
    }
  }
}
