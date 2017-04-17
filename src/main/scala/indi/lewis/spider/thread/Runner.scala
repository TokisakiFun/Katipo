package indi.lewis.spider.thread

import java.util.concurrent.atomic.AtomicBoolean

/**
  * Created by lewis on 2017/4/6.
  */
object Runner {

  trait JobChannel[A] {

    def nextJob() : Option[Job[A]];

    /**
      * 当一条任务没有触发异常完成时
      * @param job
      */
    def onJobFinish(job :Job[A]) : Unit;

    /**
      * 当一条任务结束时（无论是否为正常结束）
      * @param job
      */
    def onJobOver(job :Job[A]) : Unit;


    /**
      * 当所有任务执行结束时
      */
    def onAllJobOver() : Unit ;

    /**
      * 当任务执行触发异常时
      * @param job
      * @param e
      */
    def onException(job :Job[A], e : Exception) :Unit;
  }

  def getJobRunner[B](cores :Int,channel :Runner.JobChannel[B]) :Runner[B] = new Runner[B](cores,channel);
  def getJobRunner[B](channel :Runner.JobChannel[B]) :Runner[B] = getJobRunner(cpuCores*16,channel);

  final lazy val cpuCores:Int = Runtime.getRuntime.availableProcessors()
}

class Runner[C] private (cores :Int,channel : Runner.JobChannel[C]) {

  val ts=new Array[Thread](cores);

  private val started:java.util.concurrent.atomic.AtomicBoolean=new AtomicBoolean(false);

  val endCount=new java.util.concurrent.CountDownLatch(cores);
  val starter=new java.util.concurrent.CountDownLatch(1);

  def startRun() : Unit= {
    startRunAndRet().await()
  }

  def startRunAndRet():java.util.concurrent.CountDownLatch={
    synchronized {
      if (started.get() ) throw new RuntimeException(" Runner has been started ! ");
      started.set(true);
    }

    for( i <- 1 to cores){
      val th=new Thread(){
        override def run(): Unit = {
          starter.await();
          while(channel.nextJob().map( j =>{
            try{
              j.run();
              channel.onJobFinish(j);
            }catch {
              case ex :Exception =>channel.onException(j,ex)
            }
            channel.onJobOver(j)
          })!=None) {}
          endCount.countDown();
        }
      }

      th.setName(s"JobThread( $i /$cores )");
      th.setPriority(Thread.MAX_PRIORITY);
      ts(i-1)=th;
      th.start()
    }
    starter.countDown();

    endCount
  }

}
