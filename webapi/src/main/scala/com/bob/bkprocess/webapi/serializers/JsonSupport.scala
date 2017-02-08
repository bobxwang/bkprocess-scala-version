package com.bob.bkprocess.webapi.serializers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.UUID

import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import spray.httpx.{Json4sJacksonSupport}

/**
 * Created by bob on 16/3/22.
 */
trait JsonSupport extends Json4sJacksonSupport {

  implicit def json4sJacksonFormats: Formats
  = customDateFormat ++ JodaTimeSerializers.all ++ List(CustomTimestampSerializer) + new UUIDFormat

  val customDateFormat = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  }

  def toJValue[T](value: T): JValue = {
    Extraction.decompose(value)
  }

  class UUIDFormat extends Serializer[UUID] {
    val UUIDClass = classOf[UUID]

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
      case (TypeInfo(UUIDClass, _), JString(x)) => UUID.fromString(x)
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: UUID => JString(x.toString)
    }
  }

  case object CustomTimestampSerializer extends CustomSerializer[Timestamp](format =>
    ( {
      case JInt(x) => new Timestamp(x.longValue * 1000)
      case JNull => null
    }, {
      case date: Timestamp => JInt(date.getTime / 1000)
    }))

}