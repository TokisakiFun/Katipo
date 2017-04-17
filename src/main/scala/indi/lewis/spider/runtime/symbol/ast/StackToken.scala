package indi.lewis.spider.runtime.symbol.ast

import java.util

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.Token

/**
  * Created by lewis on 2017/4/17.
  */
class StackToken extends Token{
  private val stack=new util.ArrayList[SyntaxSymbol]();

  def push(s :SyntaxSymbol):Unit= if(s!=null) stack.add(s);

  def addCaller(preSymbol: SyntaxSymbol) = stack.add(0,preSymbol)

  override def literalValue(): String = {
    val builder=new StringBuilder();
    builder.append("(")
    val arr=stack.toArray.map( a => a.asInstanceOf[SyntaxSymbol].literalValue())
    for(i <- 0 to arr.length-1){
      builder.append(arr(i))
      if(i<arr.length-1) builder.append(",")
    }
    builder.append(")");
    builder.toString();
  }

  override def calc():(String,String)={
    val builder=new StringBuilder();
    val builder2=new StringBuilder();

    builder2.append("(")
    val arr=stack.toArray
    for(i <- 0 to arr.length-1){
      val ret=
        if(arr(i).isInstanceOf[Token])
        arr(i).asInstanceOf[Token].calc()
      else (null,arr(i).asInstanceOf[SyntaxSymbol].literalValue());
      if(ret._1!=null){
        builder.append(ret._1);
      }
      builder2.append(ret._2)
      if(i<arr.length-1){ builder2.append(","); }
    }
    builder2.append(")");

    (builder.toString(),builder2.toString())
  }
}
