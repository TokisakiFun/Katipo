package indi.lewis.spider.http

import indi.lewis.spider.thread.{HttpResultProcessor, Job}
import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.util.EntityUtils

/**
  * Created by lewis on 2017/4/11.
  */
class AsynCallback(attach :UserRequest,channel:HttpResultProcessor,val runner:HttpAsynRunner) extends FutureCallback[HttpResponse]{

  def getAttach():UserRequest=attach

  private var firstTimeTry=true;

  override def cancelled(): Unit = {
    channel.addJob(new AsynCallback.NestJob((attach,0,null),f =  ()=>throw new RuntimeException("task canceld")))
  }

  override def completed(response: HttpResponse): Unit = {
    lazy val nest  ={
        var result:String=null;
        var nest:(UserRequest,Int,String)=null;
          val entity = response.getEntity() ;
          if(entity!=null){
            val encoding=entity.getContentEncoding() ;
            val charset="UTF-8";
            result=EntityUtils.toString(entity,if(encoding==null) charset else encoding .getValue);
          }
          EntityUtils.consume(entity)

        (attach,response.getStatusLine.getStatusCode,result);
      }
    channel.addJob(new AsynCallback.NestJob(nest,()=>{ }))
  }

  override def failed(ex: Exception): Unit = {
    if(runner.getRetryBeforeThrowSocketTimeout() && firstTimeTry){
      runner.addRetryJob(this);
      this.firstTimeTry=false;
    }else{
      channel.addJob(new AsynCallback.NestJob((attach,0,null),()=>throw ex))
    }
  }
}

object AsynCallback {

  class NestJob(d : =>(UserRequest,Int,String),f:()=>Unit) extends Job[(UserRequest,Int,String)]{

    override def attach(): (UserRequest, Int, String) = d

    override def run(): Unit =  f()
  }
}
