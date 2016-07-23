package com.example.circuitbreaker

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.Uri.apply
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.pattern.CircuitBreaker
import akka.pattern.pipe
import akka.stream.ActorMaterializer

case class GeoIp(host: String)
case class GeoIpDetails(ip: String = "", countryCode: String = "", 
  countryName: String = "", latitude: Double = 0, longitude: Double = 0)

class GeoIpActor extends Actor with ActorLogging {
  import spray.json._
  import spray.json.DefaultJsonProtocol._
  
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  
  implicit val geoIpDetailsFormat = new RootJsonReader[GeoIpDetails] {
    def read(value: JsValue) = value match {
      case JsObject(fields) => GeoIpDetails(
         fields.getOrElse("ip", JsString("")).convertTo[String],
         fields.getOrElse("country_code", JsString("")).convertTo[String],
         fields.getOrElse("country_name", JsString("")).convertTo[String],
         fields.getOrElse("latitude", JsNumber(0)).convertTo[Double],
         fields.getOrElse("longitude", JsNumber(0)).convertTo[Double]
      )
      case _ => throw new DeserializationException("GeoIP Details expected")
    }
  }
  
  import context.dispatcher
  import context.system
  
  val breaker = new CircuitBreaker(
    context.system.scheduler,
    maxFailures = 5,
    callTimeout = 15 seconds,
    resetTimeout = 2 minutes)
  
  def receive = {
    case GeoIp(host) => breaker
      .withCircuitBreaker(Http()
        .singleRequest(HttpRequest(uri = s"http://freegeoip.net/json/$host"))
        .flatMap {
          case HttpResponse(OK, _, entity, _) => Unmarshal(entity).to[GeoIpDetails]
          case _ => Future.successful(GeoIpDetails())
        }
      ) pipeTo sender()
  }
}