package com.github.alikemalocalan.scala

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, SupervisorStrategy}
import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.akka.KafkaConsumerActor.{Confirm, Subscribe}
import cakesolutions.kafka.akka.{ConsumerRecords, Extractor, KafkaConsumerActor}


class ConsumerActor(
                     kafkaConfig: KafkaConsumer.Conf[String, String],
                     actorConfig: KafkaConsumerActor.Conf) extends Actor with ActorLogging {

  val recordsExt: Extractor[Any, ConsumerRecords[String, String]] = ConsumerRecords.extractor[String, String]
  val consumer: ActorRef = context.actorOf(
    KafkaConsumerActor.props(kafkaConfig, actorConfig, self)
  )

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10) {
    case _: KafkaConsumerActor.ConsumerException =>
      log.info("Consumer exception caught. Restarting consumer.")
      SupervisorStrategy.Restart
    case _ => SupervisorStrategy.Escalate
  }

  consumer ! Subscribe.AutoPartition(List("logs_broker"))

  override def receive: Receive = {

    // Records from Kafka
    case recordsExt(records) =>
      processRecords(records.pairs)
      sender() ! Confirm(records.offsets, commit = true)
  }

  private def processRecords(records: Seq[(Option[String], String)]) =
    records.foreach { case (key, value) =>
      log.info(s"Received [$value]")
    }
}
