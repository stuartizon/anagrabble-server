package com.stuartizon.anagrabble.service

import com.stuartizon.anagrabble.entity.{Game, Word}
import org.specs2.mutable.Specification

class WordBuildingServiceSpec extends Specification {
  val cat = Word("cat", 1)
  val initialGameState = Game(Nil, List(cat), List('s', 'k', 'y', 'p'))
  val wordBuildingService = new WordBuildingService

  "Word building service" should {
    "build a word by stealing an existing word" in {
      val cast = Word("cast", 23)
      val result = wordBuildingService.buildWord(initialGameState, cast)
      result must beSome[Game].which { game =>
        game.words must contain(exactly(cast)) and
          (game.letters must contain(exactly('k', 'y', 'p')))
      }
    }

    "build a word from new letters only" in {
      val sky = Word("sky", 456)
      val result = wordBuildingService.buildWord(initialGameState, sky)
      result must beSome[Game].which { game =>
        game.words must contain(exactly(cat, sky)) and
          (game.letters must contain(exactly('p')))
      }
    }

    "return none if word can't be built" in {
      val coat = Word("coat", 987)
      val result = wordBuildingService.buildWord(initialGameState, coat)
      result must beNone
    }
  }
}