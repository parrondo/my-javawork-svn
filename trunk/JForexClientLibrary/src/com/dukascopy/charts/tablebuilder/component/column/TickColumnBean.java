/*    */ package com.dukascopy.charts.tablebuilder.component.column;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.dds2.greed.gui.table.ColumnDescriptor;
/*    */ import java.text.DecimalFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public enum TickColumnBean
/*    */ {
/* 15 */   TIME, 
/*    */ 
/* 21 */   ASK, 
/*    */ 
/* 27 */   BID, 
/*    */ 
/* 33 */   ASK_VOLUME, 
/*    */ 
/* 39 */   BID_VOLUME;
/*    */ 
/*    */   protected static final DecimalFormat formatter;
/*    */ 
/*    */   public static Object getValue(TickColumnBean column, TickData tickData)
/*    */   {
/* 49 */     switch (1.$SwitchMap$com$dukascopy$charts$tablebuilder$component$column$TickColumnBean[column.ordinal()]) { case 1:
/* 50 */       return formatter.format(tickData.getAsk());
/*    */     case 2:
/* 51 */       return formatter.format(tickData.getAskVolume());
/*    */     case 3:
/* 52 */       return formatter.format(tickData.getBid());
/*    */     case 4:
/* 53 */       return formatter.format(tickData.getBidVolume());
/*    */     case 5:
/* 54 */       return new Date(tickData.getTime());
/*    */     }
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 46 */     formatter = new DecimalFormat("0.######");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.column.TickColumnBean
 * JD-Core Version:    0.6.0
 */