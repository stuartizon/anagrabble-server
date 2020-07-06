package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.Game

trait LetterTurner {
  /** Attempt to turn over a letter from the letter bag.
    *
    * Returns a [[scala.Some]] containing the new game state if a letter can be turned, or a [[scala.None]] if there
    * are no more tiles to turn. */
  def turnLetter(game: Game): Option[Game] = {
    if (game.letterBag.letters.nonEmpty) {
      val (newLetter, newLetterBag) = game.letterBag.takeRandomLetter
      Some(game.addLetter(newLetter).withLetterBag(newLetterBag))
    }
    else None
  }
}
