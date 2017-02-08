package com.bob.bkprocess.webapi.resource

import com.bob.bkprocess.hander._
import com.bob.bkprocess.webapi.routing.BBHttpService
import com.wordnik.swagger.annotations._
import spray.routing._

@Api(value = "banks", description = "operation about bank")
trait BankResource extends BBHttpService {

  def bankRoutes: Route = pathPrefix("banks") {
    getbank ~ getAllBank
  }

  @ApiOperation(value = "查询指定银行指定证件的办卡进度", notes = "查询指定银行指定证件的办卡进度", httpMethod = "GET", response = classOf[HanderResult])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "银行标识符", required = true, dataType = "integer", paramType = "path")
    , new ApiImplicitParam(name = "card", value = "身份证号", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "银行标识未找到")
  ))
  def getbank = get {
    path(Segment / Segment) {
      (id, card) => {
        id match {
          case "7" => {
            complete(ABCHander.run(card))
          }
          case "5" => {
            complete(CCBHander.run(card))
          }
          case "6" => {
            complete(CMBCHander.run(card))
          }
          case _ => {
            complete(HanderResult(mark = "暂时还不支持您要查的银行", List.empty[Result]))
          }
        }
      }
    }
  }

  @ApiOperation(value = "获取目前支持查询的所有银行", notes = "获取目前支持查询的所有银行信息", httpMethod = "GET", response = classOf[List[Bank]])
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "服务器出错")
  ))
  def getAllBank = get {
    complete(List(
      Bank(5, "建设银行"),
      Bank(6, "民生银行")
    ))
  }
}

case class Bank(id: Int, name: String)