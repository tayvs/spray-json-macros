name := "spray-json-macros"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "com.propensive" %% "magnolia" % "0.17.0",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
  "io.spray" %% "spray-json" % "1.3.6"
)
