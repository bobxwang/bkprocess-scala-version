package com.bob.bkprocess.hander

import akka.actor.ActorSystem

/**
 * Created by bob on 16/3/25.
 */
trait Hander {

  implicit val lglobal = scala.concurrent.ExecutionContext.Implicits.global

  implicit val system = ActorSystem()

  def bankid: Int
}

/**
 * 每张卡的信息
 * @param cardName 卡名
 * @param date 日期
 * @param process 进度
 */
case class Result(cardName: String, date: String, process: String)

/**
 * 返回具体结果
 * @param mark
 * @param result
 */
case class HanderResult(mark: String, result: List[Result])