/*    */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractPriceAggregationVisualisationDrawingStrategy;
/*    */ 
/*    */ public abstract class AbstractPointAndFigureVisualisationDrawingStrategy extends AbstractPriceAggregationVisualisationDrawingStrategy<PointAndFigureDataSequence, PointAndFigureData>
/*    */ {
/*    */   public AbstractPointAndFigureVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, boolean fillable, PathHelper pathHelper)
/*    */   {
/* 34 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, fillable, pathHelper);
/*    */   }
/*    */ 
/*    */   protected int getBoxesCount(PointAndFigureData currentData, double oneBoxSize)
/*    */   {
/* 48 */     double low = currentData.low;
/* 49 */     double high = currentData.high;
/*    */ 
/* 51 */     double count = (high - low) / oneBoxSize;
/* 52 */     return (int)(count + 0.5D);
/*    */   }
/*    */ 
/*    */   protected Boolean detectTrend(PointAndFigureData previousBarData, PointAndFigureData barData, PointAndFigureData nextBarData)
/*    */   {
/* 61 */     return barData.isRising();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.AbstractPointAndFigureVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */