package com.github.tayvs

import JsonFormatDeriver.{Typeclass, gen}
import com.github.tayvs.annotation.Snake
import spray.json._

object Boot extends App with DefaultJsonProtocol {

  case class Dog(hotdogsIn: Long, @Snake manufactureName: String)

  @Snake
  case class SnakeDog(hotdogsIn: Long, manufactureName: String)

  println(Dog(1000L, "Japan").toJson.prettyPrint)
  println(Dog(10_000L, "China").toJson.sortedPrint)
  println(Dog(100_000L, "China").toJson.sortedPrint)


  println(SnakeDog(1000L, "Japan").toJson.prettyPrint)
  println(SnakeDog(10_000L, "China").toJson.sortedPrint)
  println(SnakeDog(100_000L, "China").toJson.sortedPrint)

  println()
  println("#" * 80)
  println()

  println(SnakeDog(100_000L, "China").toJson.toString().parseJson.convertTo[SnakeDog])
  println(Dog(100_000L, "China").toJson.toString().parseJson.convertTo[Dog])

  println()
  println("#" * 80)
  println()

  println("success")

}
