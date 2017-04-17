package indi.lewis.spider.http

/**
  * Created by lewis on 2017/4/5.
  */
trait HttpMethod {

}

object HttpMethod {

  case class SimpleHttpGet( ) extends HttpMethod;
  case class SimpleHttpPost( ) extends HttpMethod;

}
