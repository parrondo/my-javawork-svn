/*    */ package com.dukascopy.charts.tablebuilder.component.table;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.RenkoColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.RenkoTableBackGroundRenderer;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableAnnotatedTableModel;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class RenkoTablePresentationJTable extends DataTablePresentationAbstractJTable<RenkoColumnBean, RenkoData>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ChartState chartState;
/*    */   private AbstractDataTableBackgroundRenderer renderer;
/*    */ 
/*    */   public RenkoTablePresentationJTable(ChartState chartState, JLocalizableAnnotatedTableModel<RenkoColumnBean, RenkoData> tableModel)
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
/* 36 */       this.renderer = new RenkoTableBackGroundRenderer(this.chartState);
/*    */     }
/* 38 */     return this.renderer;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.RenkoTablePresentationJTable
 * JD-Core Version:    0.6.0
 */