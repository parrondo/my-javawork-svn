/*    */ package com.dukascopy.transport.common;
/*    */ 
/*    */ import com.dukascopy.transport.common.model.type.RejectReason;
/*    */ 
/*    */ public class TradeException extends Exception
/*    */ {
/*    */   private RejectReason reason;
/*    */ 
/*    */   public TradeException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public TradeException(String message)
/*    */   {
/* 18 */     super(message);
/*    */   }
/*    */ 
/*    */   public TradeException(String message, Throwable cause) {
/* 22 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public TradeException(Throwable cause) {
/* 26 */     super(cause);
/*    */   }
/*    */ 
/*    */   public TradeException(String message, RejectReason reason) {
/* 30 */     this(message);
/* 31 */     this.reason = reason;
/*    */   }
/*    */ 
/*    */   public RejectReason getReason()
/*    */   {
/* 36 */     return this.reason;
/*    */   }
/*    */ 
/*    */   public void setReason(RejectReason reason) {
/* 40 */     this.reason = reason;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.TradeException
 * JD-Core Version:    0.6.0
 */