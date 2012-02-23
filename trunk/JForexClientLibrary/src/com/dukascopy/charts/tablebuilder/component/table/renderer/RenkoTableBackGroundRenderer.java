/*    */ package com.dukascopy.charts.tablebuilder.component.table.renderer;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.DataTablePresentationAbstractModel;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ import javax.swing.JTable;
/*    */ 
/*    */ public class RenkoTableBackGroundRenderer extends CandleDataTableBackGroundRenderer
/*    */ {
/* 26 */   private static final long serialVersionUID = 1L;
/* 26 */   protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat() { private static final long serialVersionUID = 1L; } ;
/*    */ 
/*    */   public RenkoTableBackGroundRenderer(ChartState chartState)
/*    */   {
/* 22 */     super(chartState);
/*    */   }
/*    */ 
/*    */   protected SimpleDateFormat getDateFormat()
/*    */   {
/* 36 */     return DATE_FORMATTER;
/*    */   }
/*    */ 
/*    */   protected Boolean detectTrend(JTable table, int row)
/*    */   {
/* 44 */     Object object = ((DataTablePresentationAbstractModel)table.getModel()).get(row);
/*    */ 
/* 46 */     int size = ((DataTablePresentationAbstractModel)table.getModel()).getRowCount();
/* 47 */     int previousRow = row + 1;
/*    */ 
/* 49 */     Object previousObject = null;
/*    */ 
/* 51 */     if ((previousRow > 0) && (previousRow < size)) {
/* 52 */       previousObject = ((DataTablePresentationAbstractModel)table.getModel()).get(previousRow);
/*    */     }
/*    */ 
/* 55 */     if ((object instanceof RenkoData)) {
/* 56 */       RenkoData data = (RenkoData)object;
/* 57 */       if ((previousObject instanceof RenkoData)) {
/* 58 */         RenkoData previousData = (RenkoData)previousObject;
/* 59 */         if (previousData.getHigh() <= data.getHigh()) {
/* 60 */           return Boolean.TRUE;
/*    */         }
/*    */ 
/* 63 */         return Boolean.FALSE;
/*    */       }
/*    */ 
/* 67 */       return super.detectTrend(table, row);
/*    */     }
/*    */ 
/* 71 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.renderer.RenkoTableBackGroundRenderer
 * JD-Core Version:    0.6.0
 */