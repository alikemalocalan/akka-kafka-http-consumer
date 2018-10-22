package com.github.alikemalocalan.scala

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import cakesolutions.kafka.akka.KafkaConsumerActor.{Confirm, Subscribe}
import cakesolutions.kafka.akka.{ConsumerRecords, KafkaConsumerActor}
import org.apache.kafka.common.KafkaException
import org.apache.kafka.common.config.ConfigException


class ConsumerActor extends Actor with ActorLogging with Config {

  //Type of Records
  val recordsExt = ConsumerRecords.extractor[String, String]
  val consumer: ActorRef = context.actorOf(KafkaConsumerActor.props(kafkaConsumerConf, actorConf, self))

  var actorOfHttpSender: ActorRef = context.actorOf(Props(new HttpSenderActor()))

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3) {
    case _: KafkaConsumerActor.ConsumerException => Restart
    case _:ConfigException => Stop
    case _:KafkaException => Stop
    case _ => Escalate
  }

  consumer ! Subscribe.AutoPartition(List(kafkaTopic))

  override def receive: Receive = {
    // Records from Kafka
    case recordsExt(records) =>
      records.values.foreach(actorOfHttpSender ! _)
      sender() ! Confirm(records.offsets, commit = true)
    case _ => log.error("Not known type as incoming message")
  }
}
