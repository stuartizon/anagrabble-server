package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, LetterBag, Word}
import org.specs2.mutable.Specification

class WordBuilderSpec extends Specification with WordBuilder {
  val cat = Word("cat", 1)
  val initialGameState = Game(Nil, List(cat), List('s', 'k', 'y', 'p'), LetterBag(Nil))

  "Word builder" should {
    "build a word by stealing an existing word" in {
      val cast = Word("cast", 23)
      val result = buildWord(initialGameState, cast)
      result must beSome[Game].which { game =>
        game.words must contain(exactly(cast)) and
          (game.letters must contain(exactly('k', 'y', 'p')))
      }
    }

    "build a word from new letters only" in {
      val sky = Word("sky", 456)
      val result = buildWord(initialGameState, sky)
      result must beSome[Game].which { game =>
        game.words must contain(exactly(cat, sky)) and
          (game.letters must contain(exactly('p')))
      }
    }

    "return none if word can't be built" in {
      val coat = Word("coat", 987)
      val result = buildWord(initialGameState, coat)
      result must beNone
    }
  }
}