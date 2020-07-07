package com.stuartizon.anagrabble.service

import akka.NotUsed
import akka.actor.{PoisonPill, Props}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import com.stuartizon.anagrabble.entity.{Game, PlayerCommand}

import scala.PartialFunction.empty

class GameFlow(initialGameState: Game)(implicit materializer: Materializer) {
  private val (gameStateListener, gameStateSource) = Source.actorRef[Game](empty, empty, 100, OverflowStrategy.fail)
    .toMat(BroadcastHub.sink)(Keep.both)
    .run()

  private val gameManager = materializer.system.actorOf(Props(classOf[GameManager], initialGameState, gameStateListener))

  private val playerCommandSink = MergeHub.source[PlayerCommand]
    .to(Sink.actorRef(gameManager, PoisonPill, _ => PoisonPill))
    .run()

  val gameFlow: Flow[PlayerCommand, Game, NotUsed] =
    Flow.fromSinkAndSource(playerCommandSink, gameStateSource)
}