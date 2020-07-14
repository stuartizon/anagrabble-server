package com.stuartizon.anagrabble.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.Specs2RouteTest
import org.specs2.mutable.Specification

class PingRouteSpec extends Specification with PingRoute with Specs2RouteTest {
  "Ping" should {
    "return pong" in {
      Get("/ping") ~> ping ~> check {
        status must beEqualTo(StatusCodes.OK)
        entityAs[String] must beEqualTo("pong")
      }
    }
  }
}
