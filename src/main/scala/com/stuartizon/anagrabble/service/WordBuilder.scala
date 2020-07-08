package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, Word}

trait WordBuilder {
  /** Attempt to build the given word from existing words and upturned letters.
    *
    * Returns a [[scala.Some]] containing the new game state if the word can be built, or a [[scala.None]] if it is not
    * possible to build this word from the current game state. */
  def buildWord(game: Game, newWord: Word): Option[Game] = {
    def lettersRequiredToBuildNewWord(from: Word) = newWord.value.diff(from.value).toList

    def allLettersUsedInNewWord(from: Word) = from.value.diff(newWord.value).isEmpty

    def letterPileContains(letters: List[Char]): Boolean = letters.diff(game.letters).isEmpty

    def rootHasBeenChanged(from: Word) = !newWord.root.equals(from.root)


    game.words.filter(word => allLettersUsedInNewWord(word) && rootHasBeenChanged(word))
      .map(word => word -> lettersRequiredToBuildNewWord(word))
      .find {
        case (_, lettersRequired) => lettersRequired.nonEmpty && letterPileContains(lettersRequired)
      }
      .map {
        case (word, lettersRequired) => game.removeWord(word).removeLetters(lettersRequired).addWord(newWord)
      }
      .orElse {
        val lettersRequired = newWord.value.toList
        if (letterPileContains(lettersRequired))
          Some(game.removeLetters(lettersRequired).addWord(newWord))
        else None
      }
  }
}