package com.github.alikemalocalan

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, _}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import Config._
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.{ExecutionContextExecutor, Future}


object Application {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("kafka-consumer-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val logger: LoggingAdapter = Logging(system, "kafka-consumer")

    val kafkaConfig = system.settings.config.getConfig("akka.kafka.consumer")
    val config = argsParser.parse(args, Args()).get

    val consumerSettings = ConsumerSettings(kafkaConfig, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers(config.kafkaNodes.mkString(","))
      .withGroupId(config.groupId)
      .withClientId(config.clientId)

    Consumer.committableSource(consumerSettings, Subscriptions.topics(config.topics: _*))
      .mapAsync(1) { message =>
        val messageBytes = message.record.value
        val messageString = messageBytes.map(_.toChar).mkString
        logger.debug(s"Message: $messageString")
        sendMessage(messageBytes).map { response =>
          if (response.status.isFailure)
            logger.error(s"Error: ${response.status}")
          else
            logger.error(s"Success: ${response.status}")

          message
        }
      }.mapAsync(1) { message =>
        message.committableOffset.commitScaladsl()
      }.runWith(Sink.ignore)

    def sendMessage(message: Array[Byte]): Future[HttpResponse] =
      Http(system).singleRequest(
        HttpRequest(
          HttpMethods.POST,
          s"${config.httpUrl}:${config.httpPort}/event",
          entity = HttpEntity(ContentTypes.`application/json`, message)
        )
      )
  }
}