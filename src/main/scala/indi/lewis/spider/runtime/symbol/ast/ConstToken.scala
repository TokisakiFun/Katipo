package indi.lewis.spider.runtime.symbol.ast

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.Token

/**
  * Created by lewis on 17-4-14.
  */
class ConstToken(val value:SyntaxSymbol) extends Token{

  override def literalValue(): String = value.literalValue()
}
