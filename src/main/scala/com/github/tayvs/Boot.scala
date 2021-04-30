package com.github.tayvs

import com.github.tayvs.JsonFormatDeriver.gen
import com.github.tayvs.annotation._
import spray.json._

object Boot extends App with DefaultJsonProtocol {

  case class Dog(hotdogsIn: Long, @Snake manufactureName: String)

  @Snake
  case class SnakeDog(hotdogsIn: Long, manufactureName: String)

  case class DickedDog(hotDogsCount: Long, dick: Boolean = true)
  case class HairedDog(hotDogsCount: Long, dick: Boolean, hair: Option[String] = None)
  case class CustomName(@Name("name") orgName: String)

  println(Dog(1000L, "Japan").toJson.prettyPrint)
  println(SnakeDog(1000L, "Japan").toJson.prettyPrint)
  println(HairedDog(100_000L, true, None).toJson.prettyPrint)
  println(DickedDog(100_000L).toJson.prettyPrint)
  println(CustomName("someValue").toJson.prettyPrint)

  println()
  println("#" * 80)
  println()

  println(SnakeDog(100_000L, "China").toJson.toString().parseJson.convertTo[SnakeDog])
  println(Dog(100_000L, "China").toJson.toString().parseJson.convertTo[Dog])
  println("""{"hotDogsCount":100000,"dick":true}""".parseJson.convertTo[HairedDog])
  println(HairedDog(100_000L, true, None).toJson.toString().parseJson.convertTo[HairedDog])
  println("""{"hotDogsCount":100000}""".parseJson.convertTo[DickedDog])
  println(DickedDog(100_000L).toJson.toString().parseJson.convertTo[DickedDog])
  println("""{"name": "test"}""".parseJson.convertTo[CustomName])
  println(CustomName("someValue").toJson.prettyPrint.parseJson.convertTo[CustomName])

  println()
  println("#" * 80)
  println()

  println("success")

}
