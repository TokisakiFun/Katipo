package indi.lewis.spider.html

import java.util

import com.google.gson._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
  * Created by lewis on 2017/4/7.
  */
private[html] trait ElementType {
  var elementName: String = _;
  def compile(doc:Document):JsonElement;
  def compile(doc:String):JsonElement=compile(Jsoup.parse(doc));
}

private[html] case class ModelParent() extends ElementType {
  elementName="root";

  def this(elementName: String) {
    this();
    this.elementName = elementName;
  }

  val properties: java.util.ArrayList[ElementType] = new util.ArrayList[ElementType]();

  override def compile(doc: Document): JsonElement = {
    val ret=new JsonObject
    for(i <- 0 to properties.size()-1; o= properties.get(i)){
      ret.add(o.elementName,o.compile(doc))
    }
    ret
  }
}

private[html] case class ModelElement(val elName: String, val f: (Document) => ElementType) extends ElementType {
  this.elementName=elName
  override def compile(doc: Document): JsonElement = f(doc).compile(doc)
}

private[html] case class ModelArray(val elName: String) extends ElementType {
  val array: java.util.ArrayList[ElementType] = new util.ArrayList[ElementType]();
  this.elementName=elName
  override def compile(doc: Document): JsonElement = {
    val jsonArray=new JsonArray
    for(i <- 0 to array.size()-1; o= array.get(i)){
      jsonArray.add(o.compile(doc))
    }
    jsonArray
  }
}

private[html] case class ModelConstant(val elName: String, val value: Object) extends ElementType {
  this.elementName=elName
  override def compile(doc: Document): JsonElement = if(value!=null)new JsonPrimitive(value.toString) else JsonNull.INSTANCE
}
