package com.stuartizon.anagrabble.entity

import com.stuartizon.anagrabble.config.AnagrabbleConfig
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class LetterBagSpec extends Specification with Mockito {
  "Letter bag" should {
    "create a new bag from supplied letter counts" in {
      val config = mock[AnagrabbleConfig]
      config.letterCounts returns Map('A' -> 2, 'B' -> 3, 'C' -> 4)
      LetterBag.build(config).letters must contain(exactly(
        'A', 'A', 'B', 'B', 'B', 'C', 'C', 'C', 'C'
      ))
    }
  }
}
