package indi.lewis.spider.runtime.symbol.ast

import indi.lewis.spider.runtime.symbol.Token
import indi.lewis.spider.runtime.fnlink.{Instructions, OperationCode, RuntimeHeap}

/**
  * Created by lewis on 2017/4/16.
  */
class FunctionToken extends Token{

  var function:Token=null;

  var stack:StackToken=null;

  private var ins :Instructions = null;


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

  override def instruction (ins :Instructions): Instructions ={
    this.ins=ins;
    val sf=stack.runtimeRet(ins);

    val fn=ins.getMethodDef(ins.getMethodIndex(function.literalValue()));
    val array=stack.clzArray();
    if(!fn.compatible(stack.clzArray())){
      val pm=new StringBuilder("(");
      for(i <- 0 to array.length -1){
        if(i>0) pm.append(',');
        pm.append(array(i).clz)
      }
      pm.append(")");
      throw new RuntimeException("funcion "+function.literalValue()+" is not compatible for "+pm.toString()+" ! ");
    }

    ins.functionLink={  heap : RuntimeHeap=> fn.fnBody(sf(heap)) }
    ins
  }

  override def retType(): Class[_] = {
    val md=ins.getMethodDef(ins.getMethodIndex(function.literalValue()));
    if(md==null) throw new RuntimeException("found not defined method "+literalValue() +" ! ");
    md.clzRet
  }

}
