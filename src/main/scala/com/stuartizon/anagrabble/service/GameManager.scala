package com.stuartizon.anagrabble.service

import akka.actor.{Actor, ActorRef}
import com.stuartizon.anagrabble.entity.Game
import com.stuartizon.anagrabble.entity.PlayerCommand.GuessWord

class GameManager(initialGameState: Game, gameStateListener: ActorRef) extends Actor with WordBuilder {
  override def receive: Receive = behaviour(initialGameState)

  def behaviour(gameState: Game): Receive = {
    case GuessWord(word) =>
      buildWord(gameState, word).foreach { newGameState =>
        gameStateListener ! newGameState
        context.become(behaviour(newGameState))
      }
  }
}