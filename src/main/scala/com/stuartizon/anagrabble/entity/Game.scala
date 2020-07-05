package com.stuartizon.anagrabble.entity

case class Game(players: List[Player], words: List[Word], letters: List[Char])

case class Player(id: Long, name: String)

case class Word(value: String, playerId: Long)
