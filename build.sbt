

name := "akka-kafka-http-consumer"


version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.22",
  "com.typesafe.akka" %% "akka-http"         % "10.1.5",
  "com.typesafe.akka" %% "akka-slf4j"        % "2.5.12",
  "org.slf4j"          % "slf4j-api"         % "1.7.25",
  "ch.qos.logback"     % "logback-classic"   % "1.2.3",
  "com.typesafe"       % "config"            % "1.3.2",
  "com.github.scopt"  %% "scopt"             % "3.7.0"
)

