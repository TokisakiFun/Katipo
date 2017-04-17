package indi.lewis.spider.runtime

import indi.lewis.spider.utils.ClassFinder

/**
  * Created by lewis on 17-4-14.
  */
object ClassTest {

  def main(args: Array[String]) {
    println(classOf[SyntaxSymbol])
    val classSet=ClassFinder.getClasses(SyntaxSymbol.getClass.getPackage.getName+".symbol");
    val iterator=classSet.iterator();
    while(iterator.hasNext){
      println(iterator.next())
    }
  }
}
