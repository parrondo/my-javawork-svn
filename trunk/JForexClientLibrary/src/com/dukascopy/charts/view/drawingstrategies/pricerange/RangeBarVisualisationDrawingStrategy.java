/*    */ package com.dukascopy.charts.view.drawingstrategies.pricerange;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractPriceAggregationVisualisationDrawingStrategy;
/*    */ import java.awt.geom.GeneralPath;
/*    */ 
/*    */ public class RangeBarVisualisationDrawingStrategy extends AbstractPriceAggregationVisualisationDrawingStrategy<PriceRangeDataSequence, PriceRangeData>
/*    */ {
/*    */   public RangeBarVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<PriceRangeDataSequence, PriceRangeData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 31 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, false, pathHelper);
/*    */   }
/*    */ 
/*    */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath neutralPath, PriceRangeData priceRangeData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*    */   {
/* 47 */     path.moveTo(barMiddleX, highPriceY);
/* 48 */     path.lineTo(barMiddleX, lowPriceY);
/*    */ 
/* 50 */     if (dataUnitWidth > 1) {
/* 51 */       path.moveTo(barMiddleX, openPriceY);
/* 52 */       path.lineTo(barMiddleX - dataUnitWidth / 2, openPriceY);
/* 53 */       path.moveTo(barMiddleX, closePriceY);
/* 54 */       path.lineTo(barMiddleX + dataUnitWidth / 2, closePriceY);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pricerange.RangeBarVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */