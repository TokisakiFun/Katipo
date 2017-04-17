package indi.lewis.spider.html.nestfuns

import indi.lewis.spider.html.NestFunction

/**
  * Created by lewis on 2017/4/10.
  */
class Timestamp extends NestFunction{
  override def funcName: String = "timestamp"

  override def funcBody: () => String = { () =>
    System.currentTimeMillis().toString
  }
}
