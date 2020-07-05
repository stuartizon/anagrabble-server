package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, Word}

class WordBuildingService {
  /** Attempt to build the given word from existing words and upturned letters.
    *
    * Returns a [[scala.Some]] containing the new game state if the word can be built, or a [[scala.None]] if it is not
    * possible to build this word from the current game state. */
  def buildWord(game: Game, newWord: Word): Option[Game] = {
    def extraLettersRequiredForNewWord(from: Word) = newWord.value.diff(from.value).toList

    def allLettersUsedInNewWord(from: Word) = from.value.diff(newWord.value).isEmpty

    def letterPileContains(letters: List[Char]): Boolean = letters.diff(game.letters).isEmpty


    game.words.filter(allLettersUsedInNewWord)
      .map(word => word -> extraLettersRequiredForNewWord(word))
      .find {
        case (_, extraLetters) => extraLetters.nonEmpty && letterPileContains(extraLetters)
      }
      .map {
        case (word, extraLetters) => Game(game.players, newWord :: game.words.filterNot(_ == word), game.letters.diff(extraLetters))
      }
      .orElse {
        val letters = newWord.value.toList
        if (letterPileContains(letters))
          Some(Game(game.players, newWord :: game.words, game.letters.diff(letters)))
        else None
      }
  }
}