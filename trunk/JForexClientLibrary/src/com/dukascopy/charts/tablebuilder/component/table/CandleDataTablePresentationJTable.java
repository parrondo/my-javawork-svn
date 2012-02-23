/*    */ package com.dukascopy.charts.tablebuilder.component.table;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.CandleColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.CandleDataTableBackGroundRenderer;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class CandleDataTablePresentationJTable extends DataTablePresentationAbstractJTable<CandleColumnBean, CandleData>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ChartState chartState;
/*    */   private AbstractDataTableBackgroundRenderer renderer;
/*    */ 
/*    */   public CandleDataTablePresentationJTable(ChartState chartState, JLocalizableAnnotatedTableModel<CandleColumnBean, CandleData> tableModel)
/*    */   {
/* 25 */     super(tableModel);
/* 26 */     this.chartState = chartState;
/*    */ 
/* 28 */     setDefaultRenderer(String.class, getRenderer());
/* 29 */     setDefaultRenderer(Date.class, getRenderer());
/*    */   }
/*    */ 
/*    */   public AbstractDataTableBackgroundRenderer getRenderer()
/*    */   {
/* 34 */     if (this.renderer == null) {
/* 35 */       this.renderer = new CandleDataTableBackGroundRenderer(this.chartState);
/*    */     }
/* 37 */     return this.renderer;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.CandleDataTablePresentationJTable
 * JD-Core Version:    0.6.0
 */