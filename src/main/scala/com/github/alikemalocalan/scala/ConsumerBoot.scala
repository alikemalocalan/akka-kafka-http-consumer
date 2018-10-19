package com.github.alikemalocalan.scala

import akka.actor.{ActorSystem, Props}
import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.akka.KafkaConsumerActor
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._

object ConsumerBoot extends App {
  val config = ConfigFactory.load().getConfig("consumer")
  val consumerConf = KafkaConsumer.Conf(
    new StringDeserializer,
    new StringDeserializer,
    groupId = "test_group",
    enableAutoCommit = false,
    autoOffsetReset = OffsetResetStrategy.EARLIEST)
    .withConf(config)

  val actorConf = KafkaConsumerActor.Conf(1.seconds, 3.seconds)

  val system = ActorSystem()
  system.actorOf(Props(new ConsumerActor(consumerConf, actorConf)))

}
