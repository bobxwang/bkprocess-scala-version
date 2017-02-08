package com.bob.bkprocess.webapi.config

import com.typesafe.config.ConfigFactory

/**
 * Created by bob on 16/3/23.
 */
object AppConfig {

  private val config = ConfigFactory.load()

  object HttpConfig {
    private val httpConfig = config.getConfig("http")
    lazy val host = httpConfig.getString("host")
    lazy val port = httpConfig.getInt("port")
  }

}