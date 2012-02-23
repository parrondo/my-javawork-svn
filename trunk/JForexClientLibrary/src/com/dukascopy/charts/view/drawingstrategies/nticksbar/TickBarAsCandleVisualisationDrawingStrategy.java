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
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import java.awt.geom.Rectangle2D.Float;
/*    */ 
/*    */ public class TickBarAsCandleVisualisationDrawingStrategy extends AbstractPriceAggregationVisualisationDrawingStrategy<TickBarDataSequence, TickBarData>
/*    */ {
/*    */   public TickBarAsCandleVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<TickBarDataSequence, TickBarData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 38 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, true, pathHelper);
/*    */   }
/*    */ 
/*    */   private void plotStick(GeneralPath path, int middle, int bodyTopY, int bodyHeight, int highPriceY, int lowPriceY) {
/* 42 */     if (highPriceY < bodyTopY) {
/* 43 */       path.moveTo(middle, highPriceY);
/* 44 */       path.lineTo(middle, bodyTopY - 1);
/*    */     }
/*    */ 
/* 47 */     float bodyBottomY = bodyTopY + bodyHeight;
/* 48 */     if (bodyBottomY < lowPriceY) {
/* 49 */       path.moveTo(middle, bodyBottomY);
/* 50 */       path.lineTo(middle, lowPriceY);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void plotBody(GeneralPath path, GeneralPath neutralPath, int x, int y, int candleWidth, int height, boolean drawCandleCanvas) {
/* 55 */     if (height > 1) {
/* 56 */       if (candleWidth > 1) {
/* 57 */         Shape body = new Rectangle2D.Float(x, y, candleWidth - (drawCandleCanvas ? 1 : 0), height - (drawCandleCanvas ? 1 : 0));
/* 58 */         path.append(body, false);
/* 59 */         if (drawCandleCanvas)
/* 60 */           neutralPath.append(body, false);
/*    */       }
/*    */       else {
/* 63 */         neutralPath.moveTo(x, y);
/* 64 */         neutralPath.lineTo(x, y + height);
/*    */       }
/*    */     } else {
/* 67 */       neutralPath.moveTo(x, y);
/* 68 */       neutralPath.lineTo(x + candleWidth - 1, y);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath borderPath, TickBarData barData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*    */   {
/* 85 */     int x = barMiddleX - dataUnitWidth / 2;
/* 86 */     int y = Math.min(openPriceY, closePriceY);
/* 87 */     int height = Math.abs(openPriceY - closePriceY) + 1;
/*    */ 
/* 90 */     plotBody(path, borderPath, x, y, dataUnitWidth, height, drawCandleCanvas);
/* 91 */     plotStick(borderPath, barMiddleX, y, height, highPriceY, lowPriceY);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.nticksbar.TickBarAsCandleVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */