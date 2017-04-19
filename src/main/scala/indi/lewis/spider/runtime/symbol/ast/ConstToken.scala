package indi.lewis.spider.runtime.symbol.ast

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.Token
import indi.lewis.spider.runtime.symbol.Number
import indi.lewis.spider.runtime.fnlink.{Instructions, RuntimeHeap}

/**
  * Created by lewis on 17-4-14.
  */
class ConstToken(val value:SyntaxSymbol) extends Token{

  private val fval:SyntaxSymbol=
    if(value.isInstanceOf[ConstToken])
      value.asInstanceOf[ConstToken].fval
    else
      value;

  override def literalValue(): String = fval.literalValue()

  def realValue():Any={
    var value:Any=null;
    val valString=fval.literalValue();
    if(fval.isInstanceOf[Number]){
      if(valString.indexOf('.') > -1){
        value=java.lang.Double.parseDouble(valString);
      }else{
        value=java.lang.Integer.parseInt(valString);
      }
    }else{
      value=valString;
    }
    value
  }

  override def retType(): Class[_] = fval.retType()

  override def instruction (ins :Instructions): Instructions ={
    val real=realValue();
    ins.functionLink={ heap:RuntimeHeap =>real }
    ins
  }

}
