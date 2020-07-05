package com.stuartizon.anagrabble.entity

case class Game(players: List[Player] = Nil, words: List[Word] = Nil, letters: List[Char] = Nil)

case class Player(id: Long, name: String)

case class Word(value: String, playerId: Long)