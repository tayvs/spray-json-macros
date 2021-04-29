name := "spray-json-macros"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies += "com.propensive" %% "magnolia" % "0.16.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.6"
