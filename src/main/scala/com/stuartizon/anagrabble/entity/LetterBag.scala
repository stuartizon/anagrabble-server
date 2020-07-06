package com.stuartizon.anagrabble.entity

import com.stuartizon.anagrabble.config.AnagrabbleConfig

import scala.util.Random

case class LetterBag(letters: List[Char]) {
  def takeRandomLetter: (Char, LetterBag) = {
    val index = Random.nextInt(letters.length)
    val (before, after) = letters.splitAt(index)
    (after.head, new LetterBag(before ++ after.tail))
  }
}

object LetterBag {
  def apply(chars: Char*): LetterBag = LetterBag(chars.toList)

  def build(config: AnagrabbleConfig): LetterBag = LetterBag {
    ('A' to 'Z').toList.flatMap(char => List.fill(config.letterCounts.getOrElse(char, 0))(char))
  }
}