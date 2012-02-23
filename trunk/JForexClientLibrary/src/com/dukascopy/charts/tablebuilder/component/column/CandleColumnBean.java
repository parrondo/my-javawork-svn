/*    */ package com.dukascopy.charts.tablebuilder.component.column;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public enum CandleColumnBean
/*    */ {
/* 15 */   TIME, 
/*    */ 
/* 21 */   HIGH, 
/*    */ 
/* 27 */   LOW, 
/*    */ 
/* 33 */   OPEN, 
/*    */ 
/* 39 */   CLOSE, 
/*    */ 
/* 45 */   VOLUME;
/*    */ 
/*    */   protected static final DecimalFormat formatter;
/*    */ 
/*    */   public static Object getValue(CandleColumnBean column, CandleData candleData)
/*    */   {
/* 55 */     switch (1.$SwitchMap$com$dukascopy$charts$tablebuilder$component$column$CandleColumnBean[column.ordinal()]) { case 1:
/* 56 */       return formatter.format(candleData.getOpen());
/*    */     case 2:
/* 57 */       return formatter.format(candleData.getLow());
/*    */     case 3:
/* 58 */       return formatter.format(candleData.getHigh());
/*    */     case 4:
/* 59 */       return formatter.format(candleData.getClose());
/*    */     case 5:
/* 60 */       return formatter.format(candleData.getVolume());
/*    */     case 6:
/* 61 */       return new Date(candleData.getTime());
/*    */     }
/* 63 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 52 */     formatter = new DecimalFormat("0.######");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.column.CandleColumnBean
 * JD-Core Version:    0.6.0
 */