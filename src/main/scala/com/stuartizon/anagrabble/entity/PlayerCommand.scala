package com.stuartizon.anagrabble.entity

trait PlayerCommand

object PlayerCommand {

  case class GuessWord(word: Word) extends PlayerCommand

}