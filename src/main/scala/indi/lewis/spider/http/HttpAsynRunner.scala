package indi.lewis.spider.http

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.SSLContext

import indi.lewis.spider.config.SocketConfig
import indi.lewis.spider.thread.{HttpResultProcessor, Job, Runner}
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.concurrent.FutureCallback
import org.apache.http.config.{ConnectionConfig, RegistryBuilder}
import org.apache.http.conn.ssl.DefaultHostnameVerifier
import org.apache.http.conn.util.PublicSuffixMatcherLoader
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager
import org.apache.http.impl.nio.reactor.{DefaultConnectingIOReactor, IOReactorConfig}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.nio.conn.{NHttpClientConnectionManager, NoopIOSessionStrategy, SchemeIOSessionStrategy}
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy
import org.apache.http.ssl.SSLContexts

import scala.util.control.Breaks

/**
  * Created by lewis on 2017/4/11.
  */
class HttpAsynRunner private(resultProcessor:HttpResultProcessor, url: HttpUrl, cores:Int,val retryBeforeThrowSocketTimeout:Boolean){

  private val callbackRunner =Runner.getJobRunner(cores,resultProcessor);

  private val requestCount=new java.util.concurrent.atomic.AtomicLong(0);

  private var started=false;

  private val client:CloseableHttpAsyncClient={
    val supportedProtocols =Array("SSL","TLS")
    val supportedCipherSuites =null
    var hostnameVerifier = new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault)
    var sslStrategy = new SSLIOSessionStrategy( SSLContexts.createSystemDefault, supportedProtocols, supportedCipherSuites, hostnameVerifier)
    val config=IOReactorConfig.custom()
      .setSelectInterval(1l)
      .setShutdownGracePeriod(500)
      .setInterestOpQueued(false)
      .setIoThreadCount(cores)
      .setConnectTimeout(SocketConfig.ConnectTimeout)
      .setSoTimeout(SocketConfig.SocketTimeout)
      .setSoReuseAddress(true)
      .setSoKeepAlive(true)
      .setSoLinger(-1)
      .setTcpNoDelay(true)
      .build();
    val ioReactor=new DefaultConnectingIOReactor(config,new HttpAsynRunner.DefaultThreadFactory());
    val poolingmgr = new PoolingNHttpClientConnectionManager(ioReactor, RegistryBuilder.create[SchemeIOSessionStrategy].register("http", NoopIOSessionStrategy.INSTANCE).register("https", sslStrategy).build)
    poolingmgr.setDefaultConnectionConfig(ConnectionConfig.DEFAULT)
    val max = 128
    poolingmgr.setDefaultMaxPerRoute(max)
    poolingmgr.setMaxTotal(Int.MaxValue)

    val httpAsyncClient = HttpAsyncClients.custom().setConnectionManager(poolingmgr).build();

    httpAsyncClient.start();

    httpAsyncClient
  }

  final def getRetryBeforeThrowSocketTimeout():Boolean=retryBeforeThrowSocketTimeout;

  private def doGet(url :String ,callback: FutureCallback[HttpResponse]):Unit = {
    val get=new HttpGet(url);
    get.setConfig(HttpAsynRunner.requestConfig);
    client.execute(get,callback);
  }

  private def doPost(url : String ,param:Map[String,String] ,callback: FutureCallback[HttpResponse]):Unit = {
    val post = new HttpPost( url );
    post.setConfig(HttpAsynRunner.requestConfig);

    val params = new java.util.ArrayList[BasicNameValuePair];
    param.keySet.foreach(k => params.add(new BasicNameValuePair(k,param(k))));
    post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

    client.execute(post,callback);
  }


  private val retryList=new java.util.concurrent.ConcurrentLinkedDeque[AsynCallback]();

  def addRetryJob(job:AsynCallback):Unit=retryList.add(job) ;

  private val doRequestFun={(http:UserRequest,callBack:AsynCallback) =>
    http.method match {
      case HttpMethod.SimpleHttpPost() =>   doPost(http.url(), http.parameters(), callBack)
      case HttpMethod.SimpleHttpGet() =>   doGet(http.url(),callBack)
      case _ => throw new RuntimeException("unknown http method "+http.method)
    }
  }

  def  startRun():Unit= {
    if(started) throw new RuntimeException("task can not restart !");
    started=true;
    val waiter=callbackRunner.startRunAndRet();


    Breaks.breakable {
      var pushLoop=0;

      while (true) {
        if(pushLoop>1024){
          var loop=true;
          do{
            val sleep1=resultProcessor.jobQueueSize()>>12;
            val sleep2=(requestCount.get()-resultProcessor.receiveCount)>>12;
            loop=sleep1>0||sleep2>0;
            if(loop){
              val sleep=2 * (if(sleep1>sleep2) sleep1 else sleep2)
              Thread.sleep(sleep);
            }
          }while(loop);
          pushLoop=0;
        }
        pushLoop+=1;
        if(retryBeforeThrowSocketTimeout){
          while(retryList.size()>0){
            val cb=retryList.pop();
            doRequestFun(cb.getAttach(),cb);
          }
        }
        url.next() match {
          case Some(http) =>{
            doRequestFun(http,new AsynCallback(http,resultProcessor,HttpAsynRunner.this))
            requestCount.incrementAndGet();
          }
          case None => {
            waitForClose();resultProcessor.endJob(); Breaks.break();
          }
        }
      }
    }
    waiter.await()
  }

  def waitForClose(): Unit ={
    while(requestCount.get()>resultProcessor.receiveCount){
      if(retryBeforeThrowSocketTimeout){
        while(retryList.size()>0){
          val cb=retryList.pop();
          doRequestFun(cb.getAttach(),cb);
        }
      }
      Thread.sleep(100);
    }
    client.close();
  }
}

object HttpAsynRunner {
  private val requestConfig=RequestConfig.custom().setSocketTimeout(SocketConfig.SocketTimeout).setConnectTimeout(SocketConfig.ConnectTimeout).build();

  def create(resultProcessor:HttpResultProcessor, url: HttpUrl):HttpAsynRunner=create(resultProcessor,url,Runtime.getRuntime.availableProcessors()>>1);
  def create(resultProcessor:HttpResultProcessor, url: HttpUrl,cores:Int):HttpAsynRunner=create(resultProcessor,url,cores,true);
  def create(resultProcessor:HttpResultProcessor, url: HttpUrl,cores:Int,retryBeforeThrowSocketTimeout:Boolean):HttpAsynRunner=new HttpAsynRunner(resultProcessor,url,cores,retryBeforeThrowSocketTimeout);

  class DefaultThreadFactory extends ThreadFactory {
    private val COUNT: AtomicLong = new AtomicLong(1)

    override def newThread(r: Runnable) = new Thread(r, "I/O dispatcher " + COUNT.getAndIncrement)
  }
}
