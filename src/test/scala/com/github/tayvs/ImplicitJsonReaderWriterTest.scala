package com.github.tayvs

import spray.json._
import derive.JsonFormatDeriver.gen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

case class TestFunDefault(
  a: String = "default",
  b: Int
) extends Product


class ImplicitJsonReaderWriterTest extends AnyWordSpec with Matchers {

  "Implicit JSON reader and writer definition" should {

    val json = JsObject("a" -> JsString("default"), "b" -> JsNumber(5))
    val obj = TestFunDefault(b = 5)

    val genJson = obj.toJson

    "works on case class with default value" in {
      genJson shouldBe json
    }

  }
}
