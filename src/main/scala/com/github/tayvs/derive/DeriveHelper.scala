package com.github.tayvs.derive

import com.github.tayvs.annotation._
import magnolia.{CaseClass, Param}
import spray.json.deserializationError

import scala.util.control.NoStackTrace

private[derive] object DeriveHelper {
  def findNameStyle(anns: Seq[Any]): Option[NameStyle] = anns.collectFirst { case a: NameStyle => a }

  def paramMapper[T, TC[_]](optCaseClass: Option[NameStyle], p: Param[TC, T]): String => String =
    optCaseClass.orElse(findNameStyle(p.annotations)).map(_.nameTransformer).getOrElse(identity)

  def deserializationException(field: String, `type`: String): Nothing =
    deserializationError(s"Excepted field $field with type ${`type`}", fieldNames = List(field))

  case class DeriverError(msg: String) extends Exception(msg) with NoStackTrace

  object DeriverError {
    val invalidNameAnnotationLevel: DeriverError = DeriverError("Name annotation should be used only for constructor parameters")
  }

  //TODO: try to throw error at compile time
  def validateAnnotation[T, TC[_]](caseClass: CaseClass[TC, T]): Unit =
    caseClass.annotations.collectFirst { case _: Name => throw DeriverError.invalidNameAnnotationLevel }
}