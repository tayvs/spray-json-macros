ThisBuild / organization := "com.github.tayvs"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.5"

lazy val hello = (project in file("."))
  .settings(
    name := "spray-json-macros",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
      "com.propensive" %% "magnolia" % "0.17.0",
      "io.spray" %% "spray-json" % "1.3.6"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.7" % Test
    )
  )