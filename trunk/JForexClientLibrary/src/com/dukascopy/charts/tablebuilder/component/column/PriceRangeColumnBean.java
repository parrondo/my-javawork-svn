/*    */ package com.dukascopy.charts.tablebuilder.component.column;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public enum PriceRangeColumnBean
/*    */ {
/* 15 */   START_TIME, 
/*    */ 
/* 21 */   END_TIME, 
/*    */ 
/* 27 */   HIGH, 
/*    */ 
/* 33 */   LOW, 
/*    */ 
/* 39 */   OPEN, 
/*    */ 
/* 45 */   CLOSE, 
/*    */ 
/* 51 */   VOLUME, 
/*    */ 
/* 57 */   TICK_COUNT;
/*    */ 
/*    */   protected static final DecimalFormat formatter;
/*    */ 
/*    */   public static Object getValue(PriceRangeColumnBean column, PriceRangeData priceRangeData)
/*    */   {
/* 67 */     switch (1.$SwitchMap$com$dukascopy$charts$tablebuilder$component$column$PriceRangeColumnBean[column.ordinal()]) { case 1:
/* 68 */       return formatter.format(priceRangeData.getOpen());
/*    */     case 2:
/* 69 */       return formatter.format(priceRangeData.getLow());
/*    */     case 3:
/* 70 */       return formatter.format(priceRangeData.getHigh());
/*    */     case 4:
/* 71 */       return formatter.format(priceRangeData.getClose());
/*    */     case 5:
/* 72 */       return formatter.format(priceRangeData.getVolume());
/*    */     case 6:
/* 73 */       return Long.valueOf(priceRangeData.getFormedElementsCount());
/*    */     case 7:
/* 74 */       return new Date(priceRangeData.getTime());
/*    */     case 8:
/* 75 */       return new Date(priceRangeData.getEndTime());
/*    */     }
/* 77 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 64 */     formatter = new DecimalFormat("0.######");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.column.PriceRangeColumnBean
 * JD-Core Version:    0.6.0
 */