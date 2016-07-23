package com.example.circuitbreaker

import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success
import scala.language.postfixOps

import akka.actor.ActorSystem
import akka.actor.Props
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Timeout.durationToTimeout

object BootApp extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  
  implicit val system = ActorSystem("circuit-breakers")
  implicit val timeout: Timeout = 5 seconds

  val log = Logging(system, this.getClass)
  val geoIp = system.actorOf(Props[GeoIpActor], "geo-ip-actor")
  val result = (geoIp ? GeoIp("8.8.8.8")).mapTo[GeoIpDetails]
  
  result andThen { 
    case Success(details) => log.info("GEO IP: {}", details)
    case Failure(ex) => log.error("Communication error", ex)
  } onComplete { _ =>
    Http().shutdownAllConnectionPools().onComplete { _ => 
      system.terminate() 
    }
  }
}