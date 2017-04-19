package indi.lewis.spider.runtime.fnlink.define

/**
  * Created by lewis on 17-4-19.
  */
class ObjectDef(val obName:String,val obType:Class[_]) {

  def compatible(clz : Class[_] ) : Boolean= obType.isAssignableFrom(clz)

}
