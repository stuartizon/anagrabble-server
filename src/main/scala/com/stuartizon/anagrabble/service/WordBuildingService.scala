package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, Word}

class WordBuildingService {
  /** Attempt to build the given word from existing words and upturned letters.
    *
    * Returns a [[scala.Some]] containing the new game state if the word can be built, or a [[scala.None]] if it is not
    * possible to build this word from the current game state. */
  def buildWord(game: Game, newWord: Word): Option[Game] = ???
}
