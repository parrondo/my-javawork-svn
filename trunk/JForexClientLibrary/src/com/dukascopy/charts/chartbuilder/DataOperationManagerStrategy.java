/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.ValueFrame;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class DataOperationManagerStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDataOperationManagerStrategy
/*     */ {
/*  15 */   private static final Logger LOGGER = LoggerFactory.getLogger(DataOperationManagerStrategy.class);
/*     */   private final ChartState chartState;
/*     */   private final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   private final GeometryCalculator geometryCalculator;
/*     */   private final ValueFrame valueFrame;
/*     */   private final IValueToYMapper valueToYMapper;
/*     */   private final SubValueToYMapper subValueToYMapper;
/*     */   private final IGeometryOperationManagerStrategy geometryOperationManagerStrategy;
/*     */ 
/*     */   DataOperationManagerStrategy(ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator, ValueFrame valueFrame, IValueToYMapper valueToYMapper, SubValueToYMapper subValueToYMapper, IGeometryOperationManagerStrategy geometryOperationManagerStrategy)
/*     */   {
/*  37 */     this.chartState = chartState;
/*  38 */     this.dataSequenceProvider = dataSequenceProvider;
/*  39 */     this.geometryCalculator = geometryCalculator;
/*  40 */     this.valueFrame = valueFrame;
/*  41 */     this.valueToYMapper = valueToYMapper;
/*  42 */     this.subValueToYMapper = subValueToYMapper;
/*  43 */     this.geometryOperationManagerStrategy = geometryOperationManagerStrategy;
/*     */   }
/*     */ 
/*     */   public boolean dataChanged(long from, long to) {
/*  47 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/*  49 */     if (dataSequence.isEmpty()) {
/*  50 */       return false;
/*     */     }
/*  52 */     if (this.chartState.isChartShiftActive()) {
/*  53 */       int candlesShiftingDiff = Math.round(this.chartState.getChartShiftHandlerCoordinate() / this.geometryCalculator.getDataUnitWidth());
/*     */       try {
/*  55 */         this.dataSequenceProvider.shiftToLastTick(candlesShiftingDiff);
/*  56 */         recalculateGeometry();
/*  57 */         return true;
/*     */       } catch (Exception e) {
/*  59 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*  61 */     } else if (dataSequence.intersects(from, to))
/*     */     {
/*  63 */       recalculateGeometry();
/*  64 */       return true;
/*     */     }
/*     */ 
/*  67 */     return false;
/*     */   }
/*     */ 
/*     */   public void mainIndicatorAdded(int id) {
/*  71 */     calculateYMainDataGeometry();
/*     */   }
/*     */ 
/*     */   public void mainIndicatorEdited(int id) {
/*  75 */     calculateYMainDataGeometry();
/*     */   }
/*     */ 
/*     */   public void subIndicatorAdded(int indicatorId) {
/*  79 */     calculateYSubDataGeometry(indicatorId, this.dataSequenceProvider);
/*     */   }
/*     */ 
/*     */   public void subIndicatorEdited(int indicatorId) {
/*  83 */     calculateYSubDataGeometry(indicatorId, this.dataSequenceProvider);
/*     */   }
/*     */ 
/*     */   private void recalculateGeometry()
/*     */   {
/*  89 */     this.geometryOperationManagerStrategy.resetGeometry();
/*  90 */     for (Integer indicatorId : this.subValueToYMapper.keySet())
/*  91 */       calculateYSubDataGeometry(indicatorId.intValue(), this.dataSequenceProvider);
/*     */   }
/*     */ 
/*     */   private void calculateYMainDataGeometry()
/*     */   {
/*  96 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*  97 */     if (!this.chartState.isVerticalChartMovementEnabled()) {
/*  98 */       this.valueFrame.changeMinMax(dataSequence.getMin(), dataSequence.getMax());
/*     */     }
/* 100 */     this.valueToYMapper.computeGeometry();
/*     */   }
/*     */ 
/*     */   private void calculateYSubDataGeometry(int indicatorId, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider) {
/* 104 */     AbstractDataSequence dataSequence = (AbstractDataSequence)dataSequenceProvider.getDataSequence();
/* 105 */     if (dataSequence == null) {
/* 106 */       return;
/*     */     }
/* 108 */     if (dataSequence.isFormulasMinMaxEmpty(Integer.valueOf(indicatorId))) {
/* 109 */       return;
/*     */     }
/* 111 */     double min = dataSequence.getFormulasMinFor(Integer.valueOf(indicatorId));
/* 112 */     double max = dataSequence.getFormulasMaxFor(Integer.valueOf(indicatorId));
/* 113 */     this.subValueToYMapper.get(Integer.valueOf(indicatorId)).computeGeometry(min, max);
/*     */   }
/*     */ 
/*     */   public void orderChanged(long from, long to)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.DataOperationManagerStrategy
 * JD-Core Version:    0.6.0
 */