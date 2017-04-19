package indi.lewis.spider.runtime.symbol.ast

import indi.lewis.spider.runtime.SyntaxSymbol
import indi.lewis.spider.runtime.fnlink.define.ObjectDef
import indi.lewis.spider.runtime.symbol.{Operator, Token}
import indi.lewis.spider.runtime.fnlink.{Instructions, OperationCode, RuntimeHeap}

/**
  * Created by lewis on 17-4-14.
  */
class TempToken() extends Token{

  def this( l:SyntaxSymbol,op:Int,r:SyntaxSymbol){
    this();
    this.left=l;
    this.operate=op;
    this.right=r;
  }

  var left:SyntaxSymbol=null;
  var right:SyntaxSymbol=null;

  var operate:Int = -1 ;
  var operateCode:String =_ ;

  override def calc():(String,String)={
    val builder=new StringBuilder();
    val builder2=new StringBuilder();

    val $var="$val"+Token.valIndex.incrementAndGet();
    val c1=new StringBuilder();
    if(left!=null) {
      val rl= if(left.isInstanceOf[Token]) left.asInstanceOf[Token].calc() else (null,left.literalValue())
      if(rl._1!=null) builder.append(rl._1);
      c1.append(rl._2)
    }
    c1.append(operateCode)
    if(right!=null){
      val rl= if(right.isInstanceOf[Token]) right.asInstanceOf[Token].calc() else (null,right.literalValue())
      if(rl._1!=null) builder.append(rl._1);
      c1.append(rl._2)
    }
    builder.append("val ").append($var).append(" = ").append(c1.toString()).append("\n");
    (builder.toString(),$var)
  }

  private def calcType(s:SyntaxSymbol,ins:Instructions):(RuntimeHeap=>Any,Class[_])={
    var lf:RuntimeHeap=>Any =null;
    var lt:Class[_] =null;
    if(s!=null){
      if(s.isInstanceOf[Token]) s.asInstanceOf[Token].instruction(ins);
      else new ConstToken(s).instruction(ins);
      lf=ins.functionLink;
      lt=s.retType();
    }
    (lf,lt)
  }

  override def instruction (ins:Instructions): Instructions ={
    val lt:(RuntimeHeap=>Any,Class[_])=calcType(left,ins);
    val rt:(RuntimeHeap=>Any,Class[_])=calcType(right,ins);
    //对等号做特殊处理
    if(operate!=OperationCode.OP_ASSIGN){
       val rte=OperationCode.codeToFunction(operate,lt,rt) ;
      if((!left.isInstanceOf[Token])&&(!right.isInstanceOf[Token])){
        val ret1=rte._1(null);
        ins.functionLink={heap:RuntimeHeap => ret1 }
      }else {
        ins.functionLink=rte._1
      }

      clz=rte._2
    }else{
      val li=if(left.isInstanceOf[NewToken]){
        val no=new ObjectDef(left.asInstanceOf[NewToken].value.literalValue(),classOf[Any]);
        ins.registerObject(no)
      }else
        ins.getObjIndex(left.literalValue());
      val lf=rt._1;
      ins.functionLink={ heap:RuntimeHeap =>
        val v=lf(heap);
        heap.setValue(li,v);
        v
      }
      clz=rt._2
    }
    ins
  }

  private var clz:Class[_] =null;

  override def retType(): Class[_] = clz
}
