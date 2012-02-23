/*    */ package com.dukascopy.charts.tablebuilder;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.PointAndFigureColumnBean;
/*    */ import com.dukascopy.charts.tablebuilder.component.model.PointAndFigureDataTablePresentationModel;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.PointAndFigureDataTablePresentationJTable;
/*    */ import com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer;
/*    */ 
/*    */ public class PointAndFigureDataTablePresentationManager extends AbstractDataTablePresentationManager<PointAndFigureData, PointAndFigureDataSequence>
/*    */ {
/*    */   private ChartState chartState;
/*    */   private PointAndFigureDataTablePresentationJTable pointAndFigureDataTablePresentationJTable;
/*    */   private PointAndFigureDataTablePresentationModel pointAndFigureDataTablePresentationModel;
/*    */ 
/*    */   public PointAndFigureDataTablePresentationManager(ChartState chartState, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider)
/*    */   {
/* 23 */     super(dataSequenceProvider);
/*    */ 
/* 25 */     this.chartState = chartState;
/*    */   }
/*    */ 
/*    */   protected PointAndFigureDataTablePresentationModel getTableModel()
/*    */   {
/* 30 */     return getPointAndFigureDataTablePresentationModel();
/*    */   }
/*    */ 
/*    */   public PointAndFigureDataTablePresentationJTable getDataPresentationTable()
/*    */   {
/* 35 */     return getPointAndFigureDataTablePresentationJTable();
/*    */   }
/*    */ 
/*    */   private PointAndFigureDataTablePresentationJTable getPointAndFigureDataTablePresentationJTable() {
/* 39 */     if (this.pointAndFigureDataTablePresentationJTable == null) {
/* 40 */       this.pointAndFigureDataTablePresentationJTable = new PointAndFigureDataTablePresentationJTable(this.chartState, getPointAndFigureDataTablePresentationModel());
/*    */     }
/* 42 */     return this.pointAndFigureDataTablePresentationJTable;
/*    */   }
/*    */ 
/*    */   private PointAndFigureDataTablePresentationModel getPointAndFigureDataTablePresentationModel() {
/* 46 */     if (this.pointAndFigureDataTablePresentationModel == null) {
/* 47 */       this.pointAndFigureDataTablePresentationModel = new PointAndFigureDataTablePresentationModel();
/*    */     }
/* 49 */     return this.pointAndFigureDataTablePresentationModel;
/*    */   }
/*    */ 
/*    */   protected boolean matched(PointAndFigureData data, String quickFilter)
/*    */   {
/* 54 */     for (PointAndFigureColumnBean pointAndFigureColumnBean : PointAndFigureColumnBean.values()) {
/* 55 */       Object value = PointAndFigureColumnBean.getValue(pointAndFigureColumnBean, data);
/* 56 */       String str = getPointAndFigureDataTablePresentationJTable().getRenderer().getRenderedText(value);
/* 57 */       if (str.toLowerCase().contains(quickFilter)) {
/* 58 */         return true;
/*    */       }
/*    */     }
/* 61 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.PointAndFigureDataTablePresentationManager
 * JD-Core Version:    0.6.0
 */