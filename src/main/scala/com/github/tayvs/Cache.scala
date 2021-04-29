package com.github.tayvs

trait Cache {

  private val cache = collection.mutable.WeakHashMap.empty[String, Any]

  protected def cached[T](label: String)(value: => T): T =
    cache
      .getOrElse(label, {
        val newValue = value
        cache.put(label, newValue)
        newValue
      })
      .asInstanceOf[T]

}
