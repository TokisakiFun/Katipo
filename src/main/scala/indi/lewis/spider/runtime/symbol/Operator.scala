package indi.lewis.spider.runtime.symbol

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.operators.OpImpl

import scala.util.control.Breaks;

/**
  * Created by lewis on 2017/4/12.
  */
class Operator() extends SyntaxSymbol{
  var operator:StringBuilder=new StringBuilder;

  override def testAdd(c: Char): Boolean = {
      if(OpImpl.isOperator(operator.toString()+c)){
        operator.append(c)
        true;
      } else false;
  }

  lazy val realValue:OpImpl =OpImpl.get(operator.toString());

  override def literalValue(): String = operator.toString()

  override def checkGrammar(): Unit = {
    if(nextSymbol!=null) nextSymbol.checkGrammar()
  }

  override def ast(): Token = {
    val self=realValue;
    var next=this.nextSymbol;
    while(next!=null&&(!next.isInstanceOf[Operator])) next=next.nextSymbol;
    if(next!=null){
      val index=OpImpl.getIndex(self.getClass);
      if(index>OpImpl.getIndex(next.asInstanceOf[Operator].realValue.getClass)){
        next.ast();
        this.ast();
      }else self.ast(this)
    }else self.ast(this)

  }
}
