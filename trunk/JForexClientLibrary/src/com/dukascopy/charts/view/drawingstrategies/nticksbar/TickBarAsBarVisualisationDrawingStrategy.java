/*    */ package com.dukascopy.charts.view.drawingstrategies.nticksbar;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.AbstractPriceAggregationVisualisationDrawingStrategy;
/*    */ import java.awt.geom.GeneralPath;
/*    */ 
/*    */ public class TickBarAsBarVisualisationDrawingStrategy extends AbstractPriceAggregationVisualisationDrawingStrategy<TickBarDataSequence, TickBarData>
/*    */ {
/*    */   public TickBarAsBarVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 35 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, false, pathHelper);
/*    */   }
/*    */ 
/*    */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath borderPath, TickBarData barData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*    */   {
/* 60 */     path.moveTo(barMiddleX, highPriceY);
/* 61 */     path.lineTo(barMiddleX, lowPriceY);
/*    */ 
/* 63 */     if (dataUnitWidth > 1) {
/* 64 */       path.moveTo(barMiddleX, openPriceY);
/* 65 */       path.lineTo(barMiddleX - dataUnitWidth / 2, openPriceY);
/* 66 */       path.moveTo(barMiddleX, closePriceY);
/* 67 */       path.lineTo(barMiddleX + dataUnitWidth / 2, closePriceY);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.nticksbar.TickBarAsBarVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */