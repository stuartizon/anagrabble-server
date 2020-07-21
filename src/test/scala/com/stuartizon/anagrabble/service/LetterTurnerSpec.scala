package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, LetterBag}
import org.specs2.mutable.Specification

class LetterTurnerSpec extends Specification with LetterTurner {

  "Letter turner" should {
    "turn a letter when there are letters left in the bag" in {
      val initialGameState = Game(Nil, Nil, List('s', 'y'), LetterBag('k'))
      turnLetter(initialGameState) must beSome[Game].which { game =>
        game.letters must contain(exactly('s', 'y', 'k')) and
          (game.letterBag.letters must beEmpty)
      }
    }

    "return none if the letter bag is empty" in {
      val initialGameState = Game(Nil, Nil, List('s', 'y'), LetterBag())
      turnLetter(initialGameState) must beNone
    }
  }
}
