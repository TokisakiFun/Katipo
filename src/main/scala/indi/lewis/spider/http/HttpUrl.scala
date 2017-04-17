package indi.lewis.spider.http

/**
  * Created by lewis on 2017/3/31.
  */
trait HttpUrl {

  def scheme() : String ;

  def next() : Option[UserRequest] ;
}

object HttpUrl {

  final val HTTPS = "https";

  final val HTTP = "http";

}
