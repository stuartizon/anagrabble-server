package com.stuartizon.anagrabble.entity

import io.circe.{Decoder, DecodingFailure, HCursor}

trait PlayerCommand

object PlayerCommand {

  case object TurnLetter extends PlayerCommand

  case class GuessWord(word: String, playerId: Long) extends PlayerCommand

  implicit val decoder: Decoder[PlayerCommand] = (c: HCursor) => {
    c.downField("key").as[String] match {
      case Right("TURN_LETTER") => Right(TurnLetter)
      case Right("GUESS_WORD") =>
        for {
          word <- c.downField("word").as[String]
          playerId <- c.downField("playerId").as[Long]
        } yield GuessWord(word.toLowerCase, playerId)
      case key => Left(DecodingFailure(s"Unknown command $key", Nil))
    }
  }
}