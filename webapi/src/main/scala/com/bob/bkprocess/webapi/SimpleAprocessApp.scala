package com.bob.bkprocess.webapi

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

/**
 * Created by bob on 16/3/21.
 */
object SimpleAprocessApp extends App with SimpleRoutingApp {

  implicit val system = ActorSystem("my-system")

  startServer(interface = "localhost", port = 8080) {
    path("hello") {
      get {
        complete {
          <h1>Say hello to spray</h1>
        }
      }
    }
  }
}