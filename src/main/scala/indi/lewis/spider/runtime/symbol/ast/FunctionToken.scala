package indi.lewis.spider.runtime.symbol.ast

import java.util

import indi.lewis.spider.runtime.symbol.Token

/**
  * Created by lewis on 2017/4/16.
  */
class FunctionToken extends Token{

  var function:Token=null;

  var stack:StackToken=null;


  override def literalValue(): String = {
    val builder=new StringBuilder();
    if(function!=null) builder.append(function.literalValue())
    builder.append(if(stack!=null) stack.literalValue() else "" );
    builder.toString();
  }

  override def calc():(String,String)={
    val builder=new StringBuilder();
    var callStack:String=null;
    val funRet="funRet"+Token.valIndex.incrementAndGet();
    if(stack!=null){
      val ret=stack.calc();
      builder.append(ret._1);
      builder.append("val "+funRet+" = "+function.literalValue() + ret._2).append("\n")
    }else{
      builder.append("val "+funRet+" = "+function.literalValue() + "()").append("\n")
    }
    (builder.toString(),funRet)
  }

}
