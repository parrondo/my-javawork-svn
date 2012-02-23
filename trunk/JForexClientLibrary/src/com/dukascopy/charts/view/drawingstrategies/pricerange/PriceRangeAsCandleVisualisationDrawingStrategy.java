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
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import java.awt.geom.Rectangle2D.Float;
/*    */ 
/*    */ public class PriceRangeAsCandleVisualisationDrawingStrategy extends AbstractPriceAggregationVisualisationDrawingStrategy<PriceRangeDataSequence, PriceRangeData>
/*    */ {
/*    */   public PriceRangeAsCandleVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<PriceRangeDataSequence, PriceRangeData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 34 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, true, pathHelper);
/*    */   }
/*    */ 
/*    */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath borderPath, PriceRangeData priceRangeData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*    */   {
/* 50 */     int x = barMiddleX - dataUnitWidth / 2;
/* 51 */     int y = Math.min(openPriceY, closePriceY);
/* 52 */     int height = Math.abs(openPriceY - closePriceY) + 1;
/*    */ 
/* 54 */     plotBody(path, x, y, dataUnitWidth, height, borderPath, drawCandleCanvas);
/* 55 */     plotStick(borderPath, barMiddleX, y, height, highPriceY, lowPriceY);
/*    */   }
/*    */ 
/*    */   private void plotStick(GeneralPath path, int middle, int bodyTopY, int bodyHeight, int highPriceY, int lowPriceY) {
/* 59 */     if (highPriceY < bodyTopY) {
/* 60 */       path.moveTo(middle, highPriceY);
/* 61 */       path.lineTo(middle, bodyTopY - 1);
/*    */     }
/*    */ 
/* 64 */     float bodyBottomY = bodyTopY + bodyHeight;
/* 65 */     if (bodyBottomY < lowPriceY) {
/* 66 */       path.moveTo(middle, bodyBottomY - 1.0F);
/* 67 */       path.lineTo(middle, lowPriceY);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void plotBody(GeneralPath path, int x, int y, int candleWidth, int height, GeneralPath neutralPath, boolean drawCandleCanvas) {
/* 72 */     if (height > 1) {
/* 73 */       if (candleWidth > 1) {
/* 74 */         Shape body = new Rectangle2D.Float(x, y, candleWidth - (drawCandleCanvas ? 1 : 0), height - (drawCandleCanvas ? 1 : 0));
/* 75 */         path.append(body, false);
/* 76 */         if (drawCandleCanvas)
/* 77 */           neutralPath.append(body, false);
/*    */       }
/*    */       else {
/* 80 */         neutralPath.moveTo(x, y);
/* 81 */         neutralPath.lineTo(x, y + height);
/*    */       }
/*    */     } else {
/* 84 */       neutralPath.moveTo(x, y);
/* 85 */       neutralPath.lineTo(x + candleWidth - 1, y);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pricerange.PriceRangeAsCandleVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */