package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, LetterBag, Word}
import org.specs2.mutable.Specification

class WordBuilderSpec extends Specification with WordBuilder {
  private val cat = Word("cat", "cat", 1)
  private val initialGameState = Game(Set.empty, List(cat), List('s', 'k', 'y', 'p'), LetterBag())

  "Word builder" should {
    "build a word by stealing an existing word" in {
      val cast = Word("cast", "cast", 23)
      val result = buildWord(initialGameState, cast)
      result must beSome[Game].which { game =>
        game.words must contain(exactly(cast)) and
          (game.letters must contain(exactly('k', 'y', 'p')))
      }
    }

    "build a word from new letters only" in {
      val sky = Word("sky", "sky", 456)
      val result = buildWord(initialGameState, sky)
      result must beSome[Game].which { game =>
        game.words must contain(exactly(cat, sky)) and
          (game.letters must contain(exactly('p')))
      }
    }

    "return none if root of the word has not changed" in {
      val cats = Word("cats", "cat", 55)
      val result = buildWord(initialGameState, cats)
      result must beNone
    }

    "return none if word can't be built" in {
      val coat = Word("coat", "coat", 987)
      val result = buildWord(initialGameState, coat)
      result must beNone
    }
  }
}