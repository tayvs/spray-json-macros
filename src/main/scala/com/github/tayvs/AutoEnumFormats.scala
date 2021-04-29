package com.github.tayvs

import spray.json.{JsString, JsValue, JsonFormat}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait AutoEnumFormats {
  implicit def enumFormat[T <: Enumeration]: JsonFormat[T#Value] = macro EnumFormatMacros.enumFormatMacro[T]
}

object AutoEnumFormats extends AutoEnumFormats

object EnumFormatMacros {
  def enumFormatMacro[T <: Enumeration : c.WeakTypeTag](c: Context) = {
    import c.universe._

    val tt = weakTypeOf[T]
    val ts = tt.termSymbol

    val typeName = c.Expr[String](Literal(Constant(tt.typeSymbol.fullName)))
    val withName = c.Expr[T#Value](q"$ts.withName(str)")

    reify {
      new JsonFormat[T#Value] {
        override def write(obj: T#Value): JsValue = JsString(obj.toString)

        override def read(json: JsValue): T#Value = json match {
          case JsString(str) => withName.splice
          case x =>
            throw new IllegalArgumentException(s"${x.getClass.getName} found when trying to deserialize enum type " +
              typeName.splice + ", string expected.")
        }
      }
    }
  }
}
