package com.stuartizon.anagrabble.entity

import io.circe.{Encoder, Json}

import scala.util.Random

class LetterBag(private val letters: List[Char]) {
  def nonEmpty: Boolean = letters.nonEmpty

  def takeRandomLetter: (Char, LetterBag) = {
    val index = Random.nextInt(letters.length)
    val (before, after) = letters.splitAt(index)
    (after.head, new LetterBag(before ++ after.tail))
  }
}

object LetterBag {
  def apply(chars: Char*): LetterBag = new LetterBag(chars.toList)

  def build(letterCounts: Map[Char, Int]): LetterBag = new LetterBag(
    ('A' to 'Z').toList.flatMap(char => List.fill(letterCounts.getOrElse(char, 0))(char))
  )

  implicit val encoder: Encoder[LetterBag] =
    (letterBag: LetterBag) => Json.fromInt(letterBag.letters.size)
}