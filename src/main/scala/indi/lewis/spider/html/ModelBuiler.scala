package indi.lewis.spider.html

import com.google.gson._

/**
  * Created by lewis on 2017/4/7.
  */
object ModelBuiler {

  def compile(template:String):ModelParent={
    val gson=new GsonBuilder().create()
    val model=gson.fromJson(template,new JsonObject().getClass)
    parseModel(model.getAsJsonObject)
  }

  private def parseModel( modelTree:JsonObject):ModelParent={
    val model=new ModelParent
    modelTree.entrySet().forEach(entry =>{
      val value=entry.getValue
      if(value.isInstanceOf[JsonObject]){
        val child=parseModel(value.asInstanceOf[JsonObject]);
        child.elementName=entry.getKey
        model.properties.add(child)
      }else if(value.isInstanceOf[JsonArray]){
        model.properties.add(parseModelArray(entry.getKey,value.asInstanceOf[JsonArray]))
      }else if(value.isInstanceOf[JsonPrimitive]){
        model.properties.add(parseJsonPrimitive(entry.getKey,value.asInstanceOf[JsonPrimitive]))
      }
    });
    model
  }

  private def parseModelArray (key:String,arr:JsonArray):ModelArray ={
    val ma=new ModelArray(key)
    for( i <- 0 to arr.size()-1; a=arr.get(i)){
      if(a.isInstanceOf[JsonObject]){
        ma.array.add(parseModel(a.asInstanceOf[JsonObject]))
      }else if(a.isInstanceOf[JsonArray]){
        ma.array.add(parseModelArray(null,a.asInstanceOf[JsonArray]))
      }else if(a.isInstanceOf[JsonPrimitive]){
        ma.array.add(parseJsonPrimitive(null,a.asInstanceOf[JsonPrimitive]))
      }
    }
    ma
  }

  private def parseJsonPrimitive(key:String,o : JsonPrimitive):ElementType={
    if(!o.isString){
      new ModelConstant(key,o.getAsBigDecimal)
    }else{
      new ModelElement(key,PropertyFunction.read(o.getAsString))
    }
  }
}
