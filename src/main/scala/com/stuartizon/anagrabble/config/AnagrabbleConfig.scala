package com.stuartizon.anagrabble.config

import com.typesafe.config.ConfigFactory

import scala.jdk.CollectionConverters._

trait AnagrabbleConfig {
  private val config = ConfigFactory.load()

  val host: String = config.getString("http.host")
  val port: Int = config.getInt("http.port")

  val letterCounts: Map[Char, Int] = {
    val letters = config.getObject("letters").toConfig
    letters.root().keySet().asScala.map(key => key.charAt(0).toLower -> letters.getInt(key)).toMap
  }
}
