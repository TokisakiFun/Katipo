package indi.lewis.spider.http

import indi.lewis.spider.config.SocketConfig
import indi.lewis.spider.thread.Job
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpUriRequest}
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

/**
  * Created by lewis on 2017/4/5.
  */
class HttpRequest ( url: HttpUrl ) {

  private var useProxy:Boolean = false;
  private var proxyServer:String = _;
  private var proxyPort:Int = _;

  private val clientLocal=new ThreadLocal[CloseableHttpClient]

  private def client : CloseableHttpClient={
    val tn=Thread.currentThread().getId.toString;
    val c=clientLocal.get();
    if(c==null){
      val ot=if(HttpUrl.HTTPS.equals(url.scheme())) httsClient  else httpClient ;
      clientLocal.set(ot);
      ot
    }else c
  };

  def this ( url: HttpUrl,poxyServer:String,proxyPort:Int ) {
    this(url);
    this.useProxy=true;
    this.proxyServer=poxyServer;
    this.proxyPort=proxyPort;
  }

  private final def httpClient:CloseableHttpClient={
    var builder=HttpClients.custom();
    if(useProxy) builder=builder.setProxy(new HttpHost(proxyServer,proxyPort))

    builder.setDnsResolver(UserDnsResolver.get())
    builder.build();
  }

  private final def httsClient : CloseableHttpClient={
    val sslsf=SSLConnectionSocketFactory.getSocketFactory()
    var builder=HttpClients.custom().setSSLSocketFactory(sslsf);

    if(useProxy) builder=builder.setProxy(new HttpHost(proxyServer,proxyPort))

    builder.setDnsResolver(UserDnsResolver.get())
    builder.build();
  }

  private def simpleRequest( request : HttpUriRequest) : (Int,String) ={
    val response=client.execute(request);
    var result:String= null ;
    try{
      val entity = response.getEntity() ;

      if(entity!=null){
        val encoding=entity.getContentEncoding() ;
        val charset="UTF-8";
        result=EntityUtils.toString(entity,if(encoding==null) charset else encoding .getValue);
      }
      EntityUtils.consume(entity)
    }finally{
      response.close();
    }
    (response.getStatusLine.getStatusCode,result)
  }

  /**
    *
    * @param url
    * @return
    */
  private def doGet ( url :String ):(Int,String) = {
    val get=new HttpGet(url);
    get.setConfig(HttpRequest.requestConfig);

    simpleRequest(get)
  }

  private def doPost( url : String ,param:Map[String,String]):(Int,String) = {
    val post = new HttpPost( url );
    post.setConfig(HttpRequest.requestConfig);

    val params = new java.util.ArrayList[BasicNameValuePair];
    param.keySet.foreach(k => params.add(new BasicNameValuePair(k,param(k))));
    post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

    simpleRequest(post)
  }

  def  doRequest(runner:(Job[UserRequest],Int,String)=>Unit):Option[Job[UserRequest]]={
    url.next() match {
      case Some(http) =>
        Some (
          http.method match {
            case HttpMethod.SimpleHttpPost() => new Job[UserRequest] {
              override def run() ={
                val ret=doPost(http.url, http.parameters);
                runner(this,ret._1,ret._2)
              }
              override def attach(): UserRequest = http
            };
            case _  => new Job[UserRequest] {
              override def run() = {
                val ret=doGet(http.url);
                runner(this,ret._1,ret._2);
              }
              override def attach(): UserRequest = http
            };
          }
        )
      case None => { client.close();clientLocal.set(null); None }
    }
  }

}

object HttpRequest {
  private val requestConfig=RequestConfig.custom().setSocketTimeout(SocketConfig.SocketTimeout).setConnectTimeout(SocketConfig.ConnectTimeout).build();

  val STATE_CODE_OK=200;
}
