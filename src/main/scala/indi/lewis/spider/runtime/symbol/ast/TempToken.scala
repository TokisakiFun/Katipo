package indi.lewis.spider.runtime.symbol.ast

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.{ Operator, Token}

/**
  * Created by lewis on 17-4-14.
  */
class TempToken() extends Token{

  def this( l:SyntaxSymbol,op:Int,r:SyntaxSymbol){
    this();
    this.left=l;
    this.operate=op;
    this.right=r;
  }

  var left:SyntaxSymbol=null;
  var right:SyntaxSymbol=null;

  var operate:Int = -1 ;
  var operateCode:String =_ ;

  override def calc():(String,String)={
    val builder=new StringBuilder();
    val builder2=new StringBuilder();

    val $var="$val"+Token.valIndex.incrementAndGet();
    val c1=new StringBuilder();
    if(left!=null) {
      val rl= if(left.isInstanceOf[Token]) left.asInstanceOf[Token].calc() else (null,left.literalValue())
      if(rl._1!=null) builder.append(rl._1);
      c1.append(rl._2)
    }
    c1.append(operateCode)
    if(right!=null){
      val rl= if(right.isInstanceOf[Token]) right.asInstanceOf[Token].calc() else (null,right.literalValue())
      if(rl._1!=null) builder.append(rl._1);
      c1.append(rl._2)
    }
    builder.append("val ").append($var).append(" = ").append(c1.toString()).append("\n");
    (builder.toString(),$var)
  }

}
