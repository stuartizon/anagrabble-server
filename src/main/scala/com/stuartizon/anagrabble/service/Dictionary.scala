package com.stuartizon.anagrabble.service

import scala.io.Source
import scala.util.Using

class Dictionary {
  private val entries = {
    val wordWithRoot = "(\\w+)\\s(\\w+)".r

    Using.resource(Source.fromResource("dictionary.txt")) {
      _.getLines().toList.map {
        case wordWithRoot(word, root) => word -> root
        case word => word.trim -> word.trim
      }
    }.toMap
  }

  /** Find the root of a given word
    *
    * Returns a [[scala.Some]] containing the root of the given word (which may be the word itself) if the word is in the dictionary.
    * Returns a [[scala.None]] if this is not a dictionary word. */
  def findRoot(word: String): Option[String] = entries.get(word)
}
