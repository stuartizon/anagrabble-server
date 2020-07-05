package com.stuartizon.anagrabble.service

import akka.actor.{Actor, ActorRef}
import com.stuartizon.anagrabble.entity.Game
import com.stuartizon.anagrabble.entity.PlayerCommand.GuessWord

class GameManager(wordBuildingService: WordBuildingService, gameStateListener: ActorRef) extends Actor {
  override def receive: Receive = behaviour(Game())

  def behaviour(gameState: Game): Receive = {
    case GuessWord(word) =>
      wordBuildingService.buildWord(gameState, word).foreach { newGameState =>
        gameStateListener ! newGameState
        context.become(behaviour(newGameState))
      }
  }
}