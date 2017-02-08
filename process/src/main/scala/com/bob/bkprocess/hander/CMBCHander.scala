package com.bob.bkprocess.hander

import org.apache.commons.lang3.StringEscapeUtils
import org.unbescape.html.HtmlEscape
import spray.client.pipelining._
import spray.http.HttpHeaders.{Cookie, `Set-Cookie`}
import spray.http._

import scala.concurrent.Future

/**
 * 民生银行
 */
object CMBCHander extends Hander {
  override def bankid: Int = 6

  private val gbCharset = HttpCharset.custom("gb2312").getOrElse(HttpCharsets.`ISO-8859-1`)

  private val pipeline: HttpRequest => Future[HttpResponse] = {
    sendReceive
  }

  def run(idcard: String) = {
    pipeline(Get("https://ebank.cmbc.com.cn/weblogic/servlets/EService/CSM/Processh405Svlt?txcode=h40500&channelID=u"))
      .map(rg => {
      val httpCookie = rg.headers.collect { case `Set-Cookie`(hc) => hc }
      val postdata = FormData(Seq(
        "txcode" -> "h40501",
        "CC_CertiType" -> "1",
        "CC_CertiNO" -> idcard,
        "Submit" -> " 确 认 "
      ))
      pipeline(Post("https://ebank.cmbc.com.cn/weblogic/servlets/EService/CSM/MainService?txcode=h40501", postdata)
        .withHeaders(Cookie(httpCookie)))
        .map(rs => {
        val rex_table = """<table width="100%" border="0" id="formTable" cellspacing="1" cellpadding="0" bgcolor="#CCCCCC">([\s\S]+?)</table>""".trim.r
        rex_table.findFirstIn(rs.entity.asString(gbCharset)) match {
          case Some(x) => {
            val rex_td = """ <td align="center" bgcolor="#FFFFFF">(.+?)</td><td align="center" bgcolor="#FFFFFF">([\s\S]+?)</td><td align="center" bgcolor="#FFFFFF">(.+?)</td><td align="center" bgcolor="#FFFFFF">(.+?)</td><td align="center" bgcolor="#FFFFFF">(.+?)</td><td align="center" bgcolor="#FFFFFF">([\s\S]+?)</td> """.trim.r
            val result = rex_td.findAllIn(x).flatMap(y => {
              val rex_td(applyno, applystatus, cardname, cardtype, cardkind, cardstatus) = y
              val apply_status = StringEscapeUtils.unescapeHtml4(applystatus).replace("\t", "").replace("\n", "").trim
              println(s"申请单-${applyno},申请状态-${apply_status}，卡名-${cardname}，卡类型-${cardtype}，个人卡标志-${cardkind}，卡状态-${HtmlEscape.unescapeHtml(cardstatus).replace("\t", "").replace("\n", "")}")
              List(Result(cardname, "", apply_status))
            })
            HanderResult(mark = "查询结果", result.toList)
          }
          case None => HanderResult(mark = "查询结果", List.empty[Result])
        }
      })
    })
  }
}