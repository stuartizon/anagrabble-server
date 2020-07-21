package com.stuartizon.anagrabble.entity

import io.circe.{Decoder, DecodingFailure, HCursor}

sealed trait PlayerCommand

case object Join extends PlayerCommand

case object TurnLetter extends PlayerCommand

case class GuessWord(word: String) extends PlayerCommand

object PlayerCommand {
  implicit val decoder: Decoder[PlayerCommand] = (c: HCursor) => {
    c.downField("key").as[String] match {
      case Right("TURN_LETTER") => Right(TurnLetter)
      case Right("GUESS_WORD") => c.downField("word").as[String].map(word => GuessWord(word.toLowerCase))
      case key => Left(DecodingFailure(s"Unknown command $key", Nil))
    }
  }
}

case class PlayerCommandWithName(command: PlayerCommand, player: String)