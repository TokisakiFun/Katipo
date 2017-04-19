package indi.lewis.spider.runtime.fnlink.define

import indi.lewis.spider.runtime.ClassMeta

/**
  * 注册函数
  * Created by lewis on 17-4-19.
  */
class FunctionDef[A](val fnName:String,val pType:Array[Class[_]],val clzRet: Class[A],val fnBody: Array[Any]=>A) {



  def compatible(clz : Array[Class[_]]) : Boolean={
    if((clz==null||clz.length==0)&&(pType==null || pType.length==0))
      return true
    else if(clz.length!=pType.length)
      return false
    else{
      for(i <- 0 to pType.length-1){
        if( ! pType(i).isAssignableFrom(clz(i)))
          return false;
      }
      return true
    }
  }

  def compatible(clz : Array[ClassMeta[_]]) : Boolean={
    if((clz==null||clz.length==0)&&(pType==null || pType.length==0))
      return true
    else if(clz.length!=pType.length)
      return false
    else{
      for(i <- 0 to pType.length-1){
        //基本类型间可以互相转换
        if(!(
              ( pType(i).isPrimitive && clz(i).clz.isPrimitive) ||
                pType(i).isAssignableFrom(clz(i).clz)
          ))
          return false;
      }
      return true
    }
  }


}
