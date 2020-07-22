package com.stuartizon.anagrabble.service

import akka.actor.Actor
import com.stuartizon.anagrabble.entity._

class GameManager(initialGameState: Game, dictionary: Dictionary, eventBus: GameEventBus) extends Actor with WordBuilder with LetterTurner {
  override def receive: Receive = behaviour(initialGameState)

  def behaviour(gameState: Game): Receive = {
    case PlayerCommandWithName(Join, player) =>
      val newGameState =
        if (gameState.players.contains(player)) gameState
        else gameState.addPlayer(player)
      eventBus.publish(newGameState)
      context.become(behaviour(newGameState))

    case PlayerCommandWithName(TurnLetter, _) =>
      turnLetter(gameState).foreach { newGameState =>
        eventBus.publish(newGameState)
        context.become(behaviour(newGameState))
      }

    case PlayerCommandWithName(GuessWord(word), player) =>
      dictionary.findRoot(word).foreach { root =>
        val playerId = gameState.players.indexOf(player)
        if (playerId >= 0) {
          buildWord(gameState, Word(word, root, playerId)).foreach { newGameState =>
            eventBus.publish(newGameState)
            context.become(behaviour(newGameState))
          }
        }
      }
  }
}
