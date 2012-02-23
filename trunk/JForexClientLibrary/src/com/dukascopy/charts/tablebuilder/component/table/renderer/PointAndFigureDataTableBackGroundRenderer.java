/*    */ package com.dukascopy.charts.tablebuilder.component.table.renderer;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.DataTablePresentationAbstractModel;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JTable;
/*    */ 
/*    */ public class PointAndFigureDataTableBackGroundRenderer extends CandleDataTableBackGroundRenderer
/*    */ {
/* 26 */   private static final long serialVersionUID = 1L;
/* 26 */   protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat() { private static final long serialVersionUID = 1L; } ;
/*    */ 
/*    */   public PointAndFigureDataTableBackGroundRenderer(ChartState chartState)
/*    */   {
/* 22 */     super(chartState);
/*    */   }
/*    */ 
/*    */   protected Boolean detectTrend(JTable table, int row)
/*    */   {
/* 39 */     Object object = ((DataTablePresentationAbstractModel)table.getModel()).get(row);
/* 40 */     if ((object instanceof PointAndFigureData)) {
/* 41 */       PointAndFigureData data = (PointAndFigureData)object;
/* 42 */       return data.isRising();
/*    */     }
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   protected SimpleDateFormat getDateFormat()
/*    */   {
/* 50 */     return DATE_FORMATTER;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.renderer.PointAndFigureDataTableBackGroundRenderer
 * JD-Core Version:    0.6.0
 */