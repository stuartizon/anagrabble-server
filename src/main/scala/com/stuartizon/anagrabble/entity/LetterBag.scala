package com.stuartizon.anagrabble.entity

case class LetterBag(letters: List[Char])

object LetterBag {
  def build(letterCounts: Map[Char, Int]): LetterBag = LetterBag {
    ('A' to 'Z').toList.flatMap(char => List.fill(letterCounts.getOrElse(char, 0))(char))
  }
}
