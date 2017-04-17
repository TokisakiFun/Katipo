package indi.lewis.spider.simple

import indi.lewis.spider.http.{HttpMethod, HttpUrl}

/**
  * Created by lewis on 2017/4/11.
  */
class SimpleHttpsGet(urlGenerator : ()=>Option[String] ) extends HttpUrl {

  import indi.lewis.spider.http.UserRequest

  private class SimpleUserRequest(str : String) extends UserRequest{

    override def parameters(): Map[String,String] = null

    override def url():String = str

    override def method: HttpMethod = HttpMethod.SimpleHttpGet()
  }

  override def scheme(): String = HttpUrl.HTTPS

  override def next(): Option[UserRequest] = {
    urlGenerator() match {
      case Some(url) => Some(new SimpleUserRequest(url));
      case None => None ;
    }
  }
}
