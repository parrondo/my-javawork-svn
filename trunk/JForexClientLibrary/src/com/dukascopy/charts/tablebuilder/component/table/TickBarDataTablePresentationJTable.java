/*    */ package com.dukascopy.charts.tablebuilder.component.table;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.TickBarColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.TickBarDataTableBackGroundRenderer;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class TickBarDataTablePresentationJTable extends DataTablePresentationAbstractJTable<TickBarColumnBean, TickBarData>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ChartState chartState;
/*    */   private AbstractDataTableBackgroundRenderer renderer;
/*    */ 
/*    */   public TickBarDataTablePresentationJTable(ChartState chartState, JLocalizableAnnotatedTableModel<TickBarColumnBean, TickBarData> tableModel)
/*    */   {
/* 25 */     super(tableModel);
/*    */ 
/* 27 */     this.chartState = chartState;
/*    */ 
/* 29 */     setDefaultRenderer(String.class, getRenderer());
/* 30 */     setDefaultRenderer(Date.class, getRenderer());
/*    */   }
/*    */ 
/*    */   public AbstractDataTableBackgroundRenderer getRenderer()
/*    */   {
/* 35 */     if (this.renderer == null) {
/* 36 */       this.renderer = new TickBarDataTableBackGroundRenderer(this.chartState);
/*    */     }
/* 38 */     return this.renderer;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.TickBarDataTablePresentationJTable
 * JD-Core Version:    0.6.0
 */