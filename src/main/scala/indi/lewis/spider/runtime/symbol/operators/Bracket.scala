package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.{Sentence, SyntaxSymbol}
import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.symbol.ast.{ArrayToken, FunctionToken}

import scala.util.control.Breaks

/**
  * 中括号
  * Created by lewis on 17-4-14.
  */
class Bracket() extends OpImpl{
  override def name(): String = "["

  override def ast(self:Operator):Token={
    var next=self.nextSymbol;
    var last:SyntaxSymbol=null;
    var level=0;
    Breaks.breakable{
      while(next!=null){
        if(next.isInstanceOf[Operator]){
          val j=next.asInstanceOf[Operator];
          val v=j.realValue;
          if(v.isInstanceOf[Bracket]){
            level+=1;
          }else if(v.isInstanceOf[Bracket_]){
            if(level==0){
              last=next;
              Breaks.break();
            }else{
              level-=1;
            }
          }
        }
        next=next.nextSymbol
      }
    }
    if(last==null){
      throw new RuntimeException("bracket can not be closed!");
    }else{
      var rr:SyntaxSymbol=null
      if(self.nextSymbol!=last){
        rr=self.nextSymbol;
        rr.preSymbol=null;
        last.preSymbol.nextSymbol=null;
      }
      val replace=
        if(rr!=null&&rr.nextSymbol!=null){
          val r2=Sentence(rr).ast()
          if(!r2.isInstanceOf[FunctionToken]){
            throw new RuntimeException("wrong type in bracket !");
          }
          r2
        } else{
          val rt=new ArrayToken();
          if(rr!=null) rt.push(rr) ;
          rt
        };
      if(self.preSymbol!=null) self.preSymbol.nextSymbol=replace;
      if(last.nextSymbol!=null){
        last.nextSymbol.preSymbol=replace;
      }
      replace;
    }
  }
}

class Bracket_() extends OpImpl{
  override def name(): String = "]"
}
