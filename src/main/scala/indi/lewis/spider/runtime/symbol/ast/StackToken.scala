package indi.lewis.spider.runtime.symbol.ast

import java.util

import indi.lewis.spider.runtime.{ClassMeta, SyntaxSymbol}
import indi.lewis.spider.runtime.symbol.Token
import indi.lewis.spider.runtime.fnlink.{Instructions, OperationCode, RuntimeHeap}

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

  override def instruction (ins:Instructions): Instructions = throw new Exception("should not use this method ! use method runtimeRet instead!");

  def runtimeRet(ins:Instructions):RuntimeHeap =>Array[Any] ={
    val size=stack.size();
    val farr=new Array[RuntimeHeap =>Any](size);
    for(i <- 0 to size-1){
      val t=stack.get(i);
      if(t.isInstanceOf[Token]){
        t.asInstanceOf[Token].instruction(ins);
      }else{
        new ConstToken(t.asInstanceOf[SyntaxSymbol]).instruction(ins);
      }
      farr(i)=ins.functionLink;
    }

    { heap:RuntimeHeap =>
      val arr=new Array[Any](size);
      for(i <- 0 to size-1) arr(i)=farr(i)(heap)
      arr
    }
  }

  def clzArray(): Array[ClassMeta[Any]] = {
    val arr=new Array[ClassMeta[Any]](stack.size());
    for( i <- 0 to stack.size()-1) arr(i)=new ClassMeta(stack.get(i).retType()) ;
    arr
  }

  override def retType(): Class[_] = classOf[Array[Any]]

  def stackSize():Int=stack.size()
}
