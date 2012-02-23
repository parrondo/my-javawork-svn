/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class InvocationRequest
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 200706151026L;
/*    */   private String interfaceClass;
/* 11 */   private Object[] params = new Object[0];
/*    */   private String methodName;
/*    */   private Long requestId;
/*    */ 
/*    */   public InvocationRequest(String interfaceClass, Object[] params, String methodName)
/*    */   {
/* 26 */     this.interfaceClass = interfaceClass;
/* 27 */     this.params = params;
/* 28 */     this.methodName = methodName;
/*    */   }
/*    */ 
/*    */   public String getInterfaceClass()
/*    */   {
/* 35 */     return this.interfaceClass;
/*    */   }
/*    */ 
/*    */   public void setInterfaceClass(String interfaceClass)
/*    */   {
/* 42 */     this.interfaceClass = interfaceClass;
/*    */   }
/*    */ 
/*    */   public Object[] getParams()
/*    */   {
/* 49 */     return this.params;
/*    */   }
/*    */ 
/*    */   public void setParams(Object[] params)
/*    */   {
/* 56 */     this.params = params;
/*    */   }
/*    */ 
/*    */   public String getMethodName()
/*    */   {
/* 63 */     return this.methodName;
/*    */   }
/*    */ 
/*    */   public void setMethodName(String methodName)
/*    */   {
/* 70 */     this.methodName = methodName;
/*    */   }
/*    */ 
/*    */   public Long getRequestId()
/*    */   {
/* 77 */     return this.requestId;
/*    */   }
/*    */ 
/*    */   public void setRequestId(Long requestId)
/*    */   {
/* 84 */     this.requestId = requestId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.InvocationRequest
 * JD-Core Version:    0.6.0
 */