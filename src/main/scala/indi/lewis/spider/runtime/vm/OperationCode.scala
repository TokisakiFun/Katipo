package indi.lewis.spider.runtime.vm

/**
  * Created by lewis on 17-4-14.
  */
object OperationCode {

  private var index=0;

  private def opCode():Int= { index+=1; index-1}

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

  /** 推栈 */
  final val OP_PUSH     : Int = opCode;

  /** 出栈 */
  final val OP_POP      : Int = opCode;

  /** 调用函数 */
  final val OP_CALL     : Int = opCode;
}
