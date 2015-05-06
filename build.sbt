import AssemblyKeys._

assemblySettings

scalaVersion := "2.10.4"

version := "1.0-SNAPSHOT"

name := "Gossip implementation in Scala"

libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += "joda-time" % "joda-time" % "2.7"

libraryDependencies += "junit" % "junit" % "4.8.1" % "test"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

jarName in assembly := "com.protocol.gossip.jar"

resolvers += Resolver.mavenLocal
