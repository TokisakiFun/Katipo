package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.fnlink.OperationCode

/**
  * Created by lewis on 17-4-17.
  */
class Assign extends OpImpl{
  override def name(): String = "="

  override def ast(self:Operator):Token={
    if(self.preSymbol==null) throw new RuntimeException("there is nothing before  Assign(=) !");
    if(self.nextSymbol==null) throw new RuntimeException("there is nothing after Assign(=) !");

    replaceNode(self,OperationCode.OP_ASSIGN);
  }
}
