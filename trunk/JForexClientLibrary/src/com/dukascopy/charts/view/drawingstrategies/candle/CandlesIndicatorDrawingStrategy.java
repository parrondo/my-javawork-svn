/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*    */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.indicators.CandlesIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractIndicatorDrawingStrategy;
/*    */ 
/*    */ public class CandlesIndicatorDrawingStrategy extends AbstractIndicatorDrawingStrategy<CandleDataSequence, CandleData>
/*    */ {
/*    */   public CandlesIndicatorDrawingStrategy(AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, GeometryCalculator geometryCalculator, IValueToYMapper valueToYMapper, ITimeToXMapper timeToXMapper, PathHelper pathHelper, DrawingsLabelHelper drawingsLabelHelper, FormattersManager formattersManager, ChartState chartState)
/*    */   {
/* 32 */     super(dataSequenceProvider, geometryCalculator, valueToYMapper, timeToXMapper, pathHelper, drawingsLabelHelper, formattersManager, chartState);
/*    */   }
/*    */ 
/*    */   protected AbstractIndicatorDrawingSupport<CandleDataSequence, CandleData> createIndicatorDrawingSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 46 */     return new CandlesIndicatorDrawingSupport(geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.CandlesIndicatorDrawingStrategy
 * JD-Core Version:    0.6.0
 */