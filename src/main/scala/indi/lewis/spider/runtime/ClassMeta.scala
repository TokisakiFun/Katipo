package indi.lewis.spider.runtime

/**
  * Created by lewis on 17-4-13.
  */
class ClassMeta [+A] (val clz:Class[_ <:A]){


  override def equals(obj: scala.Any): Boolean = {
    if(obj==null) false;
    else if (obj.isInstanceOf[ClassMeta[A]]){
      val t=obj.asInstanceOf[ClassMeta[A]];
      this.clz.equals(t.clz)
    }else false;
  }


  def getNew():A=clz.newInstance()
}
