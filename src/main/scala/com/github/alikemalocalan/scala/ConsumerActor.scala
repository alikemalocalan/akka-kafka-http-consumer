package com.github.alikemalocalan.scala

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import cakesolutions.kafka.akka.KafkaConsumerActor.{Confirm, Subscribe}
import cakesolutions.kafka.akka.{ConsumerRecords, Extractor, KafkaConsumerActor}


class ConsumerActor extends Actor with ActorLogging with Config {

  val recordsExt: Extractor[Any, ConsumerRecords[String, String]] = ConsumerRecords.extractor[String, String]
  val consumer: ActorRef = context.actorOf(KafkaConsumerActor.props(consumerConf, actorConf, self))

  var workerActorRouter: Router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props(new HttpSenderActor()))
      context.watch(r)
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 5) {
    case _: KafkaConsumerActor.ConsumerException =>
      log.info("Consumer exception caught. Restarting consumer.")
      SupervisorStrategy.Restart
    case _ => SupervisorStrategy.Escalate
  }

  consumer ! Subscribe.AutoPartition(List("logs_broker"))

  override def receive: Receive = {

    // Records from Kafka
    case recordsExt(records) => {
      records.values.foreach(workerActorRouter.route(_, sender()))
      sender() ! Confirm(records.offsets, commit = true)
    }
    case Terminated(s) =>
      log.error(s"${s.toString()} is terminated and will be killed.")
      workerActorRouter = workerActorRouter.removeRoutee(s)
      val r = context.actorOf(Props(new HttpSenderActor()))
      context.watch(r)
      workerActorRouter = workerActorRouter.addRoutee(r)

    case _ => log.error("Not known type as incoming message")
  }
}
