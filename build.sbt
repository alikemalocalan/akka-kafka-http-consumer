

name := "akka-kafka-http-consumer"

scalaVersion := "2.12.6"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies ++= Seq(
  "net.cakesolutions" %% "scala-kafka-client-akka" % "1.1.1",
  "org.apache.kafka" % "kafka-clients" % "1.1.1",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.14",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "org.slf4j" % "slf4j-api"  % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)