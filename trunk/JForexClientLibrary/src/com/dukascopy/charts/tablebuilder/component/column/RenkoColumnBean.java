/*    */ package com.dukascopy.charts.tablebuilder.component.column;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public enum RenkoColumnBean
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
/*    */   public static Object getValue(RenkoColumnBean column, RenkoData renkoData)
/*    */   {
/* 67 */     switch (1.$SwitchMap$com$dukascopy$charts$tablebuilder$component$column$RenkoColumnBean[column.ordinal()]) { case 1:
/* 68 */       return formatter.format(renkoData.getOpen());
/*    */     case 2:
/* 69 */       return formatter.format(renkoData.getLow());
/*    */     case 3:
/* 70 */       return formatter.format(renkoData.getHigh());
/*    */     case 4:
/* 71 */       return formatter.format(renkoData.getClose());
/*    */     case 5:
/* 72 */       return formatter.format(renkoData.getVolume());
/*    */     case 6:
/* 73 */       return Long.valueOf(renkoData.getFormedElementsCount());
/*    */     case 7:
/* 74 */       return new Date(renkoData.getTime());
/*    */     case 8:
/* 75 */       return new Date(renkoData.getEndTime());
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
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.column.RenkoColumnBean
 * JD-Core Version:    0.6.0
 */