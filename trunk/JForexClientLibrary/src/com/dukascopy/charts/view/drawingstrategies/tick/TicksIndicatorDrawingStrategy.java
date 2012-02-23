/*    */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*    */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.indicators.TicksIndicatorDrawingSupport;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractIndicatorDrawingStrategy;
/*    */ 
/*    */ public class TicksIndicatorDrawingStrategy extends AbstractIndicatorDrawingStrategy<TickDataSequence, TickData>
/*    */ {
/*    */   public TicksIndicatorDrawingStrategy(AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider, GeometryCalculator geometryCalculator, IValueToYMapper valueToYMapper, ITimeToXMapper timeToXMapper, PathHelper pathHelper, DrawingsLabelHelper drawingsLabelHelper, FormattersManager formattersManager, ChartState chartState)
/*    */   {
/* 32 */     super(dataSequenceProvider, geometryCalculator, valueToYMapper, timeToXMapper, pathHelper, drawingsLabelHelper, formattersManager, chartState);
/*    */   }
/*    */ 
/*    */   protected AbstractIndicatorDrawingSupport<TickDataSequence, TickData> createIndicatorDrawingSupport(GeometryCalculator geometryCalculator)
/*    */   {
/* 46 */     return new TicksIndicatorDrawingSupport(geometryCalculator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.TicksIndicatorDrawingStrategy
 * JD-Core Version:    0.6.0
 */