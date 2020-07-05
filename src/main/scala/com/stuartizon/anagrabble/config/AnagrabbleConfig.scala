package com.stuartizon.anagrabble.config

import com.typesafe.config.ConfigFactory

trait AnagrabbleConfig {
  private val config = ConfigFactory.load()

  val host: String = config.getString("http.host")
  val port: Int = config.getInt("http.port")
}
