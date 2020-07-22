package com.stuartizon.anagrabble.service

import akka.actor.ActorRef
import akka.event.{ActorEventBus, LookupClassification}
import com.stuartizon.anagrabble.entity.Game

class GameEventBus extends ActorEventBus with LookupClassification {
  override type Event = Game
  override type Classifier = Int

  override protected def mapSize(): Int = 32

  override protected def classify(game: Game): Int = 1

  override protected def publish(game: Game, subscriber: ActorRef): Unit = subscriber ! game
}