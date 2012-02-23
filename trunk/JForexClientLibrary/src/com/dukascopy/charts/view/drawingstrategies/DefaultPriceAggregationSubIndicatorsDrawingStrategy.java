/*    */ package com.dukascopy.charts.view.drawingstrategies;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*    */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.indicators.DefaultPriceAggregationIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ 
/*    */ public class DefaultPriceAggregationSubIndicatorsDrawingStrategy<DS extends AbstractPriceAggregationDataSequence<T>, T extends AbstractPriceAggregationData> extends AbstractSubIndicatorsDrawingStrategy<DS, T>
/*    */ {
/*    */   public DefaultPriceAggregationSubIndicatorsDrawingStrategy(SubIndicatorGroup subIndicatorGroup, GeometryCalculator geometryCalculator, AbstractDataSequenceProvider<DS, T> dataSequenceProvider, SubValueToYMapper subCandlesValueToYMapper, ITimeToXMapper timeToXMapper, PathHelper pathHelper, DrawingsLabelHelper drawingsLabelHelper, FormattersManager formattersManager, ChartState chartState)
/*    */   {
/* 37 */     super(subIndicatorGroup, geometryCalculator, dataSequenceProvider, subCandlesValueToYMapper, timeToXMapper, pathHelper, drawingsLabelHelper, formattersManager, chartState);
/*    */   }
/*    */ 
/*    */   protected AbstractIndicatorDrawingSupport<DS, T> createIndicatorSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 52 */     return new DefaultPriceAggregationIndicatorDrawingSupport(geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.DefaultPriceAggregationSubIndicatorsDrawingStrategy
 * JD-Core Version:    0.6.0
 */