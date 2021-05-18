package com.github.tayvs.derive

import com.github.tayvs.annotation._
import com.github.tayvs.derive.DeriveHelper._
import com.github.tayvs.utils.Cache
import magnolia._
import spray.json._

import scala.language.experimental.macros

object JsonFormatDeriver extends Cache {

  type Typeclass[T] = JsonFormat[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] = cached(caseClass.typeName.full) {
    println("macro deriving")
    new Typeclass[T] {
      private val jsonReader: JsonReader[T] = JsonReaderDeriver.combine(caseClass.asInstanceOf[CaseClass[JsonReader, T]])
      private val jsonWriter: JsonWriter[T] = JsonWriterDeriver.combine(caseClass.asInstanceOf[CaseClass[JsonWriter, T]])

      override def write(obj: T): JsValue = jsonWriter.write(obj)

      override def read(json: JsValue): T = jsonReader.read(json)
    }
  }

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]

}

object JsonReaderDeriver extends Cache {

  type Typeclass[T] = JsonReader[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] = {
    validateAnnotation(caseClass)
    cached(caseClass.typeName.full) {
      val optMapping: Option[NameStyle] = findNameStyle(caseClass.annotations)
      (json: JsValue) => {
        println("json " + json)
        println("caseclass " + caseClass.typeName)
        val fields = json.asJsObject.fields
        caseClass.rawConstruct(caseClass.parameters.map { p =>
          val labelTransformer = paramMapper(optMapping, p)
          val label = labelTransformer(p.label)
          println(s"label $label default ${p.default}")
          fields.get(label) match {
            case Some(value) => p.typeclass.read(value)
            case None if p.default.isDefined => p.default.get
            case None => p.typeclass.read(JsNull)
          }

//            .map(p.typeclass.read)
//            .orElse(p.default)
//            .getOrElse(deserializationException(label, p.getClass.getSimpleName))
        })
      }
    }
  }

  implicit def gen[T]: Typeclass[T] = macro Magnolia.gen[T]

}

object JsonWriterDeriver extends Cache {

  type Typeclass[T] = JsonWriter[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] = {
    validateAnnotation(caseClass)
    cached(caseClass.getClass.toString) {
      val optMapping: Option[NameStyle] = findNameStyle(caseClass.annotations)

      (obj: T) =>
        JsObject(
          caseClass.parameters
            .withFilter(!_.annotations.exists(_.isInstanceOf[JsonIgnore]))
            .flatMap(writeValue(obj, _, optMapping))
            .toMap
        )
    }
  }

  private def writeValue[T](obj: T, p: Param[Typeclass, T], optMapping: Option[NameStyle]): Seq[(String, JsValue)] = {
    findJsonUnwrapped(p.annotations) match {
      case Some(un) => writeUnwrappedValue(obj, p, un)
      case None => writePlainValue(obj, p, optMapping)
    }
  }

  private def writeUnwrappedValue[T](obj: T, p: Param[Typeclass, T], unwrapped: JsonUnwrapped): Seq[(String, JsValue)] = {
    val prefix = unwrapped.prefix
    val suffix = unwrapped.suffix
    p.typeclass.write(p.dereference(obj)) match {
      case JsObject(fields) => fields.map { case (k, v) => (prefix + k + suffix) -> v }.toList
      case _ => deserializationException(p.label, "JsObject")
    }
  }

  private def writePlainValue[T](obj: T, p: Param[Typeclass, T], optMapping: Option[NameStyle]): Seq[(String, JsValue)] =
    p.typeclass.write(p.dereference(obj)) match {
      case JsNull => Nil
      case value =>
        val labelTransformer = paramMapper(optMapping, p)
        val label = labelTransformer(p.label)
        List(label -> value)
    }

  //TODO
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

  def snakize(word: String): String = {
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
