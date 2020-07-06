package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.Game

trait LetterTurner {
  def turnLetter(game: Game): Option[Game] = {
    if (game.letterBag.letters.nonEmpty) {
      val (newLetter, newLetterBag) = game.letterBag.takeRandomLetter
      Some(game.addLetter(newLetter).withLetterBag(newLetterBag))
    }
    else None
  }
}
