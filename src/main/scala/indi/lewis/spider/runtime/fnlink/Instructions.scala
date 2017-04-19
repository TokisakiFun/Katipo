package indi.lewis.spider.runtime.fnlink

import java.util

import indi.lewis.spider.runtime.fnlink.define.{FunctionDef, ObjectDef}
import indi.lewis.spider.runtime.symbol.Token

/**
  * Created by lewis on 17-4-17.
  */
class Instructions {




  private val instructions=new util.ArrayList[RuntimeHeap => Any]();

  private val heapTypes=new util.ArrayList[ObjectDef]();

  private val heapMap=new util.HashMap[String,Int]();

  private val methods=new util.ArrayList[FunctionDef[_]]();

  private val methodMap=new util.HashMap[String,Int]();

  private[runtime] var functionLink: RuntimeHeap => Any =null;


  def pushFunction(ast: Token) = {
    ast.instruction(this)
    instructions.add(functionLink);
  }

  def runtime(rs:(String,Any)*):RuntimeHeap = {
    val fs=new Array[RuntimeHeap => Any](instructions.size());
    for(i <- 0 to fs.length -1) fs(i)=instructions.get(i);
    val runtime=new RuntimeHeap(heapTypes.size(),fs);
    for(r <- rs){
      val index=heapMap.get(r._1);
      if(index>=0) runtime.define(index,r._2);
    }
    runtime.setMap(heapMap);
    runtime
  }

  def getMethodIndex(objName:String):Int={
    if(methodMap.containsKey(objName)) methodMap.get(objName);
    else -1
  }

  def getMethodDef(index:Int):FunctionDef[_]={
    if(index<0||index>=methods.size()) null
    else methods.get(index)
  }

  def registerMethod(fn:FunctionDef[_]):Int={
    methods.add(fn);
    methodMap.put(fn.fnName,methods.size()-1);
    methods.size()-1
  }

  def getObjIndex(objName:String):Int={
   if(heapMap.containsKey(objName))heapMap.get(objName);
   else -1
  }

  def getObjDef(index:Int):ObjectDef={
    if(index<0||index>=heapTypes.size()) null
    else heapTypes.get(index)
  }


  def registerObject(ob:ObjectDef):Int={
    heapTypes.add(ob);
    heapMap.put(ob.obName,heapTypes.size()-1);
    heapTypes.size()-1
  }

}
