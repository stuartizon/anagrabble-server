package com.stuartizon.anagrabble.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.{Game, GuessWord, Join, LetterBag, PlayerCommandWithName, TurnLetter, Word}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class GameManagerSpec extends Specification with Mockito {
  implicit val system: ActorSystem = ActorSystem()

  class GameManagerSpecContext extends Scope {
    val letterBag: LetterBag = LetterBag('d')
    val dictionary = new Dictionary
    val gameStateListener: TestProbe = TestProbe()
    val config: AnagrabbleConfig = mock[AnagrabbleConfig]

    def initialGameState: Game = Game(List("Amy", "Bob"), Nil, List('c', 'a', 't'), letterBag)
    def gameManager: ActorRef = system.actorOf(Props(classOf[GameManager], initialGameState, dictionary, gameStateListener.ref))
  }

  "Game manager" should {
    "send an updated game state when a new player joins" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName( Join, "Charley")
      gameStateListener.expectMsg(Game(List("Amy", "Bob", "Charley"), Nil, List('c', 'a', 't'), letterBag))
    }

    "send an updated game state when an existing player rejoins" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName(Join, "Amy")
      gameStateListener.expectMsg(Game(List("Amy", "Bob"), Nil, List('c', 'a', 't'), letterBag))
    }

    "send an updated game state when a new word is built" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName(GuessWord("cat"), "Bob")
      gameStateListener.expectMsg(Game(List("Amy", "Bob"), List(Word("cat", "cat", 1)), Nil, LetterBag('d')))
    }

    "not send an update when a new word is not possible" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName(GuessWord("dog"), "Bob")
      gameStateListener.expectNoMessage
    }

    "not send an update when the word is not in the dictionary" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName(GuessWord("tac"), "Bob")
      gameStateListener.expectNoMessage
    }

    "not send an update when the player is not in the game" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName(GuessWord("cat"), "Roger")
      gameStateListener.expectNoMessage
    }

    "send an updated game state when a letter is turned" in new GameManagerSpecContext {
      gameManager ! PlayerCommandWithName(TurnLetter, "Amy")
      gameStateListener.expectMsg(Game(List("Amy", "Bob"), Nil, List('d', 'c', 'a', 't'), LetterBag()))
    }


    "not send an update when there are no letters to turn" in new GameManagerSpecContext {
      override val letterBag: LetterBag = LetterBag()

      gameManager ! PlayerCommandWithName(TurnLetter, "Bob")
      gameStateListener.expectNoMessage
    }
  }
}
