package com.stuartizon.anagrabble.service

import akka.actor.{Actor, ActorRef}
import com.stuartizon.anagrabble.entity.Game
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, TurnLetter}

class GameManager(initialGameState: Game, gameStateListener: ActorRef) extends Actor with WordBuilder with LetterTurner {
  override def receive: Receive = behaviour(initialGameState)

  def behaviour(gameState: Game): Receive = {
    case TurnLetter =>
      turnLetter(gameState).foreach { newGameState =>
        gameStateListener ! newGameState
        context.become(behaviour(newGameState))
      }

    case GuessWord(word) =>
      buildWord(gameState, word).foreach { newGameState =>
        gameStateListener ! newGameState
        context.become(behaviour(newGameState))
      }
  }
}