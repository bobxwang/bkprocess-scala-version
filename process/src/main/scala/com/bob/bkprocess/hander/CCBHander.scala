package com.bob.bkprocess.hander

import akka.event.Logging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spray.client.pipelining._
import spray.http.HttpHeaders.{Cookie, RawHeader, `User-Agent`, `Set-Cookie`}
import spray.http.{FormData, HttpRequest, HttpResponse}

import scala.concurrent.Future

/**
 * 建设银行
 * Created by bob on 16/3/25.
 */
object CCBHander extends Hander {

  override def bankid: Int = 5

  private val heads = List(
    RawHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
    , RawHeader("Accept-Encoding", "gzip,deflate,sdch")
    , RawHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,nl;q=0.4,zh-TW;q=0.2")
    , RawHeader("Connection", "keep-alive")
    , RawHeader("Cache-Control", "no-cache")
    , RawHeader("Pragma", "no-cache")
    , `User-Agent`.apply("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.5 Safari/537.36")
  )

  private val logResponse: HttpResponse => HttpResponse = { r => r }
  private val log = Logging.getLogger(system, this)

  private val pipeline: HttpRequest => Future[HttpResponse] = {
    addHeaders(heads) ~> logRequest(r => {
      r.headers.foreach(x => log.warning(x.toString()))
    }) ~> sendReceive ~> logResponse
  }

  /**
   *
   * @param idcard
   */
  def run(idcard: String): Future[Future[HanderResult]] = {
    pipeline(Get("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=B2CMainPlatV5&TXCODE=E13101"))
      .map(rg => {
      val postdata = FormData(Seq(
        "CERT_ID" -> idcard,
        "BTNPay" -> "查询",
        "TXCODE" -> "E13100",
        "BRANCHID" -> "310000000"
      ))
      val httpCookie = rg.headers.collect { case `Set-Cookie`(hc) => hc }
      pipeline(
        Post("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=WCCMainPlatV5", postdata).withHeaders(Cookie(httpCookie))
      ).map(rs => {
        //        val res = rs.entity.asString.replace("&nbsp;", "").trim
        //        val regexnotfound = """ <p class="p1">(.+)。</p> """.trim.r
        //        println(res)
        //        res match {
        //          case regexnotfound(msg) => println(msg)
        //          case _ => println("nothing")
        //        }

        val doc: Document = Jsoup.parse(rs.entity.asString)
        val e = doc.select("p[class=p1]").first()
        if (e != null) {
          // mark should be 您的资料目前我们没有收到或者未录入到我们系统
          HanderResult(mark = e.html.replace("&nbsp;", "").trim, List.empty[Result])
        } else {
          HanderResult(mark = "暂时还不支持建行", List.empty[Result])
        }
      })
    })
  }
}