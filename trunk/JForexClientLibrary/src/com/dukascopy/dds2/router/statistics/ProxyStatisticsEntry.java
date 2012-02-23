/*    */ package com.dukascopy.dds2.router.statistics;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class ProxyStatisticsEntry
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 0L;
/*    */   private long timeoutsCount;
/*    */   private long filledCount;
/*    */   private long rejectedCount;
/*    */   private long matchedCount;
/*    */   private long notMatchedCount;
/*    */ 
/*    */   public ProxyStatisticsEntry()
/*    */   {
/* 20 */     this.timeoutsCount = 0L;
/* 21 */     this.filledCount = 0L;
/* 22 */     this.rejectedCount = 0L;
/* 23 */     this.matchedCount = 0L;
/* 24 */     this.notMatchedCount = 0L;
/*    */   }
/*    */ 
/*    */   public long getTimeoutsCount() {
/* 28 */     return this.timeoutsCount;
/*    */   }
/*    */ 
/*    */   public void setTimeoutsCount(long timeoutsCount) {
/* 32 */     this.timeoutsCount = timeoutsCount;
/*    */   }
/*    */ 
/*    */   public long getFilledCount() {
/* 36 */     return this.filledCount;
/*    */   }
/*    */ 
/*    */   public void setFilledCount(long filledCount) {
/* 40 */     this.filledCount = filledCount;
/*    */   }
/*    */ 
/*    */   public long getRejectedCount() {
/* 44 */     return this.rejectedCount;
/*    */   }
/*    */ 
/*    */   public void setRejectedCount(long rejectedCount) {
/* 48 */     this.rejectedCount = rejectedCount;
/*    */   }
/*    */ 
/*    */   public long getMatchedCount() {
/* 52 */     return this.matchedCount;
/*    */   }
/*    */ 
/*    */   public void setMatchedCount(long matchedCount) {
/* 56 */     this.matchedCount = matchedCount;
/*    */   }
/*    */ 
/*    */   public long getNotMatchedCount() {
/* 60 */     return this.notMatchedCount;
/*    */   }
/*    */ 
/*    */   public void setNotMatchedCount(long notMatchedCount) {
/* 64 */     this.notMatchedCount = notMatchedCount;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.ProxyStatisticsEntry
 * JD-Core Version:    0.6.0
 */