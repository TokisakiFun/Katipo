package indi.lewis.spider.runtime

import indi.lewis.spider.runtime.AstTree.AstNode
import indi.lewis.spider.runtime.symbol.Operator

/**
  * Created by lewis on 17-4-14.
  */
class AstTree {



}

object AstTree {


  def parseTree(first:SyntaxSymbol):AstTree.AstNode={


    null
  }

  class AstNode() {

    var left:AstNode=_;

    var right:AstNode=_;

    var oper:Operator=_;
  }
}
