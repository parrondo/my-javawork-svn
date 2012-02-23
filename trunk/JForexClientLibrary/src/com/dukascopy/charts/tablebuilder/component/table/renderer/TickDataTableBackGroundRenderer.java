/*    */ package com.dukascopy.charts.tablebuilder.component.table.renderer;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import java.awt.Component;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JTable;
/*    */ 
/*    */ public class TickDataTableBackGroundRenderer extends AbstractDataTableBackgroundRenderer
/*    */ {
/* 23 */   private static final long serialVersionUID = 1L;
/* 23 */   protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat() { private static final long serialVersionUID = 1L; } ;
/*    */ 
/*    */   public TickDataTableBackGroundRenderer(ChartState chartState)
/*    */   {
/* 20 */     super(chartState);
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 38 */     Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 39 */     return comp;
/*    */   }
/*    */ 
/*    */   protected SimpleDateFormat getDateFormat()
/*    */   {
/* 45 */     return DATE_FORMATTER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.renderer.TickDataTableBackGroundRenderer
 * JD-Core Version:    0.6.0
 */