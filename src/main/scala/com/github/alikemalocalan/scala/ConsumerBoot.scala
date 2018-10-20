package com.github.alikemalocalan.scala

import akka.actor.{ActorSystem, Props}

object ConsumerBoot extends App with Config {

  val system = ActorSystem().actorOf(Props(new ConsumerActor()))

}
