package indi.lewis.spider.runtime.symbol

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.ast.{ConstToken, FunctionToken, TempToken}
import indi.lewis.spider.runtime.vm.OperationCode;

/**
  * Created by lewis on 2017/4/12.
  */
class Token() extends SyntaxSymbol {

  private val value=new java.lang.StringBuffer()

  override def testAdd(c: Char): Boolean = {
    if(value.length()==0){
      if(c=='$'||c=='_'||(c>='a'&&c<='z')||(c>='A'&&c<='Z')){
        value.append(c)
        true;
      }else false;
    }else {
      if(c=='$'||c=='_'||(c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9')){
        value.append(c)
        true;
      }else false;
    }
  }

  private def allowSymbol(s:SyntaxSymbol):Boolean= s==null||s.isInstanceOf[Operator] ||s.isInstanceOf[Blank]

  override def checkGrammar(): Unit = {
    if(!allowSymbol(nextSymbol)) throw new RuntimeException( "Token "+literalValue()+" can not be followed by "+nextSymbol.literalValue());
    if(nextSymbol!=null) nextSymbol.checkGrammar()
  }

  override def literalValue(): String = value.toString

  def calc():(String,String)=(null,literalValue())

  override def ast(): Token = {
    new ConstToken(this)
  }
}

object Token {
  protected [runtime] val valIndex=new java.util.concurrent.atomic.AtomicInteger(0);
}