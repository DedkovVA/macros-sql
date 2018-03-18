version := "1.0"

scalaVersion := "2.11.8"

lazy val root = (project in file("."))
  .settings(
    name := "macros-sql",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8"
  )


lazy val macrosDef = (Project("macros-def", file("macros-def"))
  settings(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8")
)
lazy val macrosTarget = (Project("macros-target", file("macros-target"))
  dependsOn(macrosDef)
  settings(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8")
)