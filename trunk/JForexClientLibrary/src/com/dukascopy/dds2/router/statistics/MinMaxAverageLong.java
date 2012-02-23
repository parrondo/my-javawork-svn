/*    */ package com.dukascopy.dds2.router.statistics;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class MinMaxAverageLong
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 0L;
/*  9 */   private long minimum = 9223372036854775807L;
/*    */ 
/* 11 */   private long maximum = 0L;
/*    */ 
/* 13 */   private long sum = 0L;
/*    */ 
/* 15 */   private long count = 0L;
/*    */ 
/*    */   public MinMaxAverageLong() {
/* 18 */     this.minimum = 9223372036854775807L;
/* 19 */     this.maximum = 0L;
/* 20 */     this.sum = 0L;
/* 21 */     this.count = 0L;
/*    */   }
/*    */ 
/*    */   public MinMaxAverageLong(long minimum, long maximum, long sum, long count) {
/* 25 */     this.minimum = minimum;
/* 26 */     this.maximum = maximum;
/* 27 */     this.sum = sum;
/* 28 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public long getMinimum() {
/* 32 */     return this.minimum;
/*    */   }
/*    */ 
/*    */   public void setMinimum(long minimum) {
/* 36 */     this.minimum = minimum;
/*    */   }
/*    */ 
/*    */   public long getMaximum() {
/* 40 */     return this.maximum;
/*    */   }
/*    */ 
/*    */   public void setMaximum(long maximum) {
/* 44 */     this.maximum = maximum;
/*    */   }
/*    */ 
/*    */   public long getSum() {
/* 48 */     return this.sum;
/*    */   }
/*    */ 
/*    */   public void setSum(long sum) {
/* 52 */     this.sum = sum;
/*    */   }
/*    */ 
/*    */   public long getCount() {
/* 56 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void setCount(long count) {
/* 60 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public long getAverage() {
/* 64 */     if (this.count == 0L) {
/* 65 */       return 0L;
/*    */     }
/* 67 */     return this.sum / this.count;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.MinMaxAverageLong
 * JD-Core Version:    0.6.0
 */