package indi.lewis.spider.runtime

import java.util

import indi.lewis.spider.runtime.symbol.{Blank, Operator, Quote, Token,Number}


/**
  * Created by lewis on 2017/4/12.
  */
trait SyntaxSymbol {

  var preSymbol:SyntaxSymbol=null;

  var nextSymbol:SyntaxSymbol=null;

  def testAdd(c :Char):Boolean;

  def checkGrammar():Unit;

  def literalValue():String ;

  def ast(): Token ;

}

object SyntaxSymbol {

  private val symbolList=new util.ArrayList[ClassMeta[SyntaxSymbol]]();

  protected[runtime] def registSymbol[A <: SyntaxSymbol](clzz : Class[A]):Unit={
    val clz=new ClassMeta(clzz);
    if(!symbolList.contains(clz)){
      symbolList.add(clz);
    }
  }

  private def newSymbolList():java.util.LinkedList[SyntaxSymbol]={
    val len=symbolList.size()-1;
    val list=new java.util.LinkedList[SyntaxSymbol]();
    val iterator=symbolList.iterator();
    while(iterator.hasNext){
     list.add(iterator.next().getNew())
    }
    list
  }

  def parseSentence(sentence:String):Sentence={
    var first:SyntaxSymbol= null;
    var last:SyntaxSymbol= null;
    var cur:SyntaxSymbol=null;
    var tmpSymbol=newSymbolList();

    var index=0;
    for(c <- sentence){
      var iterator=tmpSymbol.iterator();
      if(tmpSymbol.size()==1){
        cur=tmpSymbol.get(0);
      }
      while(iterator.hasNext){
        val s=iterator.next();
        if(!s.testAdd(c)) iterator.remove();
      }
      if(tmpSymbol.size()==0){
        if(cur!=null){
          if(first==null){first=cur;last=cur;}
          else{
            last.nextSymbol=cur;
            cur.preSymbol=last;
          }
          last=cur;
          cur=null;
          tmpSymbol=newSymbolList();
          iterator=tmpSymbol.iterator();
          while(iterator.hasNext){
            val s=iterator.next();
            if(!s.testAdd(c)) iterator.remove();
          }
          if(tmpSymbol.size()==0){
            throw new RuntimeException(s"$sentence : stop compile at $index ! unrecognizable word $c !")
          }
        }else if(cur==null&&last!=null){
          throw new RuntimeException(s"$sentence : stop compile at $index ! multi meaning!")
        }
      }
      index+=1;
    }
    if(tmpSymbol.size()==1){
      cur=tmpSymbol.get(0)
      last.nextSymbol=cur;
      cur.preSymbol=last;
    }else if(tmpSymbol.size()>1) throw new RuntimeException(s"$sentence : stop compile at $index ! multi meaning!")

    var iterator=first;
    while(iterator!=null){
      iterator.checkGrammar();
      iterator=iterator.nextSymbol
    }
    Sentence(trimBlank(first))
  }

  def trimBlank(first:SyntaxSymbol):SyntaxSymbol={

    val findNext:SyntaxSymbol=>SyntaxSymbol = {( s: SyntaxSymbol) =>
      var iterator=s;
      while (iterator!=null&&iterator.isInstanceOf[Blank]) iterator=iterator.nextSymbol;
      iterator
    }

    val ofirst:SyntaxSymbol= findNext(first);
    var next=ofirst
    while(next!=null){
      next.nextSymbol=findNext(next.nextSymbol);
      next=next.nextSymbol
    }

    ofirst
  }

  private def loadSymbols():Unit={

  SyntaxSymbol.registSymbol(classOf[Blank]);
  SyntaxSymbol.registSymbol(classOf[Number]);
  SyntaxSymbol.registSymbol(classOf[Operator]);
  SyntaxSymbol.registSymbol(classOf[Quote]);
  SyntaxSymbol.registSymbol(classOf[Token]);
  }


  loadSymbols();
}
