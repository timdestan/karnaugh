name := "Karnaugh"

version := "0.1"

scalaVersion := "2.12.0"

scalacOptions ++= Seq(
  "-feature",
  "-language:_",
  "-deprecation"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.4.2"
)

tutSettings
