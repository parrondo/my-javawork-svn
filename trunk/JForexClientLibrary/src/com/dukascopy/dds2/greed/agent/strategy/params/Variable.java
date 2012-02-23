/*    */ package com.dukascopy.dds2.greed.agent.strategy.params;
/*    */ 
/*    */ public class Variable
/*    */ {
/*    */   private Object value;
/*    */   private Class<?> type;
/*    */ 
/*    */   public Variable(Object value, Class<?> type)
/*    */   {
/* 12 */     this.value = value;
/* 13 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public Object getValue() {
/* 17 */     return this.value;
/*    */   }
/*    */ 
/*    */   public void setValue(Object value) {
/* 21 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public Class<?> getType() {
/* 25 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(Class<?> type) {
/* 29 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 34 */     return "Variable[" + this.value + " / " + this.type + "]";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.params.Variable
 * JD-Core Version:    0.6.0
 */