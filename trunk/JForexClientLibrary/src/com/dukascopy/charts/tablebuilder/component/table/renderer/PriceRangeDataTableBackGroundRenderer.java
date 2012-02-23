/*    */ package com.dukascopy.charts.tablebuilder.component.table.renderer;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class PriceRangeDataTableBackGroundRenderer extends CandleDataTableBackGroundRenderer
/*    */ {
/* 21 */   private static final long serialVersionUID = 1L;
/* 21 */   protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat() { private static final long serialVersionUID = 1L; } ;
/*    */ 
/*    */   public PriceRangeDataTableBackGroundRenderer(ChartState chartState)
/*    */   {
/* 17 */     super(chartState);
/*    */   }
/*    */ 
/*    */   protected SimpleDateFormat getDateFormat()
/*    */   {
/* 31 */     return DATE_FORMATTER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.renderer.PriceRangeDataTableBackGroundRenderer
 * JD-Core Version:    0.6.0
 */