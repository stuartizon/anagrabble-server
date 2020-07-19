package com.stuartizon.anagrabble.service

import akka.actor.{Actor, ActorRef}
import com.stuartizon.anagrabble.entity.{Game, Player, Word}
import com.stuartizon.anagrabble.entity.PlayerCommand.{GuessWord, Join, TurnLetter}

class GameManager(initialGameState: Game, dictionary: Dictionary, gameStateListener: ActorRef) extends Actor with WordBuilder with LetterTurner {
  override def receive: Receive = behaviour(initialGameState)

  def behaviour(gameState: Game): Receive = {
    case Join(playerId) =>
      val newGameState = gameState.addPlayer(Player(playerId))
      gameStateListener ! newGameState
      context.become(behaviour(newGameState))

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