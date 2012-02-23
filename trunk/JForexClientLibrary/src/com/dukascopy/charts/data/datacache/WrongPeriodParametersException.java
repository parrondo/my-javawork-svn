/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ public class WrongPeriodParametersException extends DataCacheException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public WrongPeriodParametersException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public WrongPeriodParametersException(String message)
/*    */   {
/* 13 */     super(message);
/*    */   }
/*    */ 
/*    */   public WrongPeriodParametersException(Throwable cause) {
/* 17 */     super(cause);
/*    */   }
/*    */ 
/*    */   public WrongPeriodParametersException(String message, Throwable cause) {
/* 21 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.WrongPeriodParametersException
 * JD-Core Version:    0.6.0
 */