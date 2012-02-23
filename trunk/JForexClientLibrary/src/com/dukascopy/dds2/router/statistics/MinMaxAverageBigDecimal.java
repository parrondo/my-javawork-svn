/*    */ package com.dukascopy.dds2.router.statistics;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.MathContext;
/*    */ 
/*    */ public class MinMaxAverageBigDecimal
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = 0L;
/* 11 */   private BigDecimal minimum = new BigDecimal(9223372036854775807L);
/*    */ 
/* 13 */   private BigDecimal maximum = new BigDecimal(0);
/*    */ 
/* 15 */   private BigDecimal sum = new BigDecimal(0);
/*    */ 
/* 17 */   private long count = 0L;
/*    */ 
/*    */   public MinMaxAverageBigDecimal() {
/* 20 */     this.minimum = new BigDecimal(9223372036854775807L);
/* 21 */     this.maximum = new BigDecimal(0);
/* 22 */     this.sum = new BigDecimal(0);
/* 23 */     this.count = 0L;
/*    */   }
/*    */ 
/*    */   public MinMaxAverageBigDecimal(BigDecimal minimum, BigDecimal maximum, BigDecimal sum, long count) {
/* 27 */     this.minimum = new BigDecimal(minimum.doubleValue());
/* 28 */     this.maximum = new BigDecimal(maximum.doubleValue());
/* 29 */     this.sum = new BigDecimal(sum.doubleValue());
/* 30 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public BigDecimal getMinimum() {
/* 34 */     return this.minimum;
/*    */   }
/*    */ 
/*    */   public void setMinimum(BigDecimal minimum) {
/* 38 */     this.minimum = new BigDecimal(minimum.doubleValue());
/*    */   }
/*    */ 
/*    */   public BigDecimal getMaximum() {
/* 42 */     return this.maximum;
/*    */   }
/*    */ 
/*    */   public void setMaximum(BigDecimal maximum) {
/* 46 */     this.maximum = new BigDecimal(maximum.doubleValue());
/*    */   }
/*    */ 
/*    */   public BigDecimal getSum() {
/* 50 */     return this.sum;
/*    */   }
/*    */ 
/*    */   public void setSum(BigDecimal sum) {
/* 54 */     this.sum = new BigDecimal(sum.doubleValue());
/*    */   }
/*    */ 
/*    */   public long getCount() {
/* 58 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void setCount(long count) {
/* 62 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public BigDecimal getAverage() {
/* 66 */     if (this.count == 0L) {
/* 67 */       return new BigDecimal(0);
/*    */     }
/* 69 */     return this.sum.divide(new BigDecimal(this.count), MathContext.DECIMAL32);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.router.statistics.MinMaxAverageBigDecimal
 * JD-Core Version:    0.6.0
 */