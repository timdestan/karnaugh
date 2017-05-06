name := "Karnaugh"

version := "0.1"

scalaVersion := "2.12.1"

scalacOptions ++= Seq(
  "-feature",
  "-language:_",
  "-deprecation",
  "-Ypartial-unification"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.4.2",
  "org.typelevel" %% "cats" % "0.9.0"
)

tutSettings
