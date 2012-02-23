/*    */ package com.dukascopy.charts.tablebuilder;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.RenkoColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.RenkoTablePresentationModel;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.RenkoTablePresentationJTable;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ 
/*    */ public class RenkoTablePresentationManager extends AbstractDataTablePresentationManager<RenkoData, RenkoDataSequence>
/*    */ {
/*    */   private ChartState chartState;
/*    */   private RenkoTablePresentationJTable renkoTablePresentationJTable;
/*    */   private RenkoTablePresentationModel renkoTablePresentationModel;
/*    */ 
/*    */   public RenkoTablePresentationManager(ChartState chartState, AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> dataSequenceProvider)
/*    */   {
/* 23 */     super(dataSequenceProvider);
/*    */ 
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   protected RenkoTablePresentationModel getTableModel()
/*    */   {
/* 30 */     return getRenkoTablePresentationModel();
/*    */   }
/*    */ 
/*    */   public RenkoTablePresentationJTable getDataPresentationTable()
/*    */   {
/* 35 */     return getRenkoTablePresentationJTable();
/*    */   }
/*    */ 
/*    */   private RenkoTablePresentationJTable getRenkoTablePresentationJTable() {
/* 39 */     if (this.renkoTablePresentationJTable == null) {
/* 40 */       this.renkoTablePresentationJTable = new RenkoTablePresentationJTable(this.chartState, getRenkoTablePresentationModel());
/*    */     }
/* 42 */     return this.renkoTablePresentationJTable;
/*    */   }
/*    */ 
/*    */   private RenkoTablePresentationModel getRenkoTablePresentationModel() {
/* 46 */     if (this.renkoTablePresentationModel == null) {
/* 47 */       this.renkoTablePresentationModel = new RenkoTablePresentationModel();
/*    */     }
/* 49 */     return this.renkoTablePresentationModel;
/*    */   }
/*    */ 
/*    */   protected boolean matched(RenkoData data, String quickFilter)
/*    */   {
/* 54 */     for (RenkoColumnBean renkoColumnBean : RenkoColumnBean.values()) {
/* 55 */       Object value = RenkoColumnBean.getValue(renkoColumnBean, data);
/* 56 */       String str = getRenkoTablePresentationJTable().getRenderer().getRenderedText(value);
/* 57 */       if (str.toLowerCase().contains(quickFilter)) {
/* 58 */         return true;
/*    */       }
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.RenkoTablePresentationManager
 * JD-Core Version:    0.6.0
 */