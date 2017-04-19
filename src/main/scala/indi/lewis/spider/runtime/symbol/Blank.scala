package indi.lewis.spider.runtime.symbol

import indi.lewis.spider.runtime.SyntaxSymbol

/**
  * Created by lewis on 17-4-14.
  */
class Blank() extends SyntaxSymbol  {

  private val value=new java.lang.StringBuffer();
  private var lastChar=0;

  override def testAdd(c: Char): Boolean = {
    if(c <= ' '){
      if(c!=lastChar){
        value.append(c);
      }
      lastChar=c;
      true;
    }else false;
  }
  override def checkGrammar(): Unit = {
    if(nextSymbol!=null) nextSymbol.checkGrammar()
  }

  override def literalValue(): String = value.toString

  override def ast(): Token = null

  override def retType(): Class[_] = null
}
