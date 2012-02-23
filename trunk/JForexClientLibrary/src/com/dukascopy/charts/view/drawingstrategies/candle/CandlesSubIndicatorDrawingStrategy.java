/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*    */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.indicators.CandlesIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractSubIndicatorsDrawingStrategy;
/*    */ 
/*    */ public class CandlesSubIndicatorDrawingStrategy extends AbstractSubIndicatorsDrawingStrategy<CandleDataSequence, CandleData>
/*    */ {
/*    */   public CandlesSubIndicatorDrawingStrategy(SubIndicatorGroup subIndicatorGroup, GeometryCalculator geometryCalculator, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, SubValueToYMapper subValueToYMapper, ITimeToXMapper timeToXMapper, PathHelper pathHelper, DrawingsLabelHelper drawingsLabelHelper, FormattersManager formattersManager, ChartState chartState)
/*    */   {
/* 34 */     super(subIndicatorGroup, geometryCalculator, dataSequenceProvider, subValueToYMapper, timeToXMapper, pathHelper, drawingsLabelHelper, formattersManager, chartState);
/*    */   }
/*    */ 
/*    */   protected AbstractIndicatorDrawingSupport<CandleDataSequence, CandleData> createIndicatorSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 49 */     return new CandlesIndicatorDrawingSupport(geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.CandlesSubIndicatorDrawingStrategy
 * JD-Core Version:    0.6.0
 */