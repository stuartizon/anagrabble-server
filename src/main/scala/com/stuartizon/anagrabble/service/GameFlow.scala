package com.stuartizon.anagrabble.service

import akka.NotUsed
import akka.actor.{PoisonPill, Props}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import com.stuartizon.anagrabble.entity.{Game, PlayerCommandWithName}

import scala.PartialFunction.empty

class GameFlow(initialGameState: Game, dictionary: Dictionary)(implicit materializer: Materializer) {
  private val (gameStateListener, gameStateSource) = Source.actorRef[Game](empty, empty, 100, OverflowStrategy.fail)
    .toMat(BroadcastHub.sink)(Keep.both)
    .run()

  private val gameManager = materializer.system.actorOf(Props(classOf[GameManager], initialGameState, dictionary, gameStateListener))

  private val playerCommandSink = MergeHub.source[PlayerCommandWithName]
    .to(Sink.actorRef(gameManager, PoisonPill, _ => PoisonPill))
    .run()

  val gameFlow: Flow[PlayerCommandWithName, Game, NotUsed] =
    Flow.fromSinkAndSource(playerCommandSink, gameStateSource)
}