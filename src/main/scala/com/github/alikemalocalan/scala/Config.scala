package com.github.alikemalocalan.scala

import cakesolutions.kafka.KafkaConsumer.Conf
import cakesolutions.kafka.akka.KafkaConsumerActor
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._

trait Config {
  private val config = ConfigFactory.load().getConfig("consumer")
  private val httpConfig = ConfigFactory.load().getConfig("destination-http")

  val consumerConf: Conf[String, String] = Conf(
    new StringDeserializer,
    new StringDeserializer,
    groupId = config.getString("groupId"),
    autoOffsetReset = OffsetResetStrategy.EARLIEST
  )
    .withConf(config)
  val actorConf = KafkaConsumerActor.Conf(1.seconds, 3.seconds)

  val httpClientAdress: String = httpConfig.getString("adress")
}