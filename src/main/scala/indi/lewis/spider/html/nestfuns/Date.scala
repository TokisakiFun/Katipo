package indi.lewis.spider.html.nestfuns

import java.text.SimpleDateFormat

import indi.lewis.spider.html.NestFunction

/**
  * Created by lewis on 2017/4/10.
  */
class Date  extends NestFunction{
  override def funcName: String = "date"

  val format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

  override def funcBody: () => String = {
    ()=>format.format(new java.util.Date)
  }
}
