package indi.lewis.spider.http

/**
  * Created by lewis on 2017/4/5.
  */
trait UserRequest {

  def url() : String  ;

  def parameters() : Map[String,String] ;

  def method:HttpMethod;
}
