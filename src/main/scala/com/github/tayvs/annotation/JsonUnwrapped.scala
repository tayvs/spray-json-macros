package com.github.tayvs.annotation

import scala.annotation.StaticAnnotation

final class JsonUnwrapped(val prefix: String = "", val suffix: String = "") extends StaticAnnotation
