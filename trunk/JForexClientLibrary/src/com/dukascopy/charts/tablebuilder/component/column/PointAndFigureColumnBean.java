/*    */ package com.dukascopy.charts.tablebuilder.component.column;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public enum PointAndFigureColumnBean
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
/*    */   public static Object getValue(PointAndFigureColumnBean column, PointAndFigureData pointAndFigureData)
/*    */   {
/* 71 */     switch (1.$SwitchMap$com$dukascopy$charts$tablebuilder$component$column$PointAndFigureColumnBean[column.ordinal()]) { case 1:
/* 72 */       return formatter.format(pointAndFigureData.getHigh());
/*    */     case 2:
/* 73 */       return formatter.format(pointAndFigureData.getLow());
/*    */     case 3:
/* 74 */       return formatter.format(pointAndFigureData.getOpen());
/*    */     case 4:
/* 75 */       return formatter.format(pointAndFigureData.getClose());
/*    */     case 5:
/* 76 */       return formatter.format(pointAndFigureData.getVolume());
/*    */     case 6:
/* 77 */       return new Long(pointAndFigureData.getFormedElementsCount());
/*    */     case 7:
/* 78 */       return new Date(pointAndFigureData.getTime());
/*    */     case 8:
/* 79 */       return new Date(pointAndFigureData.getEndTime());
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
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.column.PointAndFigureColumnBean
 * JD-Core Version:    0.6.0
 */