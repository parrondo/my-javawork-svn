/*    */ package com.dukascopy.charts.chartbuilder;
/*    */ 
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import com.dukascopy.charts.mappers.value.ValueFrame;
/*    */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*    */ 
/*    */ class GeometryOperationManagerStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*    */   implements IGeometryOperationManagerStrategy
/*    */ {
/*    */   private final ChartState chartState;
/*    */   private final GeometryCalculator geometryCalculator;
/*    */   private final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*    */   private final ValueFrame valueFrame;
/*    */   private final IValueToYMapper mainCandleValueToYMapper;
/*    */   private final SubValueToYMapper subCandleValueToYMapper;
/*    */ 
/*    */   public GeometryOperationManagerStrategy(ChartState chartState, GeometryCalculator geometryCalculator, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, ValueFrame valueFrame, IValueToYMapper mainCandleValueToYMapper, SubValueToYMapper subCandleValueToYMapper)
/*    */   {
/* 30 */     this.chartState = chartState;
/* 31 */     this.geometryCalculator = geometryCalculator;
/* 32 */     this.dataSequenceProvider = dataSequenceProvider;
/* 33 */     this.valueFrame = valueFrame;
/* 34 */     this.mainCandleValueToYMapper = mainCandleValueToYMapper;
/* 35 */     this.subCandleValueToYMapper = subCandleValueToYMapper;
/*    */   }
/*    */ 
/*    */   public void componentSizeChanged(int width, int height) {
/* 39 */     if (this.geometryCalculator.getPaneWidth() != width)
/* 40 */       resetGeometry(width, height);
/*    */     else
/* 42 */       this.mainCandleValueToYMapper.computeGeometry(height);
/*    */   }
/*    */ 
/*    */   public void subComponentHeightChanged(Integer indicatorId, int height)
/*    */   {
/* 47 */     this.subCandleValueToYMapper.computeGeometry(indicatorId, height);
/*    */   }
/*    */ 
/*    */   public void subIndicatorAdded(Integer indicatorId, int previousSubHeight) {
/* 51 */     this.subCandleValueToYMapper.subComponentAdded(indicatorId, previousSubHeight);
/*    */   }
/*    */ 
/*    */   public int subIndicatorDeleted(Integer id) {
/* 55 */     return this.subCandleValueToYMapper.subComponentDeleted(id);
/*    */   }
/*    */ 
/*    */   void widthChanged(int width)
/*    */   {
/* 61 */     this.geometryCalculator.paneWidthChanged(width);
/* 62 */     int geomCandlesCount = this.geometryCalculator.getDataUnitsCount();
/* 63 */     geomCandlesCount = geomCandlesCount < 10 ? 10 : geomCandlesCount;
/* 64 */     int realCandlesCount = tryToSetGeomCandlesCount(geomCandlesCount);
/* 65 */     if (realCandlesCount != geomCandlesCount) {
/* 66 */       int recalculatedCandlesCount = this.geometryCalculator.recalculate(realCandlesCount);
/* 67 */       this.dataSequenceProvider.setSequenceSize(recalculatedCandlesCount);
/*    */     }
/*    */   }
/*    */ 
/*    */   int tryToSetGeomCandlesCount(int geomCandlesCount) {
/*    */     try {
/* 73 */       return this.dataSequenceProvider.setSequenceSize(geomCandlesCount); } catch (Exception ex) {
/*    */     }
/* 75 */     return -1;
/*    */   }
/*    */ 
/*    */   private void resetGeometry(int width, int height)
/*    */   {
/* 80 */     widthChanged(width);
/* 81 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/* 82 */     if (!this.chartState.isVerticalChartMovementEnabled()) {
/* 83 */       this.valueFrame.changeMinMax(dataSequence.getMin(), dataSequence.getMax());
/*    */     }
/* 85 */     this.mainCandleValueToYMapper.computeGeometry(height);
/*    */   }
/*    */ 
/*    */   public void resetGeometry()
/*    */   {
/* 90 */     int width = this.geometryCalculator.getPaneWidth();
/* 91 */     int height = this.mainCandleValueToYMapper.getHeight();
/* 92 */     resetGeometry(width, height);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.GeometryOperationManagerStrategy
 * JD-Core Version:    0.6.0
 */