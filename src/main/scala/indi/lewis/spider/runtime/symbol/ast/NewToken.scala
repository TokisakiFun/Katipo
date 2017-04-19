package indi.lewis.spider.runtime.symbol.ast

import indi.lewis.spider.runtime.symbol.Token

/**
  * Created by lewis on 17-4-19.
  */
class NewToken (val value:Token) extends Token{

  override def literalValue(): String = value.literalValue()

  override def retType(): Class[_] = classOf[Any]
}
