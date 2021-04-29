package com.github.tayvs

import com.github.tayvs.annotation._
import magnolia._
import spray.json._

import scala.language.experimental.macros

object JsonFormatDeriver extends Cache {

  type Typeclass[T] = JsonFormat[T]

  private def findNameStyle(anns: Seq[Any]): Option[NameStyle] = anns.collectFirst { case a: NameStyle => a }

  private def paramMapper[T](optCaseClass: Option[NameStyle], p: Param[Typeclass, _]): String => String =
    optCaseClass.orElse(findNameStyle(p.annotations)).map(_.nameTransformer).getOrElse(identity)

  private def deserializationException(field: String, `type`: String): Nothing =
    deserializationError(s"Excepted field $field with type ${`type`}", fieldNames = List(field))


  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] = cached(caseClass.typeName.full) {
    new Typeclass[T] {
      println("macro deriving")
      val optMapping: Option[NameStyle] = findNameStyle(caseClass.annotations)

      override def write(obj: T): JsValue = {
        JsObject(
          caseClass.parameters
            .map { p: Param[Typeclass, T] =>
              val labelTransformer = paramMapper(optMapping, p)
              val label = labelTransformer(p.label)
              label -> p.typeclass.write(p.dereference(obj))
            }
            .toMap
        )
      }

      override def read(json: JsValue): T = {
        val fields = json.asJsObject.fields
        caseClass.rawConstruct(caseClass.parameters.map { p =>
          val labelTransformer = paramMapper(optMapping, p)
          val label = labelTransformer(p.label)
          p.typeclass.read(fields.getOrElse(label, deserializationException(label, p.getClass.getSimpleName)))
        })
      }
    }
  }

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]

}

object JsonReaderDeriver {

  type Typeclass[T] = JsonReader[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    (json: JsValue) => caseClass.rawConstruct(caseClass.parameters.map(p => p.typeclass.read(json)))

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]

}

object JsonWriterDeriver {

  type Typeclass[T] = JsonWriter[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] =
    (obj: T) => JsObject(
      caseClass.parameters.map(p => p.label -> p.typeclass.write(p.dereference(obj))): _*
    )

  def dispatch[T](sealedTrait: SealedTrait[Typeclass, T]): Typeclass[T] =
    (obj: T) => sealedTrait.dispatch(obj)(subType => subType.typeclass.write(subType.cast(obj)))

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]

}


object NamingUtils {

  def camelize(word: String): String = {
    val w = pascalize(word)
    w.substring(0, 1).toLowerCase(java.util.Locale.ENGLISH) + w.substring(1)
  }

  def pascalize(word: String): String = {
    val lst = word.split("_").toList
    (lst.headOption.map(s => s.substring(0, 1).toUpperCase(java.util.Locale.ENGLISH) + s.substring(1)).get ::
      lst.tail.map(s => s.substring(0, 1).toUpperCase + s.substring(1))).mkString("")
  }

  def snakize(word: String) = {
    val spacesPattern = "[-\\s]".r
    val firstPattern = "([A-Z]+)([A-Z][a-z])".r
    val secondPattern = "([a-z\\d])([A-Z])".r
    val replacementPattern = "$1_$2"

    spacesPattern.replaceAllIn(
      secondPattern.replaceAllIn(
        firstPattern.replaceAllIn(
          word, replacementPattern), replacementPattern), "_").toLowerCase
  }

}
