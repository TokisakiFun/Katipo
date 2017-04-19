package indi.lewis.spider.runtime

import indi.lewis.spider.runtime.fnlink.Instructions
import indi.lewis.spider.runtime.fnlink.define.{FunctionDef, ObjectDef}

/**
  * Created by lewis on 17-4-18.
  */
object FullOpertations {
  def main(args: Array[String]) {
    val funs=Array(
      "var tfg =\"sss\"+a(2*2+1,ff)+6*5",
      "fg = tfg+\"sss\" "
    );
    //解析语法树
    val asts=for (f <- funs )
      yield SyntaxSymbol.parseSentence(f).ast();
    //指令集定义
    val ins=new Instructions();
    //注册内置函数定义
    val a=new FunctionDef[String]("a",Array(classOf[Int],classOf[String]),classOf[String],{ p : Array[Any] =>
      val sr=p(0).asInstanceOf[Int]
      val it=p(1).asInstanceOf[String]
      val builder=new StringBuilder();
      for(i <- 0 to sr-1 ) builder.append(it)
      builder.toString()
    });
    ins.registerMethod(a)
    //注册内置对象类型
    ins.registerObject(new ObjectDef("ff",classOf[String]))
    ins.registerObject(new ObjectDef("fg",classOf[String]))
    //生成执行函数
    for(ast <- asts )ins.pushFunction(ast);

    //生成运行时环境
    val runtime=ins.runtime(("ff","12"));
    //开始执行
    runtime.run();
    println (runtime.getValue("tfg"));
    println (runtime.getValue("fg"));
  }

}
