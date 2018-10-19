

name := "akka-kafka-http-consumer"

scalaVersion := "2.12.6"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies ++={
  val akkaV = "2.5.12"
  val kafkaV = "1.1.0"
  val logbackV = "1.2.3"
  val slf4jV = "1.7.25"
  Seq(
    "net.cakesolutions" %% "scala-kafka-client-akka" % kafkaV,
    "org.apache.kafka" % "kafka-clients" % kafkaV,
    "com.typesafe.akka" %% "akka-http-core" % "10.1.5",
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "ch.qos.logback" % "logback-classic" % logbackV
  )
}