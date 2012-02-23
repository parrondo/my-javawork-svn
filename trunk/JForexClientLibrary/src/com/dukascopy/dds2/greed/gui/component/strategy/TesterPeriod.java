/*    */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*    */ 
/*    */ import com.dukascopy.api.Period;
/*    */ 
/*    */ public class TesterPeriod
/*    */ {
/*    */   private Period period;
/*    */ 
/*    */   public TesterPeriod(Period period)
/*    */   {
/* 10 */     if (period == null) {
/* 11 */       throw new NullPointerException("Period cannot be null!");
/*    */     }
/* 13 */     this.period = period;
/*    */   }
/*    */ 
/*    */   public Period getPeriod() {
/* 17 */     return this.period;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 23 */     if ((obj instanceof TesterPeriod))
/*    */     {
/* 25 */       Period otherPeriod = ((TesterPeriod)obj).getPeriod();
/* 26 */       return this.period == otherPeriod;
/*    */     }
/*    */ 
/* 29 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterPeriod
 * JD-Core Version:    0.6.0
 */