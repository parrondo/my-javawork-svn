/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.TickBarSize;
/*    */ 
/*    */ public class CompositePeriod
/*    */ {
/*    */   private Type type;
/*    */   private Period period;
/*    */   private TickBarSize tickBarSize;
/*    */ 
/*    */   public CompositePeriod(Type type, Period period)
/*    */   {
/* 17 */     this.type = type;
/* 18 */     this.period = period;
/*    */   }
/*    */ 
/*    */   public CompositePeriod() {
/*    */   }
/*    */ 
/*    */   public Type getType() {
/* 25 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(Type type) {
/* 29 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public Period getPeriod() {
/* 33 */     return this.period;
/*    */   }
/*    */   public void setPeriod(Period period) {
/* 36 */     this.period = period;
/*    */   }
/*    */ 
/*    */   public TickBarSize getTickBarSize() {
/* 40 */     return this.tickBarSize;
/*    */   }
/*    */ 
/*    */   public void setTickBarSize(TickBarSize tickBarSize) {
/* 44 */     this.tickBarSize = tickBarSize;
/*    */   }
/*    */ 
/*    */   public boolean isValid()
/*    */   {
/* 49 */     boolean valid = false;
/*    */ 
/* 51 */     if ((this.type == Type.PERIOD) && (this.period != null)) {
/* 52 */       valid = true;
/*    */     }
/*    */ 
/* 55 */     if ((this.type == Type.TICKBARSIZE) && (this.tickBarSize != null)) {
/* 56 */       valid = true;
/*    */     }
/*    */ 
/* 59 */     return valid;
/*    */   }
/*    */ 
/*    */   public boolean isHSTCompatible() {
/* 63 */     boolean compatible = false;
/*    */ 
/* 65 */     if ((this.type == Type.PERIOD) && (
/* 66 */       (this.period == Period.ONE_MIN) || (this.period == Period.FIVE_MINS) || (this.period == Period.FIFTEEN_MINS) || (this.period == Period.THIRTY_MINS) || (this.period == Period.ONE_HOUR) || (this.period == Period.FOUR_HOURS) || (this.period == Period.DAILY) || (this.period == Period.WEEKLY) || (this.period == Period.MONTHLY)))
/*    */     {
/* 79 */       compatible = true;
/*    */     }
/*    */ 
/* 83 */     return compatible;
/*    */   }
/*    */ 
/*    */   public static enum Type
/*    */   {
/*  8 */     PERIOD, 
/*  9 */     TICKBARSIZE;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.CompositePeriod
 * JD-Core Version:    0.6.0
 */