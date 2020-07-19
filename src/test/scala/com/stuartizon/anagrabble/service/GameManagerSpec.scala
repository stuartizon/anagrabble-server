package com.stuartizon.anagrabble.service

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestProbe
import com.stuartizon.anagrabble.config.AnagrabbleConfig
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, Join, TurnLetter}
import com.stuartizon.anagrabble.entity.{Game, LetterBag, Player, Word}
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

    def initialGameState: Game = Game(Set(Player("Bob")), Nil, List('c', 'a', 't'), letterBag)
    def gameManager: ActorRef = system.actorOf(Props(classOf[GameManager], initialGameState, dictionary, gameStateListener.ref))
  }

  "Game manager" should {
    "send an updated game state when a new player joins" in new GameManagerSpecContext {
      gameManager ! Join("Emma")
      gameStateListener.expectMsg(Game(Set(Player("Bob"), Player("Emma")), Nil, List('c', 'a', 't'), letterBag))
    }

    "send an updated game state when an existing player rejoins" in new GameManagerSpecContext {
      gameManager ! Join("Bob")
      gameStateListener.expectMsg(Game(Set(Player("Bob")), Nil, List('c', 'a', 't'), letterBag))
    }

    "send an updated game state when a new word is built" in new GameManagerSpecContext {
      gameManager ! GuessWord("cat", 123)
      gameStateListener.expectMsg(Game(Set(Player("Bob")), List(Word("cat", "cat", 123)), Nil, LetterBag('d')))
    }

    "not send an update when a new word is not possible" in new GameManagerSpecContext {
      gameManager ! GuessWord("dog", 234)
      gameStateListener.expectNoMessage
    }

    "not send an update when the word is not in the dictionary" in new GameManagerSpecContext {
      gameManager ! GuessWord("tac", 345)
      gameStateListener.expectNoMessage
    }

    "send an updated game state when a letter is turned" in new GameManagerSpecContext {
      gameManager ! TurnLetter
      gameStateListener.expectMsg(Game(Set(Player("Bob")), Nil, List('d', 'c', 'a', 't'), LetterBag()))
    }


    "not send an update when there are no letters to turn" in new GameManagerSpecContext {
      override val letterBag: LetterBag = LetterBag()

      gameManager ! TurnLetter
      gameStateListener.expectNoMessage
    }
  }
}
