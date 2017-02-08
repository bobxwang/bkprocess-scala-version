package com.bob.bkprocess.hander

import spray.client.pipelining._
import spray.http.HttpHeaders.RawHeader
import spray.http.{FormData, HttpRequest, HttpResponse}

import scala.concurrent.Future

/**
 * 农业银行
 * Created by bob on 16/3/25.
 */
object ABCHander extends Hander {

  override def bankid: Int = 7

  private val heads = List(RawHeader("Host", "wx.abchina.com"), RawHeader("Origin", "https://wx.abchina.com"), RawHeader("Referer", "https://wx.abchina.com/WebSite/CardStatusAuthenticationAct.ebf"))
  private val pipeline: HttpRequest => Future[HttpResponse] = {
    addHeaders(heads) ~> sendReceive
  }

  def run(idcard: String): Future[HanderResult] = {
    Future {
      HanderResult(mark = "暂时还不支持农行", List.empty[Result])
    }
  }

  def realrun(): Unit = {
    pipeline(Post("https://wx.abchina.com/WebSite/CardStatusQueryAct.ebf", FormData(Seq(
      "cCertType" -> "I",
      "iCertId" -> "350124198604220155",
      "openId" -> "oHFX_jqLqUeHgkFB-0n-6H6Mxe98",
      "appId" -> "wx51fdf61c0de4ab0b"
    )))).map(rs => {
      if (rs.status.isSuccess) {
        println(rs.entity.asString)
      }
      else {
        println(rs.entity.asString)
      }
    })
  }
}