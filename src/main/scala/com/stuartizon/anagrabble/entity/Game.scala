package com.stuartizon.anagrabble.entity

case class Game(players: List[Player] = Nil, words: List[Word] = Nil, letters: List[Char] = Nil, letterBag: LetterBag) {
  def addWord(word: Word): Game = copy(words = word :: words)

  def removeWord(word: Word): Game = copy(words = words.filterNot(_ == word))

  def removeLetters(letters: List[Char]): Game = copy(letters = this.letters.diff(letters))
}

case class Player(id: Long, name: String)

case class Word(value: String, playerId: Long)