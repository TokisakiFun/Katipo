package indi.lewis.spider.runtime

/**
  * Created by lewis on 17-4-13.
  */
object SyntaxSymbolTest {

  def main(args: Array[String]) {

    val f:String=>Unit={(str:String) =>
      println("Sentence String :"+str)
      println("Parse Result    :")
      val sc=SyntaxSymbol.parseSentence(str)
      sc.print()
      println("Calculate Plan  :")
      val ret=sc.ast().calc();
      println(ret._1)
      println(ret._2)
    }

    f ("a.call(12,\"fff\",d%2)");

    f ("a.call(b(),c(e,f),d/3)");

    f ("a.call(1*2,c(e,f),7-6,5*d(1,2))");

    f ("f-g*call(f,a+b-c*d/f%e)");

  }
}
