package indi.lewis.spider.runtime.symbol

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.fnlink.{Instructions, RuntimeHeap};

/**
  * Created by lewis on 2017/4/12.
  */
class Token() extends SyntaxSymbol {

  private val value=new java.lang.StringBuffer()
  private var ins :Instructions=null;

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

  def instruction( ins :Instructions ):Instructions= {
    this.ins=ins;
    val li=ins.getObjIndex(literalValue());
    ins.functionLink={ heap:RuntimeHeap =>
      heap.getValue(li);
    }
    ins;
  }

  override def ast(): Token =  this

  override def retType(): Class[_] = {
    val od=ins.getObjDef(ins.getObjIndex(literalValue()));
    if(od==null) throw new RuntimeException("could not found defined heap object with name "+literalValue() +" ! ");
    od.obType
  }
}

object Token {
  protected [runtime] val valIndex=new java.util.concurrent.atomic.AtomicInteger(0);
}