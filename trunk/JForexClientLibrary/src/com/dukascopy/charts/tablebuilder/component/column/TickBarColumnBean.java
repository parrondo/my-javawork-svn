/*    */ package com.dukascopy.charts.tablebuilder.component.column;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public enum TickBarColumnBean
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
/* 57 */   TICK_OR_CANDLES_COUNT;
/*    */ 
/*    */   protected static final DecimalFormat formatter;
/*    */ 
/*    */   public static Object getValue(TickBarColumnBean column, TickBarData tickBarData)
/*    */   {
/* 71 */     switch (1.$SwitchMap$com$dukascopy$charts$tablebuilder$component$column$TickBarColumnBean[column.ordinal()]) { case 1:
/* 72 */       return formatter.format(tickBarData.getHigh());
/*    */     case 2:
/* 73 */       return formatter.format(tickBarData.getLow());
/*    */     case 3:
/* 74 */       return formatter.format(tickBarData.getOpen());
/*    */     case 4:
/* 75 */       return formatter.format(tickBarData.getClose());
/*    */     case 5:
/* 76 */       return formatter.format(tickBarData.getVolume());
/*    */     case 6:
/* 77 */       return new Long(tickBarData.getFormedElementsCount());
/*    */     case 7:
/* 78 */       return new Date(tickBarData.getTime());
/*    */     case 8:
/* 79 */       return new Date(tickBarData.getEndTime());
/*    */     }
/* 81 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 64 */     formatter = new DecimalFormat("0.######");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.column.TickBarColumnBean
 * JD-Core Version:    0.6.0
 */