package indi.lewis.spider.runtime.symbol

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.ast.ConstToken;

/**
  * Created by lewis on 2017/4/12.
  */
class Number() extends SyntaxSymbol{
  private val value=new java.lang.StringBuffer()

  override def testAdd(c: Char): Boolean = {
    if(c>='0'&&c<='9'){
      value.append(c);
      return true;
    }
    else if(c=='.'&&value.length()>0){
      for(i <- 0 to value.length()-1){
        if(value.charAt(i)=='.') return false;
      }
      value.append(c);
      return true
    }
    else false;
  }

  private def allowSymbol(s:SyntaxSymbol):Boolean= s==null||s.isInstanceOf[Operator] ||s.isInstanceOf[Blank]

  override def checkGrammar(): Unit = {
    if(!allowSymbol(nextSymbol)) throw new RuntimeException( "Number "+literalValue()+" can not be followed by "+nextSymbol.literalValue());
    if(nextSymbol!=null) nextSymbol.checkGrammar()
  }

  override def literalValue(): String = value.toString

  override def ast(): Token = new ConstToken(this);
}
