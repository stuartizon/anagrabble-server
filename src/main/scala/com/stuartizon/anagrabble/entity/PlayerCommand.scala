package com.stuartizon.anagrabble.entity

trait PlayerCommand

object PlayerCommand {

  case object TurnLetter extends PlayerCommand

  case class GuessWord(word: Word) extends PlayerCommand

}