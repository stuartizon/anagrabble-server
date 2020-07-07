package com.stuartizon.anagrabble.entity

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class LetterBagSpec extends Specification with Mockito {
  "Letter bag" should {
    "create a new bag from supplied letter counts" in {
      val letterCounts = Map('a' -> 2, 'b' -> 3, 'c' -> 4)
      LetterBag.build(letterCounts).letters must contain(exactly(
        'a', 'a', 'b', 'b', 'b', 'c', 'c', 'c', 'c'
      ))
    }
  }
}
