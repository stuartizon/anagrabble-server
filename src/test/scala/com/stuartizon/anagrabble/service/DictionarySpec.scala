package com.stuartizon.anagrabble.service

import org.specs2.mutable.Specification

class DictionarySpec extends Specification {
  val dictionary = new Dictionary

  "Dictionary" should {
    "return entry for a root word" in {
      dictionary.findRoot("point") must beSome("point")
    }

    "return entry for a derived word" in {
      dictionary.findRoot("pointed") must beSome("point")
    }

    "return none for a non dictionary word" in {
      dictionary.findRoot("pointiness") must beNone
    }
  }
}
