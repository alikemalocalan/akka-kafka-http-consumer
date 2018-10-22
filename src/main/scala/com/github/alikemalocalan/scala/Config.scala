package com.github.alikemalocalan.scala

import cakesolutions.kafka.KafkaConsumer.Conf
import cakesolutions.kafka.akka.KafkaConsumerActor
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._

trait Config {
  private val consumer = ConfigFactory.load().getConfig("consumer")
  private val consumerConfig = consumer.getConfig("kafka")
  private val httpConfig = ConfigFactory.load().getConfig("destination-http")

  //Kafka Config
  val kafkaTopic: String =consumer.getString("topicname")
  val kafkaConsumerConf: Conf[String, String] = Conf(
    new StringDeserializer,
    new StringDeserializer,
    groupId = consumerConfig.getString("group.id")
  )
    .withConf(consumerConfig)
  val actorConf = KafkaConsumerActor.Conf(1.seconds, 3.seconds)

  val httpClientAdress: String = httpConfig.getString("adress")
}