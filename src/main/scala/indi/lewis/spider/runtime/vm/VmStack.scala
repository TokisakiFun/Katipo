package indi.lewis.spider.runtime.vm

/**
  * Created by lewis on 17-4-14.
  */
private[vm] class VmStack private (val initSize:Int) {

  private var stack:Array[Any]=new Array[Any](initSize);

  private var pointer=0;

  def push(a:Any):Unit={
    if(pointer==stack.length){
      val newStack=new Array[Any](stack.length*2);
      Array.copy(stack,0,newStack,0,stack.length);
      stack=newStack;
    }
    stack(pointer)=a
    pointer+=1;
  }

  def pop():Any={
    pointer-=1;
    stack(pointer)
  }
}
object VmStack {
  def newStack():VmStack=new VmStack(8);
}
