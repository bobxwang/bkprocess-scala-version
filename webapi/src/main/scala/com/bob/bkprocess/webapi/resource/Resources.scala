package com.bob.bkprocess.webapi.resource

import spray.routing.ExceptionHandler
import spray.util.LoggingContext

/**
 * Created by bob on 16/3/23.
 */
trait Resources extends PeopleResource with BankResource {

  /**
   * 全局统一出错处理
   * @param log
   * @return
   */
  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: Exception =>
        requestUri { uri =>
          log.warning(e.getMessage, e)
          log.warning("Request to {} could not be handled normally", uri)
          complete(500, "Bad numbers, bad result!!!")
        }
    }
}