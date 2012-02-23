/*    */ package com.dukascopy.charts.utils;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.TickBarSize;
/*    */ import com.dukascopy.api.Unit;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ 
/*    */ public class ChartsLocalizator
/*    */ {
/*    */   public static String localize(JForexPeriod jForexPeriod)
/*    */   {
/* 22 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[jForexPeriod.getDataType().ordinal()])
/*    */     {
/*    */     case 1:
/* 25 */       return LocalizationManager.getText(jForexPeriod.getDataType().toString());
/*    */     case 2:
/* 28 */       if (jForexPeriod.getPriceRange() == null) {
/* 29 */         return LocalizationManager.getText(jForexPeriod.getDataType().toString());
/*    */       }
/* 31 */       return getLocalized(jForexPeriod.getPriceRange());
/*    */     case 3:
/* 34 */       return "P&F(" + getLocalized(jForexPeriod.getPriceRange()) + " x " + jForexPeriod.getReversalAmount() + ")";
/*    */     case 4:
/* 37 */       return getLocalized(jForexPeriod.getTickBarSize());
/*    */     case 5:
/* 40 */       return "Renko " + getLocalized(jForexPeriod.getPriceRange());
/*    */     case 6:
/* 43 */       return getLocalized(jForexPeriod.getPeriod());
/*    */     }
/*    */ 
/* 46 */     throw new IllegalArgumentException("Unsupported data type - " + jForexPeriod.getDataType());
/*    */   }
/*    */ 
/*    */   public static String getLocalized(PriceRange priceRange)
/*    */   {
/* 52 */     if (priceRange == null) {
/* 53 */       return "null";
/*    */     }
/* 55 */     String pips = priceRange.getPipCount() > 1 ? LocalizationManager.getText("price.range.pips") : LocalizationManager.getText("price.range.pip");
/* 56 */     return priceRange.getPipCount() + " " + pips;
/*    */   }
/*    */ 
/*    */   public static String getLocalized(TickBarSize tickBarSize) {
/* 60 */     if (tickBarSize == null) {
/* 61 */       return "null";
/*    */     }
/* 63 */     String ticks = tickBarSize.getSize() > 1 ? LocalizationManager.getText("trade.bar.ticks") : LocalizationManager.getText("trade.bar.tick");
/* 64 */     return tickBarSize.getSize() + " " + ticks;
/*    */   }
/*    */ 
/*    */   public static String getLocalized(Period period) {
/* 68 */     Period basicPeriod = Period.isPeriodBasic(period);
/* 69 */     if (basicPeriod != null)
/* 70 */       return LocalizationManager.getText(basicPeriod.name());
/*    */     String key;
/* 74 */     switch (1.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) { case 1:
/* 75 */       key = period.getNumOfUnits() == 1 ? "custom.period.sec" : "custom.period.secs"; break;
/*    */     case 2:
/* 76 */       key = "custom.period.mins"; break;
/*    */     case 3:
/* 77 */       key = "custom.period.hours"; break;
/*    */     case 4:
/* 78 */       key = "custom.period.days"; break;
/*    */     case 5:
/* 79 */       key = period.getNumOfUnits() == 1 ? "custom.period.week" : "custom.period.weeks"; break;
/*    */     case 6:
/* 80 */       key = period.getNumOfUnits() == 1 ? "custom.period.month" : "custom.period.months"; break;
/*    */     default:
/* 81 */       key = null;
/*    */     }
/*    */ 
/* 84 */     String localizedPeriod = period.getNumOfUnits() + " " + LocalizationManager.getText(key);
/* 85 */     return localizedPeriod;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.ChartsLocalizator
 * JD-Core Version:    0.6.0
 */