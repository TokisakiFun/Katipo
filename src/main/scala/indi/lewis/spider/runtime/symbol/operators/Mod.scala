package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.vm.OperationCode

/**
  * Created by lewis on 17-4-14.
  */
class Mod() extends OpImpl{
  override def name(): String = "%"

  override def ast(self:Operator):Token={
    if(self.preSymbol==null) throw new RuntimeException("there is nothing before mode(%) !");
    if(self.nextSymbol==null) throw new RuntimeException("there is nothing after mode(%) !");

    replaceNode(self,OperationCode.OP_MOD)
  }
}
