package com.github.alikemalocalan.scala

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, _}

import scala.concurrent.duration._

class HttpSenderActor  extends Actor  with ActorLogging {


  implicit val system = context.system

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NullPointerException => Restart
      case _: java.util.concurrent.ExecutionException => Stop
      case _: Exception => Escalate
    }

  override def receive: Receive = {

    case record:String =>{
      log.info(s"Received [$record]")
      Http(system)
        .singleRequest(
          HttpRequest(
            HttpMethods.POST,
            s"http://0.0.0.0:9000/pulse",
            entity = HttpEntity(ContentTypes.`application/json`,record )
          )
        )
    }
  }
}
