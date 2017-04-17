package indi.lewis.spider.runtime.symbol.ast

import java.util

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.symbol.Token

/**
  * Created by lewis on 2017/4/16.
  */

class ArrayToken extends Token{

  private val stack=new util.ArrayList[SyntaxSymbol]();

  def push(s :SyntaxSymbol):Unit= stack.add(s);
}
