package com.stuartizon.anagrabble.service

import akka.NotUsed
import akka.actor.{Actor, ActorRef, PoisonPill}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import com.stuartizon.anagrabble.entity.{Game, PlayerCommandWithName}

import scala.PartialFunction.empty

class GameFlow(eventBus: GameEventBus, gameManager: ActorRef)(implicit materializer: Materializer) {
  def gameFlow(): Flow[PlayerCommandWithName, Game, NotUsed] = {
    val (gameStateListener, gameStateSource) = Source.actorRef[Game](empty, empty, 100, OverflowStrategy.fail)
      .preMaterialize()

    eventBus.subscribe(gameStateListener, 1)

    val playerCommandSink =
      MergeHub.source[PlayerCommandWithName]
        .to(Sink.actorRef(gameManager, PoisonPill, _ => PoisonPill))
        .run()

    Flow.fromSinkAndSource(playerCommandSink, gameStateSource)
  }
}