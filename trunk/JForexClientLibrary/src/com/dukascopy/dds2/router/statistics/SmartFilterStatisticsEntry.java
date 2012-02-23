/*    */ package com.dukascopy.dds2.router.statistics;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class SmartFilterStatisticsEntry
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 0L;
/*    */   private long filteredAsksCount;
/*    */   private long notFilteredAsksCount;
/*    */   private long filteredBidsCount;
/*    */   private long notFilteredBidsCount;
/*    */ 
/*    */   public SmartFilterStatisticsEntry()
/*    */   {
/* 18 */     this.filteredAsksCount = 0L;
/* 19 */     this.notFilteredAsksCount = 0L;
/* 20 */     this.filteredBidsCount = 0L;
/* 21 */     this.notFilteredBidsCount = 0L;
/*    */   }
/*    */ 
/*    */   public long getFilteredAsksCount() {
/* 25 */     return this.filteredAsksCount;
/*    */   }
/*    */ 
/*    */   public void setFilteredAsksCount(long filteredAsksCount) {
/* 29 */     this.filteredAsksCount = filteredAsksCount;
/*    */   }
/*    */ 
/*    */   public long getNotFilteredAsksCount() {
/* 33 */     return this.notFilteredAsksCount;
/*    */   }
/*    */ 
/*    */   public void setNotFilteredAsksCount(long notFilteredAsksCount) {
/* 37 */     this.notFilteredAsksCount = notFilteredAsksCount;
/*    */   }
/*    */ 
/*    */   public long getFilteredBidsCount() {
/* 41 */     return this.filteredBidsCount;
/*    */   }
/*    */ 
/*    */   public void setFilteredBidsCount(long filtereBidsCount) {
/* 45 */     this.filteredBidsCount = filtereBidsCount;
/*    */   }
/*    */ 
/*    */   public long getNotFilteredBidsCount() {
/* 49 */     return this.notFilteredBidsCount;
/*    */   }
/*    */ 
/*    */   public void setNotFilteredBidsCount(long notFilteredBidsCount) {
/* 53 */     this.notFilteredBidsCount = notFilteredBidsCount;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.SmartFilterStatisticsEntry
 * JD-Core Version:    0.6.0
 */