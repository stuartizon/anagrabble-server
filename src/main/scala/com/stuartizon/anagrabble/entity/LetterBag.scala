package com.stuartizon.anagrabble.entity

import com.stuartizon.anagrabble.config.AnagrabbleConfig

case class LetterBag(letters: List[Char])

object LetterBag {
  def build(config: AnagrabbleConfig): LetterBag = LetterBag {
    ('A' to 'Z').toList.flatMap(char => List.fill(config.letterCounts.getOrElse(char, 0))(char))
  }
}
