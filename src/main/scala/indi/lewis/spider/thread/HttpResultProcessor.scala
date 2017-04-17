package indi.lewis.spider.thread

import java.util

import indi.lewis.spider.http.UserRequest

/**
  * Created by lewis on 2017/4/11.
  */
abstract class HttpResultProcessor  extends Runner.JobChannel[(UserRequest,Int,String)]{

  val jobQueue=new java.util.concurrent.ConcurrentLinkedDeque[Option[Job[(UserRequest,Int,String)]]]();

  private val responseCount=new java.util.concurrent.atomic.AtomicLong(0);

  def  addJob ( job : Job[(UserRequest,Int,String)] ):Unit={
    responseCount.incrementAndGet();
    jobQueue.addLast(Some(job));
  }

  def  endJob ():Unit= jobQueue.add(None)
  def  jobQueueSize ():Long= jobQueue.size()

  def receiveCount:Long = responseCount.get()

  override def nextJob(): Option[Job[(UserRequest,Int,String)]] = {
    synchronized {
      var size:Int =jobQueue.size() ;
      while(size == 0){
        wait(10);
        size =jobQueue.size() ;
      }
      val ret=jobQueue.element()
      ret match {
        case Some(a) => jobQueue.remove()
        case None => {}
      }
      ret
    }

  }

  /**
    * 当一条任务没有触发异常完成时
    *
    * @param job
    */
  override def onJobFinish(job: Job[(UserRequest,Int,String)]): Unit ;

  /**
    * 当一条任务结束时（无论是否为正常结束）
    *
    * @param job
    */
final override def onJobOver(job: Job[(UserRequest,Int,String)]): Unit={} ;

  /**
    * 当所有任务执行结束时
    */
final override def onAllJobOver(): Unit ={};

  /**
    * 当任务执行触发异常时
    *
    * @param job
    * @param e
    */
  override def onException(job: Job[(UserRequest,Int,String)], e: Exception): Unit ;
}
