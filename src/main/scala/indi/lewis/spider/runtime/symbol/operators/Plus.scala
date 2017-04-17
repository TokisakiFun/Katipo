package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.{Operator, Token,Number}
import indi.lewis.spider.runtime.vm.OperationCode

/**
  * Created by lewis on 17-4-14.
  */
class Plus() extends OpImpl{
  override def name(): String = "+"

  override def ast(self:Operator):Token={
    if(self.nextSymbol==null) throw new RuntimeException("there is nothing after plus(+) !");
    replaceNode(self,OperationCode.OP_ADD)
  }
}
