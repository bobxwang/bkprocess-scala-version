package com.bob.bkprocess.webapi

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.bob.bkprocess.webapi.config.AppConfig
import spray.can.Http

import scala.concurrent.duration._

object ProcessApp extends App {

  implicit val system = ActorSystem("my-system")

  implicit val timeout = Timeout(5.seconds)

  implicit val executionContext = system.dispatcher

  // create and start our service actor
  val service = system.actorOf(Props(new RestInterface), "myfirst-service")

  val host = AppConfig.HttpConfig.host
  val port = AppConfig.HttpConfig.port

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http).ask(Http.Bind(service, host, port = port))
    .mapTo[Http.Event]
    .map {
    case Http.Bound(address) =>
      println(s"REST interface bound to $address")
    case Http.CommandFailed(cmd) =>
      println("REST interface could not bind to " +
        s"$host:$port, ${cmd.failureMessage}")
      system.terminate()
  }
}