/*    */ package com.dukascopy.charts.tablebuilder;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.TickBarColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.TickBarDataTablePresentationModel;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.TickBarDataTablePresentationJTable;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ 
/*    */ public class TickBarDataTablePresentationManager extends AbstractDataTablePresentationManager<TickBarData, TickBarDataSequence>
/*    */ {
/*    */   private ChartState chartState;
/*    */   private TickBarDataTablePresentationJTable tickBarDataTablePresentationJTable;
/*    */   private TickBarDataTablePresentationModel tickBarDataTablePresentationModel;
/*    */ 
/*    */   public TickBarDataTablePresentationManager(ChartState chartState, AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> dataSequenceProvider)
/*    */   {
/* 23 */     super(dataSequenceProvider);
/*    */ 
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   protected TickBarDataTablePresentationModel getTableModel()
/*    */   {
/* 30 */     return getTickBarDataTablePresentationModel();
/*    */   }
/*    */ 
/*    */   public TickBarDataTablePresentationJTable getDataPresentationTable()
/*    */   {
/* 35 */     return getTickBarDataTablePresentationJTable();
/*    */   }
/*    */ 
/*    */   private TickBarDataTablePresentationJTable getTickBarDataTablePresentationJTable() {
/* 39 */     if (this.tickBarDataTablePresentationJTable == null) {
/* 40 */       this.tickBarDataTablePresentationJTable = new TickBarDataTablePresentationJTable(this.chartState, getTickBarDataTablePresentationModel());
/*    */     }
/* 42 */     return this.tickBarDataTablePresentationJTable;
/*    */   }
/*    */ 
/*    */   private TickBarDataTablePresentationModel getTickBarDataTablePresentationModel() {
/* 46 */     if (this.tickBarDataTablePresentationModel == null) {
/* 47 */       this.tickBarDataTablePresentationModel = new TickBarDataTablePresentationModel();
/*    */     }
/* 49 */     return this.tickBarDataTablePresentationModel;
/*    */   }
/*    */ 
/*    */   protected boolean matched(TickBarData data, String quickFilter)
/*    */   {
/* 54 */     for (TickBarColumnBean pointAndFigureColumnBean : TickBarColumnBean.values()) {
/* 55 */       Object value = TickBarColumnBean.getValue(pointAndFigureColumnBean, data);
/* 56 */       String str = getTickBarDataTablePresentationJTable().getRenderer().getRenderedText(value);
/* 57 */       if (str.toLowerCase().contains(quickFilter)) {
/* 58 */         return true;
/*    */       }
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.TickBarDataTablePresentationManager
 * JD-Core Version:    0.6.0
 */