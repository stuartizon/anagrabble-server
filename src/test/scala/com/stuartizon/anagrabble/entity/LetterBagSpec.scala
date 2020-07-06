package com.stuartizon.anagrabble.entity

import org.specs2.mutable.Specification

class LetterBagSpec extends Specification {
  "Letter bag" should {
    "create a new bag from supplied letter counts" in {
      val letterCounts = Map('A' -> 2, 'B' -> 3, 'C' -> 4)
      LetterBag.build(letterCounts).letters must contain(exactly(
        'A', 'A', 'B', 'B', 'B', 'C', 'C', 'C', 'C'
      ))
    }
  }
}
