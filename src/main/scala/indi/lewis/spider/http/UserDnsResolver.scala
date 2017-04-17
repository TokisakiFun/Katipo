package indi.lewis.spider.http

import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap


/**
  * Created by lewis on 2017/4/5.
  */
class UserDnsResolver private () extends org.apache.http.conn.DnsResolver{
  /**
    * In-memory collection that will hold the associations between a host name
    * and an array of InetAddress instances.
    */
  private val dnsMap  = new ConcurrentHashMap[String, Array[InetAddress]]


  override def resolve(host: String): Array[InetAddress] = {
    var ret=dnsMap.get(host)
    if(ret == null){
      synchronized {
        ret= dnsMap.get(host)
        if(ret==null){
          ret=InetAddress.getAllByName(host)
          if(ret !=null && ret.length>0){
            dnsMap.put(host,ret)
          }
        }
      }
    }
    ret
  }
}

object UserDnsResolver{

  private val userDnsResolver=new UserDnsResolver;

  def get():UserDnsResolver=userDnsResolver
}