package com.stuartizon.anagrabble.config

import com.typesafe.config.ConfigFactory
import scala.collection.convert.ImplicitConversionsToScala._

trait AnagrabbleConfig {
  private val config = ConfigFactory.load()

  val host: String = config.getString("http.host")
  val port: Int = config.getInt("http.port")

  val letterCounts: Map[Char, Int] = {
    val letters = config.getObject("letters").toConfig
    letters.root().keySet().map(key => key.charAt(0).toUpper -> letters.getInt(key)).toMap
  }
}
