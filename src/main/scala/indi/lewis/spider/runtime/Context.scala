package indi.lewis.spider.runtime

import java.util

/**
  * Created by lewis on 17-4-14.
  */
class Context private() {

  private val env=new util.HashMap[String,Any]()

  def get(key:String):Any=env.get(key)

  private def load(context:(String,Any)):Context={
    env.put(context._1,context._2);
    this
  }
}

object Context {
  def newConext(context:(String,Any)*):Context={
    val c=new Context();
    context.map(d => c.load(d._1,d._2));
    c
  }
}
