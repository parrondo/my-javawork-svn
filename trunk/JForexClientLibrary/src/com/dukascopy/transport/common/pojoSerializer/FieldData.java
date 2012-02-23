/*    */ package com.dukascopy.transport.common.pojoSerializer;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class FieldData
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 200812021207L;
/*    */   private String fieldName;
/*    */   private Object value;
/* 13 */   private boolean serObject = false;
/*    */ 
/*    */   protected FieldData(String fieldName, Object value)
/*    */   {
/* 17 */     this.fieldName = fieldName;
/* 18 */     this.value = value;
/*    */   }
/*    */ 
/*    */   protected String getFieldName() {
/* 22 */     return this.fieldName;
/*    */   }
/*    */ 
/*    */   protected void setFieldName(String fieldName) {
/* 26 */     this.fieldName = fieldName;
/*    */   }
/*    */ 
/*    */   protected Object getValue() {
/* 30 */     return this.value;
/*    */   }
/*    */ 
/*    */   protected void setValue(Object value) {
/* 34 */     this.value = value;
/*    */   }
/*    */ 
/*    */   protected boolean isSerObject() {
/* 38 */     return this.serObject;
/*    */   }
/*    */ 
/*    */   protected void setSerObject(boolean serObject) {
/* 42 */     this.serObject = serObject;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.pojoSerializer.FieldData
 * JD-Core Version:    0.6.0
 */