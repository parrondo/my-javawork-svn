/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public enum PeriodType
/*    */ {
/*  7 */   Tick("TICK"), 
/*  8 */   Ticks("label.caption.trade.bars"), 
/*  9 */   Seconds("label.caption.seconds"), 
/* 10 */   Minutes("label.caption.minutes"), 
/* 11 */   Hours("label.caption.hours"), 
/* 12 */   Days("label.caption.days"), 
/* 13 */   Weeks("label.caption.weeks"), 
/* 14 */   Months("label.caption.months"), 
/* 15 */   Range("PRICE_RANGE_AGGREGATION"), 
/* 16 */   PF("label.caption.point.and.figure");
/*    */ 
/*    */   private String caption;
/*    */ 
/* 21 */   private PeriodType(String caption) { this.caption = caption; }
/*    */ 
/*    */   public String getCaption()
/*    */   {
/* 25 */     return LocalizationManager.getText(this.caption);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 30 */     return LocalizationManager.getText(this.caption);
/*    */   }
/*    */ 
/*    */   public static PeriodType[] getHSTvalues() {
/* 34 */     return new PeriodType[] { Minutes, Hours, Days, Weeks, Months };
/*    */   }
/*    */ 
/*    */   public boolean isHSTCompatible()
/*    */   {
/* 45 */     return (this == Minutes) || (this == Hours) || (this == Days) || (this == Weeks) || (this == Months);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.PeriodType
 * JD-Core Version:    0.6.0
 */