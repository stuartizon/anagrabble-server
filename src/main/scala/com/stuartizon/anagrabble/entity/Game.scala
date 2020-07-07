package com.stuartizon.anagrabble.entity

case class Game(players: List[Player], words: List[Word], letters: List[Char], letterBag: LetterBag) {
  def addWord(word: Word): Game = copy(words = word :: words)

  def removeWord(word: Word): Game = copy(words = words.filterNot(_ == word))

  def addLetter(letter: Char): Game = copy(letters = letter :: letters)

  def removeLetters(letters: List[Char]): Game = copy(letters = this.letters.diff(letters))

  def withLetterBag(letterBag: LetterBag): Game = copy(letterBag = letterBag)
}

case class Player(id: Long, name: String)

case class Word(value: String, root: String, playerId: Long)