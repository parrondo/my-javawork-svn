/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ public class TaLibException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public TaLibException(String message, Throwable cause)
/*    */   {
/* 11 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public TaLibException(Throwable cause) {
/* 15 */     super(cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.TaLibException
 * JD-Core Version:    0.6.0
 */