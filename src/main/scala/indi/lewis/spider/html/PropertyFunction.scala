package indi.lewis.spider.html

import java.io.File
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util

import indi.lewis.spider.utils.ClassFinder
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.mutable.StringBuilder

/**
  * Created by lewis on 2017/4/7.
  */
private[html] class PropertyFunction(str:String) {

  private val callType:CallType= if(str.startsWith("${")&&str.endsWith("}")){
    val builder=new StringBuilder()
    val real=str.trim
    for( i <- 2 to real.size -2; c=real.charAt(i)){
      if(c>=' '&& c<='~'){
        builder append c
      }
    }
    val call=builder.toString()
    val arr=call.split('%');
    if(arr.length==1) new SysFun(arr(0));
    else new NodeValue(arr(0),arr(1));
  }else {
    new Constant(str);
  }

  def get(doc :Document):ElementType= callType.call(doc)


  trait CallType {
    def call(doc :Document):ElementType ;
  }
  class NodeValue (selector:String,operator:String) extends CallType{
    val sl=operator.split('[');
    val funName: Element => String = sl(0) match {
      case "text" => {( e:Element)=> e.text() }
      case "html" => {( e:Element)=> e.html() }
      case _  => throw new RuntimeException(sl(0)+" is not valid node value function !");
    };
    val arrayPattern:(Boolean,Boolean,Int)={
      if(sl.length==1){
        (false,false,0)
      }else{
        val buffer=new StringBuffer()
        for(i <- 0 to sl(1).length -1; c=sl(1).charAt(i) if(c!=']')){
          if((c<'0' || c>'9')&&c!=' '){
            throw new RuntimeException(s"$operator is not valid node value function !");
          }
          buffer append c
        }
        if(buffer.length()>0) (true,true,Integer.parseInt(buffer.toString))
        else (true,false,0)
      }
    }

    val funCall : (Document) =>ElementType =arrayPattern match {
      case (false,_,_) =>{  (doc: Document) =>
        val els:Elements  = doc.select(selector);
        if(els.size()>0) new ModelConstant(null,funName(els.get(0)))
        else new ModelConstant(null,null)
      }
      case (true,true,index) => {
        (doc: Document) =>
          val els:Elements  = doc.select(selector);
          if(els.size()>index) new ModelConstant(null,funName(els.get(index)))
          else new ModelConstant(null,null)
      }
      case (true,false,_) => {  (doc: Document) =>
        val els:Elements  = doc.select(selector);
        val modelArray=new ModelArray(null)
        for( i<- 0 to els.size()-1; e = els.get(i))
          modelArray.array.add(new ModelConstant(null,funName(e)))
        modelArray
      }
    }

    override def call(doc: Document): ElementType = funCall(doc)

  };
  class SysFun (operator:String ) extends CallType {
    val f:()=>ElementType={
      val fd=PropertyFunction.env.get(operator);
      if(fd!=null){
        () => new ModelConstant(null,fd());
      }else{
        throw new RuntimeException(s"$operator is not valid function !");
      }
    }
    override def call(doc: Document): ElementType = f()
  }

  class Constant (value:String) extends CallType{
    override def call(doc: Document): ElementType = new ModelConstant(null,value)
  }

}

private[html] object PropertyFunction {

  private val env:java.util.HashMap[String,()=>String]={

    val map=new util.HashMap[String,()=>String]();
    val clzs=ClassFinder.getClasses(PropertyFunction.getClass.getPackage.getName+".nestfuns");
    val iterator=clzs.iterator();
    val fz=classOf[NestFunction]
    while(iterator.hasNext){
      val tz=iterator.next();
      if(fz.isAssignableFrom(tz)){
        val mod=tz.getModifiers;
        if((!Modifier.isAbstract(mod))&&(!Modifier.isInterface(mod))){
          val func=tz.newInstance().asInstanceOf[NestFunction];
          map.put(func.funcName,func.funcBody)
        }
      }
    }
    map
  }


  def read(str : String):(Document)=>ElementType={
    val commands:java.util.ArrayList[StringBuilder]=new java.util.ArrayList[StringBuilder]();
    var stringBuilder=new StringBuilder
    var pre=0;
    for( c <- str){
      c match {
        case '$' => {commands.add(stringBuilder);stringBuilder=new StringBuilder("$")}
        case '}' => {stringBuilder.append('}');commands.add(stringBuilder);stringBuilder=new StringBuilder()}
        case _ => stringBuilder.append(c)
      }
    }
    commands.add(stringBuilder);
    val ret:java.util.ArrayList[PropertyFunction]=new java.util.ArrayList[PropertyFunction]();
    var tmp=new StringBuilder
    for(i <- 0 to commands.size()-1 ;
        b=commands.get(i)){
      if(b.startsWith("${")&&b.endsWith("}")){
        if(tmp.length>0){
          ret add  new PropertyFunction(tmp.toString())
        }
        ret add new PropertyFunction(b.toString())
        tmp=new StringBuilder
      }else{
        tmp.append(b)
      }
    }
    if(tmp.length>0){
      ret add  new PropertyFunction(tmp.toString())
    }
    val  get:(Document)=>ElementType = { (doc:Document)=>
      if(ret.size()>1){
        val arr=new ModelArray(null)
        for(i <- 0 to ret.size() -1; b=ret.get(i))
          arr.array.add(b.get(doc))
        arr
      }else if(ret.size()==0) new ModelConstant(null,null)
      else ret.get(0).get(doc)
    }
    get
  }
}