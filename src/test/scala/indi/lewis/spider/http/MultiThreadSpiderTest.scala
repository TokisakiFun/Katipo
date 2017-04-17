package indi.lewis.spider.http

import indi.lewis.spider.html.ModelBuiler
import indi.lewis.spider.simple.{SimpleChannel, SimpleHttpGet}
import indi.lewis.spider.thread.{Job, Runner}

/**
  * Created by lewis on 2017/4/11.
  */
object MultiThreadSpiderTest {

  def main(args: Array[String]): Unit = {
    var aid = new java.util.concurrent.atomic.AtomicInteger(0);
    val f = {()=>
      aid.incrementAndGet() match {
        case a if(a < 200) => Some("http://www.acfun.cn/v/list110/index_"+a+".htm")
        case _ => None
      }
    }
    val model=ModelBuiler.compile("{ \n" +
      "\"comments\":\"${.item .a%text[]}\"," +
      "\"title\":\"${.item .title%text[]}\"," +
      "\"date\":\"${date}\"\n"+"}");
    val success = (r:Job[UserRequest], code:Int,ret:String)=>{
      println("success---------"+r.attach().url())
      //  println(Thread.currentThread().getName+":"+r.attach().url()+"===="+model.compile(ret).toString)
//      try{
//        val arr=model.compile(ret).getAsJsonObject.getAsJsonArray("title");
//        for(i <- 0 to arr.size()-1;a=arr.get(i)) println(a);
//      }catch {
//        case ex => ex.printStackTrace();
//      }

    };
    val failure = ( r:Job[UserRequest],e:Exception)=>{
      println("err---------"+r.attach().url())
      //    e.printStackTrace()
    };

    val start=System.currentTimeMillis();
    Runner.getJobRunner[UserRequest](new SimpleChannel(new HttpRequest(new SimpleHttpGet(f)),success,failure)).startRun()
    println(System.currentTimeMillis()-start)
  }
}
