/*    */ package com.dukascopy.transport.common.datafeed;
/*    */ 
/*    */ public class KeyNotFoundException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 8039344816696230293L;
/*    */ 
/*    */   public KeyNotFoundException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public KeyNotFoundException(String message, Throwable cause)
/*    */   {
/* 12 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public KeyNotFoundException(String message) {
/* 16 */     super(message);
/*    */   }
/*    */ 
/*    */   public KeyNotFoundException(Throwable cause) {
/* 20 */     super(cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.datafeed.KeyNotFoundException
 * JD-Core Version:    0.6.0
 */