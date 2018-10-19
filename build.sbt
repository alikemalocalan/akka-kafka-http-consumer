

name := "akka-kafka-http-consumer"

scalaVersion := "2.12.6"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.14",
  "org.apache.kafka" % "kafka-clients" % "1.1.1",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "net.cakesolutions" %% "scala-kafka-client-akka" % "1.1.1"

)