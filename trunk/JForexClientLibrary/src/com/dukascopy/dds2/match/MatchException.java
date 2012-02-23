/*    */ package com.dukascopy.dds2.match;
/*    */ 
/*    */ public class MatchException extends RuntimeException
/*    */ {
/*    */   public MatchException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public MatchException(String message)
/*    */   {
/* 13 */     super(message);
/*    */   }
/*    */ 
/*    */   public MatchException(String message, Throwable cause) {
/* 17 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public MatchException(Throwable cause) {
/* 21 */     super(cause);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.match.MatchException
 * JD-Core Version:    0.6.0
 */