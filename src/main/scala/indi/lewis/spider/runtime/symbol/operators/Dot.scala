package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.symbol.ast.{FunctionToken, StackToken}

/**
  * Created by lewis on 17-4-14.
  */
class Dot() extends OpImpl{
  override def name(): String = "."

  override def ast(self:Operator):Token={
    if(self.preSymbol==null) throw new RuntimeException("there is nothing before dot !");
    if(self.nextSymbol==null) throw new RuntimeException("there is nothing after dot !");
    if(!self.nextSymbol.isInstanceOf[FunctionToken])
      throw new RuntimeException("there is not a funtion after dot ! real is "+self.nextSymbol);

    val fc=self.nextSymbol.asInstanceOf[FunctionToken];
    if(fc.stack==null) fc.stack=new StackToken();
    fc.stack.addCaller(self.preSymbol);
    if(self.preSymbol.preSymbol!=null){
      self.preSymbol.preSymbol.nextSymbol=fc
    }
    fc.preSymbol=self.preSymbol.preSymbol
    fc
  }
}
