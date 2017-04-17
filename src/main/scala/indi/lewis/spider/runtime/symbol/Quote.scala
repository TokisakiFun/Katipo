package indi.lewis.spider.runtime.symbol

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.ast.ConstToken;

/**
  * Created by lewis on 2017/4/12.
  */
class Quote () extends SyntaxSymbol{
  private val value=new java.lang.StringBuffer()
  var preChar:Char = 0;
  var startChar:Char=0;
  var endChar:Char=0;

  override def testAdd(c: Char): Boolean = {
    preChar=c;
    (startChar,endChar) match {
      case (0,0) => {
        if(c=='\''|| c=='"'){ startChar=c; true; }
        else false;
      }
      case (_,0) if(startChar>0) => {
        if(c!=startChar) value.append(c);
        else if(c==startChar&&preChar=='\\') value.append(c);
        else if(c==startChar) endChar=c;
        true;
      }
      case _ => false;
    }
  }

  override def literalValue(): String = {
    value.toString
  }

  private def allowSymbol(s:SyntaxSymbol):Boolean= s==null||s.isInstanceOf[Operator] ||s.isInstanceOf[Blank]

  override def checkGrammar(): Unit = {
    if(!allowSymbol(nextSymbol)) throw new RuntimeException( "String "+literalValue()+" can not be followed by "+nextSymbol.literalValue());
    if(nextSymbol!=null) nextSymbol.checkGrammar()
  }

  override def ast(): Token = new ConstToken(this)
}
