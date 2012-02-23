/*    */ package com.dukascopy.charts.utils.formatter;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ 
/*    */ public final class FormattersManager
/*    */ {
/*    */   final ValueFormatter valueFormatter;
/*    */   final DateFormatter dateFormatter;
/*    */ 
/*    */   public FormattersManager(ChartState chartState)
/*    */   {
/* 11 */     this.valueFormatter = new ValueFormatter(chartState);
/* 12 */     this.dateFormatter = new DateFormatter(chartState);
/*    */   }
/*    */ 
/*    */   public DateFormatter getDateFormatter() {
/* 16 */     return this.dateFormatter;
/*    */   }
/*    */ 
/*    */   public ValueFormatter getValueFormatter() {
/* 20 */     return this.valueFormatter;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.utils.formatter.FormattersManager
 * JD-Core Version:    0.6.0
 */