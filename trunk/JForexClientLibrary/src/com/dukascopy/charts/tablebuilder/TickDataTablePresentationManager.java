/*    */ package com.dukascopy.charts.tablebuilder;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.TickColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.TickDataTablePresentationModel;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.TickDataTablePresentationJTable;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ 
/*    */ public class TickDataTablePresentationManager extends AbstractDataTablePresentationManager<TickData, TickDataSequence>
/*    */ {
/*    */   private ChartState chartState;
/*    */   private TickDataTablePresentationJTable tickDataTablePresentationJTable;
/*    */   private TickDataTablePresentationModel tickDataTablePresentationModel;
/*    */ 
/*    */   public TickDataTablePresentationManager(ChartState chartState, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider)
/*    */   {
/* 23 */     super(dataSequenceProvider);
/*    */ 
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   protected TickDataTablePresentationModel getTableModel()
/*    */   {
/* 30 */     return getTickDataTablePresentationModel();
/*    */   }
/*    */ 
/*    */   public TickDataTablePresentationJTable getDataPresentationTable()
/*    */   {
/* 35 */     return getTickDataTablePresentationJTable();
/*    */   }
/*    */ 
/*    */   private TickDataTablePresentationJTable getTickDataTablePresentationJTable() {
/* 39 */     if (this.tickDataTablePresentationJTable == null) {
/* 40 */       this.tickDataTablePresentationJTable = new TickDataTablePresentationJTable(this.chartState, getTickDataTablePresentationModel());
/*    */     }
/* 42 */     return this.tickDataTablePresentationJTable;
/*    */   }
/*    */ 
/*    */   private TickDataTablePresentationModel getTickDataTablePresentationModel() {
/* 46 */     if (this.tickDataTablePresentationModel == null) {
/* 47 */       this.tickDataTablePresentationModel = new TickDataTablePresentationModel();
/*    */     }
/* 49 */     return this.tickDataTablePresentationModel;
/*    */   }
/*    */ 
/*    */   protected boolean matched(TickData data, String quickFilter)
/*    */   {
/* 54 */     for (TickColumnBean tickColumnBean : TickColumnBean.values()) {
/* 55 */       Object value = TickColumnBean.getValue(tickColumnBean, data);
/* 56 */       String str = getTickDataTablePresentationJTable().getRenderer().getRenderedText(value);
/* 57 */       if (str.toLowerCase().contains(quickFilter)) {
/* 58 */         return true;
/*    */       }
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.TickDataTablePresentationManager
 * JD-Core Version:    0.6.0
 */