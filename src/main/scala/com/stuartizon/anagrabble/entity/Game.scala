package com.stuartizon.anagrabble.entity

case class Game(players: List[String], words: List[Word], letters: List[Char], letterBag: LetterBag) {
  def addPlayer(player: String): Game = copy(players = players :+ player)

  def addWord(word: Word): Game = copy(words = word :: words)

  def removeWord(word: Word): Game = copy(words = words.filterNot(_ == word))

  def addLetter(letter: Char): Game = copy(letters = letter :: letters)

  def removeLetters(letters: List[Char]): Game = copy(letters = this.letters.diff(letters))

  def withLetterBag(letterBag: LetterBag): Game = copy(letterBag = letterBag)
}

case class Word(value: String, root: String, playerId: Long)