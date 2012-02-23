/*    */ package com.dukascopy.transport.common.pojoSerializer;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class ObjectFieldDescriptor
/*    */ {
/*    */   private String fieldName;
/*    */   private Method setter;
/*    */   private Method getter;
/*    */   private Class parameterType;
/*    */   private Class resultType;
/*    */ 
/*    */   protected String getFieldName()
/*    */   {
/* 25 */     return this.fieldName;
/*    */   }
/*    */ 
/*    */   protected void setFieldName(String fieldName) {
/* 29 */     this.fieldName = fieldName;
/*    */   }
/*    */ 
/*    */   protected Method getSetter() {
/* 33 */     return this.setter;
/*    */   }
/*    */ 
/*    */   protected void setSetter(Method setter) {
/* 37 */     this.setter = setter;
/*    */   }
/*    */ 
/*    */   protected Method getGetter() {
/* 41 */     return this.getter;
/*    */   }
/*    */ 
/*    */   protected void setGetter(Method getter) {
/* 45 */     this.getter = getter;
/*    */   }
/*    */ 
/*    */   protected Class getParameterType() {
/* 49 */     return this.parameterType;
/*    */   }
/*    */ 
/*    */   protected void setParameterType(Class parameterType) {
/* 53 */     this.parameterType = parameterType;
/*    */   }
/*    */ 
/*    */   protected Class getResultType() {
/* 57 */     return this.resultType;
/*    */   }
/*    */ 
/*    */   protected void setResultType(Class resultType) {
/* 61 */     this.resultType = resultType;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.pojoSerializer.ObjectFieldDescriptor
 * JD-Core Version:    0.6.0
 */