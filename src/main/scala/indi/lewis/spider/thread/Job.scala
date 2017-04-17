package indi.lewis.spider.thread

/**
  * Created by lewis on 2017/4/6.
  */
abstract class Job[+A] extends Runnable{
  def attach() :A ;
}
