/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ public class NoDataForPeriodException extends DataCacheException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public NoDataForPeriodException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public NoDataForPeriodException(String message)
/*    */   {
/* 13 */     super(message);
/*    */   }
/*    */ 
/*    */   public NoDataForPeriodException(Throwable cause) {
/* 17 */     super(cause);
/*    */   }
/*    */ 
/*    */   public NoDataForPeriodException(String message, Throwable cause) {
/* 21 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.NoDataForPeriodException
 * JD-Core Version:    0.6.0
 */