package indi.lewis.spider.runtime.symbol.operators

import java.util

import indi.lewis.spider.runtime.ClassMeta
import indi.lewis.spider.runtime.symbol.ast.TempToken
import indi.lewis.spider.runtime.symbol.{Operator, Token}

/**
  * Created by lewis on 17-4-14.
  */
abstract class OpImpl {

  def name():String ;

  def ast(self:Operator): Token = null

  protected [runtime] def replaceNode(self:Operator,code:Int):TempToken={
    val fc=new TempToken(self.preSymbol,code,self.nextSymbol);
    fc.operateCode= name();

    if(self.preSymbol!=null) fc.preSymbol=self.preSymbol.preSymbol;
    if(self.preSymbol.preSymbol!=null){
      self.preSymbol.preSymbol.nextSymbol=fc
    }
    self.preSymbol.preSymbol=null
    self.preSymbol.nextSymbol=null

    fc.nextSymbol=self.nextSymbol.nextSymbol;
    if(self.nextSymbol.nextSymbol!=null){
      self.nextSymbol.nextSymbol.preSymbol=fc;
    }
    self.nextSymbol.preSymbol=null;
    self.nextSymbol.nextSymbol=null;
    fc
  }
}

object OpImpl {

  private val map=new util.HashMap[String,ClassMeta[OpImpl]]();
  private val index=new util.HashMap[Class[_],Int]();

  private val indexSeq=new java.util.concurrent.atomic.AtomicInteger(0);

  private def regist(clz:Class[_ <:OpImpl]):Unit={
    val cm=new ClassMeta(clz);
    map.put(cm.getNew().name(),cm);
    index.put(clz,indexSeq.incrementAndGet());
  }

  private def regist(clz:Class[_ <:OpImpl],ci:Int):Unit={
    val cm=new ClassMeta(clz);
    map.put(cm.getNew().name(),cm);
    index.put(clz,ci);
  }

  def isOperator(c :String):Boolean={
    for( m <- map.keySet().toArray){
      if(m.toString.startsWith(c)){
        return true
      }
    }
    false
  }

  def getIndex(clz:Class[_ <:OpImpl]):Int={
    if(index.containsKey(clz)) index.get(clz)
    else -1
  }

  def get(c :String):OpImpl={
    map.get(c).getNew();
  }

  {
    //注册可识别的标识符，注册顺序代表运算顺序
    //小括号
    regist(classOf[Parentheses]);
    regist(classOf[Parentheses_]);

    //函数调用
    regist(classOf[Dot]);

    //乘除取余
    regist(classOf[Multiply]);
    regist(classOf[Divide],getIndex(classOf[Multiply]));
    regist(classOf[Mod],getIndex(classOf[Multiply]));

    //加减
    regist(classOf[Plus]);
    regist(classOf[Minus],getIndex(classOf[Plus]));

    //中括号
    regist(classOf[Bracket]);
    regist(classOf[Bracket_]);

    //逗号
    regist(classOf[Comma]);
  }


//  private def loadSymbols():Unit={
//
//    val classSet=ClassFinder.getClasses(OpImpl.getClass.getPackage.getName);
//    val iterator=classSet.iterator();
//    val fcz=classOf[OpImpl];
//    while(iterator.hasNext){
//      val clz=iterator.next()
//      val mod=clz.getModifiers;
//      if((!Modifier.isAbstract(mod))&&(!Modifier.isInterface(mod))){
//        if(fcz.isAssignableFrom(clz)){
//          val i=clz.newInstance().asInstanceOf[OpImpl];
//          map.put(i.name(),i);
//        }
//      }
//    }
//  }
//  loadSymbols()
}
