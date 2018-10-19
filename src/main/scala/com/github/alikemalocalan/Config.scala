package com.github.alikemalocalan

import scopt.OptionParser

object Config {
  case class Args(
      kafkaNodes: List[String] = List(),
      httpUrl: String = "",
      httpPort: String = "80",
      topics: List[String] = List(),
      groupId: String = "group1",
      clientId: String = "kafka-to-http-consumer"
  )

  val argsParser: OptionParser[Args] = new scopt.OptionParser[Args]("kafka-consumer") {
    opt[Seq[String]]('b', "kafka-nodes")
      .action((input, args) => args.copy(kafkaNodes = input.toList))

    opt[String]('c', "http-url")
      .action((input, args) => args.copy(httpUrl = input))

    opt[String]('d', "http-port")
      .action((input, args) => args.copy(httpPort = input))

    opt[Seq[String]]('e', "topics")
      .action((input, args) => args.copy(topics = input.toList))

    opt[String]('f', "group-id")
      .action((input, args) => args.copy(groupId = input))

    opt[String]('g', "client-id")
      .action((input, args) => args.copy(clientId = input))
  }
}
