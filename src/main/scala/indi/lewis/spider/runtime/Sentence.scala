package indi.lewis.spider.runtime

import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.symbol.ast.ConstToken
import indi.lewis.spider.runtime.fnlink.Instructions

/**
  * Created by lewis on 2017/4/12.
  */
class Sentence(val first:SyntaxSymbol ) {

  var ff=first;

  def ast():Token={
    if(first!=null&&first.nextSymbol==null){
      new ConstToken(first)
    }else if(first!=null){
      var op=first;
      while(op.nextSymbol!=null){
        if(op.isInstanceOf[Operator] ){
          op=op.asInstanceOf[Operator].ast()
        }else if(op.nextSymbol.isInstanceOf[Operator]){
          op=op.nextSymbol.asInstanceOf[Operator].ast()
        }else throw new RuntimeException("can not find a operator!");
      }
      if(op.preSymbol!=null) throw new RuntimeException("cant not resolve the first token "+op.preSymbol.literalValue()+"!");
      op.asInstanceOf[Token]
    } else null
  }



  def print():Unit={
    var iterator=ff;
    val builder=new StringBuilder();
    while(iterator!=null){
      builder.append(" "+iterator.literalValue()+":"+iterator.getClass.getSimpleName+" ");
      iterator=iterator.nextSymbol;
    }
    println(builder.toString())
  }
}

object Sentence {
  def apply(first: SyntaxSymbol): Sentence
  = new Sentence(first)

}