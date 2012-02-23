/*    */ package com.dukascopy.charts.tablebuilder;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.CandleColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.CandleDataTablePresentationModel;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.CandleDataTablePresentationJTable;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ 
/*    */ public class CandleDataTablePresentationManager extends AbstractDataTablePresentationManager<CandleData, CandleDataSequence>
/*    */ {
/*    */   private CandleDataTablePresentationJTable candleDataTablePresentationJTable;
/*    */   private CandleDataTablePresentationModel candleDataTablePresentationModel;
/*    */   private ChartState chartState;
/*    */ 
/*    */   public CandleDataTablePresentationManager(ChartState chartState, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider)
/*    */   {
/* 23 */     super(dataSequenceProvider);
/*    */ 
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   protected CandleDataTablePresentationModel getTableModel()
/*    */   {
/* 30 */     return getCandleDataTablePresentationModel();
/*    */   }
/*    */ 
/*    */   public CandleDataTablePresentationJTable getDataPresentationTable()
/*    */   {
/* 35 */     return getCandleDataTablePresentationJTable();
/*    */   }
/*    */ 
/*    */   private CandleDataTablePresentationJTable getCandleDataTablePresentationJTable() {
/* 39 */     if (this.candleDataTablePresentationJTable == null) {
/* 40 */       this.candleDataTablePresentationJTable = new CandleDataTablePresentationJTable(this.chartState, getCandleDataTablePresentationModel());
/*    */     }
/* 42 */     return this.candleDataTablePresentationJTable;
/*    */   }
/*    */ 
/*    */   private CandleDataTablePresentationModel getCandleDataTablePresentationModel() {
/* 46 */     if (this.candleDataTablePresentationModel == null) {
/* 47 */       this.candleDataTablePresentationModel = new CandleDataTablePresentationModel();
/*    */     }
/* 49 */     return this.candleDataTablePresentationModel;
/*    */   }
/*    */ 
/*    */   protected boolean matched(CandleData data, String quickFilter)
/*    */   {
/* 54 */     for (CandleColumnBean candleColumnBean : CandleColumnBean.values()) {
/* 55 */       Object value = CandleColumnBean.getValue(candleColumnBean, data);
/* 56 */       String str = getCandleDataTablePresentationJTable().getRenderer().getRenderedText(value);
/* 57 */       if (str.toLowerCase().contains(quickFilter)) {
/* 58 */         return true;
/*    */       }
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.CandleDataTablePresentationManager
 * JD-Core Version:    0.6.0
 */