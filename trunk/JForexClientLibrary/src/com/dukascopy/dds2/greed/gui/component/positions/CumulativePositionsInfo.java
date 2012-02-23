/*    */ package com.dukascopy.dds2.greed.gui.component.positions;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class CumulativePositionsInfo
/*    */ {
/*    */   private int count;
/*    */   private int countShort;
/*    */   private int countLong;
/*    */   private BigDecimal amount;
/*    */   private BigDecimal profitLoss;
/*    */ 
/*    */   public CumulativePositionsInfo(int count, int countShort, int countLong, BigDecimal amount, BigDecimal profitLoss)
/*    */   {
/* 21 */     this.count = count;
/* 22 */     this.countShort = countShort;
/* 23 */     this.countLong = countLong;
/* 24 */     this.amount = amount;
/* 25 */     this.profitLoss = profitLoss;
/*    */   }
/*    */   public int getCount() {
/* 28 */     return this.count; } 
/* 29 */   public int getCountShort() { return this.countShort; } 
/* 30 */   public int getCountLong() { return this.countLong; } 
/* 31 */   public BigDecimal getAmount() { return this.amount; } 
/* 32 */   public BigDecimal getProfitLoss() { return this.profitLoss;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.positions.CumulativePositionsInfo
 * JD-Core Version:    0.6.0
 */