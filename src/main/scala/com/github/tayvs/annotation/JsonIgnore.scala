package com.github.tayvs.annotation

import scala.annotation.StaticAnnotation

/** Market field would be ignored on json writing */
final class JsonIgnore extends StaticAnnotation
