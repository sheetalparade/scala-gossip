import AssemblyKeys._

assemblySettings

scalaVersion := "2.11.6"

version := "1.0-SNAPSHOT"

name := "Gossip implementation in Scala"

libraryDependencies += "log4j" % "log4j" % "1.2.17" % "provided"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.3.10"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

jarName in assembly := "gossip.jar"

resolvers += Resolver.mavenLocal
