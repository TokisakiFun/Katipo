package indi.lewis.spider.http

import indi.lewis.spider.simple.SimpleHttpGet
import indi.lewis.spider.thread.{HttpResultProcessor, Job}

/**
  * Created by lewis on 2017/4/11.
  */
object AsynRequestTest {

  def main(args: Array[String]): Unit = {
    val aid = new java.util.concurrent.atomic.AtomicInteger(0);
    val f = {()=>

      aid.incrementAndGet() match {
        case a if(a < 20000) => Some("http://127.0.0.1:9001/")
        case _ => None
      }
    }
    val url=new SimpleHttpGet(f)
    val proc=new HttpResultProcessor {
      override def onJobFinish(job: Job[(UserRequest, Int, String)]) = {
    //     println("success---------"+job.attach()._1.url())
        Thread.sleep(2)
      }
      override def onException(job: Job[(UserRequest, Int, String)], e: Exception) = {
        println("err-------------"+job.attach()._1.url())
     //   e.printStackTrace()
      }
    };
    val start=System.currentTimeMillis();
    HttpAsynRunner.create(proc,url).startRun()
    println(System.currentTimeMillis()-start)
  }
}
