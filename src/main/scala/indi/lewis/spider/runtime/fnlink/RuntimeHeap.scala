package indi.lewis.spider.runtime.fnlink

import java.util

/**
  * Created by lewis on 17-4-19.
  */
class RuntimeHeap private[fnlink] (val heapSize:Int,val fnLink:Array[RuntimeHeap=>Any]){

  private val heapMap=new util.HashMap[String,Int]();

  private val heap=new Array[Any](heapSize);

  private[runtime] def setMap(heapMap: util.HashMap[String, Int]) = this.heapMap.putAll(heapMap)


  /**
    * 获得指定位置的值
    *
    * @param li
    * @return
    */
  private[runtime] def getValue(li: Int): Any = heap(li)

  /**
    * 设定指定位置的值
    *
    * @param li
    * @param value
    * @return
    */
  private[runtime] def setValue(li: Int, value: Any): Any = heap(li)=value

  private[fnlink] def define(li: Int, value: Any): Any = setValue(li,value)

  def run():Any= {
    var ret:Any=null;
    for( f <- fnLink )
      ret=f(this);
    ret
  }

  def getValue(name:String):Any=
    if(heapMap.containsKey(name))
      getValue(heapMap.get(name))
    else
      null;

}
