package com.stuartizon.anagrabble.service

import akka.actor.{Actor, ActorRef}
import com.stuartizon.anagrabble.entity.{Game, Word}
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, TurnLetter}

class GameManager(initialGameState: Game, dictionary: Dictionary, gameStateListener: ActorRef) extends Actor with WordBuilder with LetterTurner {
  override def receive: Receive = behaviour(initialGameState)

  def behaviour(gameState: Game): Receive = {
    case TurnLetter =>
      turnLetter(gameState).foreach { newGameState =>
        gameStateListener ! newGameState
        context.become(behaviour(newGameState))
      }

    case GuessWord(word, playerId) =>
      dictionary.findRoot(word).foreach { root =>
        buildWord(gameState, Word(word, root, playerId)).foreach { newGameState =>
          gameStateListener ! newGameState
          context.become(behaviour(newGameState))
        }
      }
  }
}