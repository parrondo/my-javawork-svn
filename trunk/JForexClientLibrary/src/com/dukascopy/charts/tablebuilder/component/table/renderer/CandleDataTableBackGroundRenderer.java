/*    */ package com.dukascopy.charts.tablebuilder.component.table.renderer;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.DataTablePresentationAbstractModel;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JTable;
/*    */ 
/*    */ public class CandleDataTableBackGroundRenderer extends AbstractDataTableBackgroundRenderer
/*    */ {
/* 29 */   private static final long serialVersionUID = 1L;
/* 29 */   protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat() { private static final long serialVersionUID = 1L; } ;
/*    */ 
/*    */   public CandleDataTableBackGroundRenderer(ChartState chartState)
/*    */   {
/* 25 */     super(chartState);
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 45 */     Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*    */ 
/* 47 */     if (hasFocus) {
/* 48 */       return comp;
/*    */     }
/*    */ 
/* 51 */     Color CANDLE_BEAR_ROW = this.chartState.getTheme().getColor(ITheme.ChartElement.CANDLE_BEAR_ROW);
/* 52 */     Color CANDLE_BULL_ROW = this.chartState.getTheme().getColor(ITheme.ChartElement.CANDLE_BULL_ROW);
/*    */ 
/* 54 */     Boolean trend = detectTrend(table, row);
/*    */ 
/* 56 */     if (Boolean.FALSE.equals(trend)) {
/* 57 */       comp.setBackground(CANDLE_BEAR_ROW);
/*    */     }
/* 59 */     else if (Boolean.TRUE.equals(trend)) {
/* 60 */       comp.setBackground(CANDLE_BULL_ROW);
/*    */     }
/*    */ 
/* 63 */     return comp;
/*    */   }
/*    */ 
/*    */   protected Boolean detectTrend(JTable table, int row)
/*    */   {
/* 70 */     Object object = ((DataTablePresentationAbstractModel)table.getModel()).get(row);
/* 71 */     if ((object instanceof CandleData)) {
/* 72 */       CandleData candleData = (CandleData)object;
/*    */ 
/* 74 */       if (candleData.getOpen() > candleData.getClose()) {
/* 75 */         return Boolean.FALSE;
/*    */       }
/* 77 */       if (candleData.getOpen() < candleData.getClose()) {
/* 78 */         return Boolean.TRUE;
/*    */       }
/*    */     }
/*    */ 
/* 82 */     return null;
/*    */   }
/*    */ 
/*    */   protected SimpleDateFormat getDateFormat()
/*    */   {
/* 87 */     return DATE_FORMATTER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.renderer.CandleDataTableBackGroundRenderer
 * JD-Core Version:    0.6.0
 */