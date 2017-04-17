package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.{Sentence, SyntaxSymbol}
import indi.lewis.spider.runtime.symbol.ast.{FunctionToken, StackToken}
import indi.lewis.spider.runtime.symbol.{Operator, Token}

import scala.util.control.Breaks

/**
  * 小括号
  * Created by lewis on 17-4-14.
  */
class Parentheses() extends OpImpl{
  override def name(): String = "("

  override def ast(self:Operator):Token={
    var next=self.nextSymbol;
    var last:SyntaxSymbol=null;
    var level=0;
    Breaks.breakable{
      while(next!=null){
        if(next.isInstanceOf[Operator]){
          val j=next.asInstanceOf[Operator];
          val v=j.realValue;
          if(v.isInstanceOf[Parentheses]){
            level+=1;
          }else if(v.isInstanceOf[Parentheses_]){
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
      throw new RuntimeException("parentheses can not be closed!");
    }else{
      var rr:SyntaxSymbol=null
      if(self.nextSymbol!=last){
        rr=self.nextSymbol;
        rr.preSymbol=null;
        last.preSymbol.nextSymbol=null;
      }
      val replace= if(self.preSymbol!=null&&(!self.preSymbol.isInstanceOf[Operator])){
        val rt=new FunctionToken();
        if(rr!=null){
          var rtt=Sentence(rr).ast();
          if(rtt.isInstanceOf[StackToken]) rt.stack=rtt.asInstanceOf[StackToken];
          else {
            val stack=new StackToken();
            stack.push(rtt)
            rt.stack=stack;
          }
        }else rt.stack=new StackToken();
        if(!self.preSymbol.isInstanceOf[Token])
          throw new RuntimeException(self.preSymbol.literalValue()+" is not a function ! ");

        val sp=self.preSymbol;
        rt.function=self.preSymbol.asInstanceOf[Token];
        if(sp.preSymbol!=null) sp.preSymbol.nextSymbol=rt;
        sp.preSymbol=null;
        sp.nextSymbol=null;
        rt
      }else {
        var sp=Sentence(rr).ast()
        if(self.preSymbol!=null) self.preSymbol.nextSymbol=sp;
        sp
      }
      if(last.nextSymbol!=null){
        last.nextSymbol.preSymbol=replace;
      }
      replace.nextSymbol=last.nextSymbol;
      replace
    }
  }
}

class Parentheses_() extends OpImpl{
  override def name(): String = ")"
}
