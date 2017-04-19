package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.symbol.ast.{FunctionToken, StackToken, TempToken}
import indi.lewis.spider.runtime.fnlink.OperationCode

/**
  * Created by lewis on 17-4-14.
  */
class Multiply() extends OpImpl{

  override def name(): String = "*"

  override def ast(self:Operator):Token={
    if(self.preSymbol==null) throw new RuntimeException("there is nothing before multiply(*) !");
    if(self.nextSymbol==null) throw new RuntimeException("there is nothing after multiply(*) !");

    replaceNode(self,OperationCode.OP_MULTIPLY)
  }
}
