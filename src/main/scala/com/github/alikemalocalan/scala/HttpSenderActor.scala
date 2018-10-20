package com.github.alikemalocalan.scala

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class HttpSenderActor extends Actor with ActorLogging with Config {


  implicit val system = context.system

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NullPointerException => Restart
      case _: java.util.concurrent.ExecutionException => Stop
      case _: Exception => Escalate
    }

  override def receive: Receive = {

    case record: String => {
      log.info(s"Received [$record]")
      Http(system)
        .singleRequest(
          HttpRequest(
            HttpMethods.POST,
            httpClientAdress,
            entity = HttpEntity(ContentTypes.`application/json`, record)
          )
        ).flatMap { response =>
        response.status match {
          case status if status.isSuccess =>
            log.debug("Request successfull to Http")
            Future.successful("Request successfull to Http")
          case status if status.isFailure =>
            log.error("Bad Request")
            Future.failed(new Exception("Bad Request"))
        }
      }
    }
  }
}
