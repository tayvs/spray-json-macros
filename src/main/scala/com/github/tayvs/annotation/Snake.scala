package com.github.tayvs.annotation

import scala.annotation.StaticAnnotation

sealed trait NameStyle extends StaticAnnotation

final class Snake extends NameStyle
final class Camel extends NameStyle
final class Pascal extends NameStyle
