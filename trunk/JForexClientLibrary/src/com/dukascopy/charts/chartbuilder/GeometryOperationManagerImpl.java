/*    */ package com.dukascopy.charts.chartbuilder;
/*    */ 
/*    */ import com.dukascopy.api.DataType;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ class GeometryOperationManagerImpl
/*    */   implements GeometryOperationManagerListener
/*    */ {
/* 10 */   private static final Logger LOGGER = LoggerFactory.getLogger(GeometryOperationManagerImpl.class);
/*    */   private final ChartState chartState;
/*    */   private final IGeometryOperationManagerStrategy tickGOMS;
/*    */   private final IGeometryOperationManagerStrategy candleGOMS;
/*    */   private final IGeometryOperationManagerStrategy priceRangeGOMS;
/*    */   private final IGeometryOperationManagerStrategy pointAndFigureGOMS;
/*    */   private final IGeometryOperationManagerStrategy tickBarGOMS;
/*    */   private final IGeometryOperationManagerStrategy renkoGOMS;
/*    */ 
/*    */   GeometryOperationManagerImpl(ChartState chartState, IGeometryOperationManagerStrategy tickGOMS, IGeometryOperationManagerStrategy candleGOMS, IGeometryOperationManagerStrategy priceRangeGOMS, IGeometryOperationManagerStrategy pointAndFigureGOMS, IGeometryOperationManagerStrategy tickBarGOMS, IGeometryOperationManagerStrategy renkoGOMS)
/*    */   {
/* 30 */     this.chartState = chartState;
/* 31 */     this.tickGOMS = tickGOMS;
/* 32 */     this.candleGOMS = candleGOMS;
/* 33 */     this.priceRangeGOMS = priceRangeGOMS;
/* 34 */     this.pointAndFigureGOMS = pointAndFigureGOMS;
/* 35 */     this.tickBarGOMS = tickBarGOMS;
/* 36 */     this.renkoGOMS = renkoGOMS;
/*    */   }
/*    */ 
/*    */   public void componentSizeChanged(int width, int height)
/*    */   {
/* 41 */     if (LOGGER.isTraceEnabled()) {
/* 42 */       LOGGER.trace("componentSizeChanged(" + width + ", " + height + ")");
/*    */     }
/* 44 */     getOperationManager().componentSizeChanged(width, height);
/*    */   }
/*    */ 
/*    */   public void subComponentHeightChanged(IndicatorWrapper indicatorWrapper, int newHeight)
/*    */   {
/* 49 */     if (LOGGER.isTraceEnabled()) {
/* 50 */       LOGGER.trace("subComponentHeightChanged(" + indicatorWrapper.getId() + ", " + newHeight + ")");
/*    */     }
/* 52 */     getOperationManager().subComponentHeightChanged(Integer.valueOf(indicatorWrapper.getId()), newHeight);
/*    */   }
/*    */ 
/*    */   public void subIndicatorAdded(IndicatorWrapper indicatorWrapper, int previousSubHeight)
/*    */   {
/* 57 */     if (LOGGER.isTraceEnabled()) {
/* 58 */       LOGGER.trace("subIndicatorAdded(" + indicatorWrapper.getId() + ")");
/*    */     }
/* 60 */     getOperationManager().subIndicatorAdded(Integer.valueOf(indicatorWrapper.getId()), previousSubHeight);
/*    */   }
/*    */ 
/*    */   public int subIndicatorDeleted(IndicatorWrapper indicatorWrapper)
/*    */   {
/* 65 */     if (LOGGER.isTraceEnabled()) {
/* 66 */       LOGGER.trace("subIndicatorsDeleted(" + indicatorWrapper.getId() + ")");
/*    */     }
/* 68 */     return getOperationManager().subIndicatorDeleted(Integer.valueOf(indicatorWrapper.getId()));
/*    */   }
/*    */ 
/*    */   private IGeometryOperationManagerStrategy getOperationManager() {
/* 72 */     DataType dataType = this.chartState.getDataType();
/* 73 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) { case 1:
/* 74 */       return this.tickGOMS;
/*    */     case 2:
/* 75 */       return this.candleGOMS;
/*    */     case 3:
/* 76 */       return this.priceRangeGOMS;
/*    */     case 4:
/* 77 */       return this.pointAndFigureGOMS;
/*    */     case 5:
/* 78 */       return this.tickBarGOMS;
/*    */     case 6:
/* 79 */       return this.renkoGOMS; }
/* 80 */     throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.GeometryOperationManagerImpl
 * JD-Core Version:    0.6.0
 */