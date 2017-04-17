package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.ast.{ StackToken }
import indi.lewis.spider.runtime.symbol.{Operator, Token}

/**
  * 逗号
  * Created by lewis on 17-4-14.
  */
class Comma() extends OpImpl{
  override def name(): String = ","

  override def ast(self:Operator):Token={
    if(self.preSymbol==null) throw new RuntimeException("there is nothing before comma!");
    if(self.preSymbol.isInstanceOf[StackToken]){
      val fc=self.preSymbol.asInstanceOf[StackToken];
      if(self.nextSymbol!=null){
        fc.push(self.nextSymbol);
        self.nextSymbol.preSymbol=null;
        fc.nextSymbol=self.nextSymbol.nextSymbol;
        self.nextSymbol.nextSymbol=null;
      }
      fc
    }else {
      val replace=new StackToken();
      //推入前一个对象
      replace.push(self.preSymbol)
      replace.preSymbol=self.preSymbol.preSymbol;
      if(self.preSymbol.preSymbol!=null) self.preSymbol.preSymbol.nextSymbol=replace;
      self.preSymbol.preSymbol=null;
      self.preSymbol.nextSymbol=null;
      //推入后一个对象
      if(self.nextSymbol!=null){
        replace.push(self.nextSymbol);
        if(self.nextSymbol.nextSymbol!=null){
          self.nextSymbol.nextSymbol.preSymbol=replace;
        }
        replace.nextSymbol=self.nextSymbol.nextSymbol;
        self.nextSymbol.nextSymbol=null;
        self.nextSymbol.preSymbol=null;
      }
      replace
    }
  }
}
