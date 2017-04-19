package indi.lewis.spider.runtime.fnlink

/**
  * Created by lewis on 17-4-14.
  */
object OperationCode {



  private var index=0;

  private def opCode():Int= { index+=1; index-1}

  /** 赋值 */
  final val OP_ASSIGN   : Int = opCode;

  /** 加 */
  final val OP_ADD      : Int = opCode;

  /** 减 */
  final val OP_MINUS    : Int = opCode;

  /** 乘 */
  final val OP_MULTIPLY : Int = opCode;

  /** 除 */
  final val OP_DIVIDE   : Int = opCode;

  /** 取模 */
  final val OP_MOD      : Int = opCode;

  final val MP_RET      : Int = -2 ;
  final val MP_ZERO     : Int = -1 ;

  private def isNumber(clz:Class[_]):Boolean= clz==classOf[Double] || clz==classOf[Int]

  private def calcNumber(lt: ((RuntimeHeap) => Any, Class[_]),
                         rt: ((RuntimeHeap) => Any, Class[_]),
                         ff:(Number,Number)=>Any ):((RuntimeHeap) => Any,Class[_] )={
    val lff=lt._1;
    val rff=rt._1;
    val ct= if(lt._2==rt._2) lt._2 else classOf[Double];
    if(ct==classOf[Double])
      ({heap:RuntimeHeap=> ff(lff(heap).asInstanceOf[Double] , rff(heap).asInstanceOf[Double])},classOf[Double])
    else
      ({heap:RuntimeHeap=> ff(lff(heap).asInstanceOf[Int] , rff(heap).asInstanceOf[Int])},classOf[Int])

  }

  def codeToFunction(operate: Int,
                     lt: ((RuntimeHeap) => Any, Class[_]),
                     rt: ((RuntimeHeap) => Any, Class[_])): ( RuntimeHeap => Any,Class[_] )= {
    operate match {
      case OP_ADD => {
        if(lt._2==null){
          ( rt._1,rt._2 )
        }else{
          val lff=lt._1;
          val rff=rt._1;
          if(isNumber(lt._2)&&isNumber(rt._2)){
            val ct= if(lt._2==rt._2) lt._2 else classOf[Double];
            if(ct==classOf[Double])
             ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Double] + rff(heap).asInstanceOf[Double]},classOf[Double])
            else
             ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Int] + rff(heap).asInstanceOf[Int]},classOf[Int])
          }else{
            val string={(obj:Any)=>if(obj==null) "null" else obj.toString}
            ({heap:RuntimeHeap=> string(lff(heap)) + string(rff(heap))},classOf[String])
          }
        }
      };
      case OP_MINUS => {
        if(lt._2==null){
          if(rt._2==classOf[Double]){
            ( {heap:RuntimeHeap=> 0 - rt._1(heap).asInstanceOf[Double] },rt._2 )
          }else{
            ( {heap:RuntimeHeap=> 0 - rt._1(heap).asInstanceOf[Int] },rt._2 )
          }
        }else{
          val lff=lt._1;
          val rff=rt._1;
          val ct = if (lt._2 == rt._2) lt._2 else classOf[Double];
          if(ct==classOf[Double])
            ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Double] - rff(heap).asInstanceOf[Double]},classOf[Double])
          else
          ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Int] - rff(heap).asInstanceOf[Int]},classOf[Int])
        }
      };
      case OP_DIVIDE =>{
        val lff=lt._1;
        val rff=rt._1;
        val ct = if (lt._2 == rt._2) lt._2 else classOf[Double];
        if(ct==classOf[Double])
          ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Double] / rff(heap).asInstanceOf[Double]},classOf[Double])
        else
          ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Int] / rff(heap).asInstanceOf[Int]},classOf[Int])
      } ;
      case OP_MULTIPLY => {
        val lff=lt._1;
        val rff=rt._1;
        val ct = if (lt._2 == rt._2) lt._2 else classOf[Double];
        if(ct==classOf[Double])
          ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Double] * rff(heap).asInstanceOf[Double]},classOf[Double])
        else
          ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Int] * rff(heap).asInstanceOf[Int]},classOf[Int])
      }
      case OP_MOD => {
        val lff=lt._1;
        val rff=rt._1;
        ({heap:RuntimeHeap=> lff(heap).asInstanceOf[Int] % rff(heap).asInstanceOf[Int]},classOf[Int])
      }
      case _ => throw new RuntimeException(operate +" is not a operator ! ")
    }
  }
}
