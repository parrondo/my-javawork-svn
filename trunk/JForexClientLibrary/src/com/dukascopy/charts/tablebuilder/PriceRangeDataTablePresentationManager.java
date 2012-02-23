/*    */ package com.dukascopy.charts.tablebuilder;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.PriceRangeColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.PriceRangeDataTablePresentationModel;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.PriceRangeDataTablePresentationJTable;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ 
/*    */ public class PriceRangeDataTablePresentationManager extends AbstractDataTablePresentationManager<PriceRangeData, PriceRangeDataSequence>
/*    */ {
/*    */   private ChartState chartState;
/*    */   private PriceRangeDataTablePresentationJTable priceRangeDataTablePresentationJTable;
/*    */   private PriceRangeDataTablePresentationModel priceRangeDataTablePresentationModel;
/*    */ 
/*    */   public PriceRangeDataTablePresentationManager(ChartState chartState, AbstractDataSequenceProvider<PriceRangeDataSequence, PriceRangeData> dataSequenceProvider)
/*    */   {
/* 23 */     super(dataSequenceProvider);
/*    */ 
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   protected PriceRangeDataTablePresentationModel getTableModel()
/*    */   {
/* 30 */     return getPriceRangeDataTablePresentationModel();
/*    */   }
/*    */ 
/*    */   public PriceRangeDataTablePresentationJTable getDataPresentationTable()
/*    */   {
/* 35 */     return getPriceRangeDataTablePresentationJTable();
/*    */   }
/*    */ 
/*    */   private PriceRangeDataTablePresentationJTable getPriceRangeDataTablePresentationJTable() {
/* 39 */     if (this.priceRangeDataTablePresentationJTable == null) {
/* 40 */       this.priceRangeDataTablePresentationJTable = new PriceRangeDataTablePresentationJTable(this.chartState, getPriceRangeDataTablePresentationModel());
/*    */     }
/* 42 */     return this.priceRangeDataTablePresentationJTable;
/*    */   }
/*    */ 
/*    */   private PriceRangeDataTablePresentationModel getPriceRangeDataTablePresentationModel() {
/* 46 */     if (this.priceRangeDataTablePresentationModel == null) {
/* 47 */       this.priceRangeDataTablePresentationModel = new PriceRangeDataTablePresentationModel();
/*    */     }
/* 49 */     return this.priceRangeDataTablePresentationModel;
/*    */   }
/*    */ 
/*    */   protected boolean matched(PriceRangeData data, String quickFilter)
/*    */   {
/* 54 */     for (PriceRangeColumnBean priceRangeColumnBean : PriceRangeColumnBean.values()) {
/* 55 */       Object value = PriceRangeColumnBean.getValue(priceRangeColumnBean, data);
/* 56 */       String str = getPriceRangeDataTablePresentationJTable().getRenderer().getRenderedText(value);
/* 57 */       if (str.toLowerCase().contains(quickFilter)) {
/* 58 */         return true;
/*    */       }
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.PriceRangeDataTablePresentationManager
 * JD-Core Version:    0.6.0
 */