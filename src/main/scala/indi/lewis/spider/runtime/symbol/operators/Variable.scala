package indi.lewis.spider.runtime.symbol.operators

import indi.lewis.spider.runtime.symbol.ast.{NewToken}
import indi.lewis.spider.runtime.symbol.{Operator, Token}

/**
  * Created by lewis on 17-4-19.
  */
class Variable extends OpImpl{
  override def name(): String = "var"

  override def ast(self:Operator) : Token={
    if(self.nextSymbol==null) throw new RuntimeException("there is nothing after variable define(var) !");
    if(self.preSymbol!=null) throw new RuntimeException("there must be nothing before variable define(var) !");
    if(!self.nextSymbol.isInstanceOf[Token]) throw new RuntimeException("next element after variable define(var) is not a token ! "+self.nextSymbol.literalValue());

    val fc=new NewToken(self.nextSymbol.asInstanceOf[Token]);
    fc.nextSymbol=self.nextSymbol.nextSymbol;
    if(self.nextSymbol.nextSymbol!=null){
      self.nextSymbol.nextSymbol.preSymbol=fc;
    }
    self.nextSymbol.preSymbol=null;
    self.nextSymbol.nextSymbol=null;
    fc
  }
}
