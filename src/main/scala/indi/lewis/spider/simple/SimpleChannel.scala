package indi.lewis.spider.simple

import indi.lewis.spider.http.{HttpRequest, UserRequest}
import indi.lewis.spider.thread.{Job, Runner}

/**
  * Created by lewis on 2017/4/6.
  */
class SimpleChannel( request : HttpRequest , success : (Job[UserRequest],Int,String)=>Unit,failure: (Job[UserRequest],Exception)=>Unit ) extends Runner.JobChannel[UserRequest] {

  override def nextJob(): Option[Job[UserRequest]] = {
    request.doRequest(success)
  }

  /**
    * 当一条任务没有触发异常完成时
    *
    * @param job
    */
  override def onJobFinish(job: Job[UserRequest]): Unit = {}

  /**
    * 当一条任务结束时（无论是否为正常结束）
    *
    * @param job
    */
override def onJobOver(job: Job[UserRequest]): Unit = {}

  /**
    * 当所有任务执行结束时
    */
override def onAllJobOver(): Unit = {}

  /**
    * 当任务执行触发异常时
    *
    * @param job
    * @param e
    */
  override def onException(job: Job[UserRequest], e: Exception): Unit = failure(job,e)
}
