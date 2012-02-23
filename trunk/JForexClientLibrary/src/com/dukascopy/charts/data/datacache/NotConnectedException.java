/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ public class NotConnectedException extends DataCacheException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public NotConnectedException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NotConnectedException(String message, Throwable cause)
/*    */   {
/* 14 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public NotConnectedException(String message) {
/* 18 */     super(message);
/*    */   }
/*    */ 
/*    */   public NotConnectedException(Throwable cause) {
/* 22 */     super(cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.NotConnectedException
 * JD-Core Version:    0.6.0
 */