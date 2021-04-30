package com.github.tayvs.annotation

import com.github.tayvs.derive.NamingUtils

import scala.annotation.StaticAnnotation

sealed abstract class NameStyle(val nameTransformer: String => String) extends StaticAnnotation

final class Snake extends NameStyle(NamingUtils.snakize)
final class Camel extends NameStyle(NamingUtils.camelize)
final class Pascal extends NameStyle(NamingUtils.pascalize)

final class Name(name: String) extends NameStyle(_ => name)
