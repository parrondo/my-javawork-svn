/*    */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*    */ import com.dukascopy.charts.utils.PathHelper;
/*    */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.geom.GeneralPath;
/*    */ 
/*    */ public class BarVisualisationDrawingStrategy extends AbstractCandleVisualisationDrawingStrategy
/*    */ {
/*    */   public BarVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*    */   {
/* 27 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, pathHelper, false);
/*    */   }
/*    */ 
/*    */   protected void plotCandles(Graphics g, CandleDataSequence dataSequence)
/*    */   {
/* 32 */     float halfCandleWidth = this.geometryCalculator.getDataUnitWidthWithoutOverhead() / 2;
/*    */ 
/* 34 */     CandleData[] data = (CandleData[])dataSequence.getData();
/* 35 */     int extraBefore = dataSequence.getExtraBefore();
/* 36 */     int idx = extraBefore; for (int j = data.length - dataSequence.getExtraAfter(); idx < j; idx++) {
/* 37 */       CandleData candle = data[idx];
/* 38 */       if (candle == null) {
/*    */         continue;
/*    */       }
/* 41 */       plotSingleCandle(idx, candle, this.timeToXMapper.xt(candle.time), halfCandleWidth);
/*    */     }
/*    */   }
/*    */ 
/*    */   void plotSingleCandle(int idx, CandleData candle, float middle, float halfDataUnitWidth) {
/* 46 */     float leftPriceY = this.valueToYMapper.yv(candle.open);
/* 47 */     float rightPriceY = this.valueToYMapper.yv(candle.close);
/* 48 */     float highPriceY = this.valueToYMapper.yv(candle.high);
/* 49 */     float lowPriceY = this.valueToYMapper.yv(candle.low);
/*    */ 
/* 51 */     GeneralPath path = null;
/* 52 */     if (candle.open > candle.close)
/* 53 */       path = this.negativePath;
/* 54 */     else if (candle.open < candle.close)
/* 55 */       path = this.positivePath;
/*    */     else {
/* 57 */       path = this.neutralPath;
/*    */     }
/*    */ 
/* 60 */     path.moveTo(middle, highPriceY);
/* 61 */     path.lineTo(middle, lowPriceY);
/*    */ 
/* 63 */     if (halfDataUnitWidth > 0.5D) {
/* 64 */       path.moveTo(middle, leftPriceY);
/* 65 */       path.lineTo(middle - halfDataUnitWidth, leftPriceY);
/* 66 */       path.moveTo(middle, rightPriceY);
/* 67 */       path.lineTo(middle + halfDataUnitWidth, rightPriceY);
/*    */     }
/* 69 */     this.pathHelper.savePoints(idx, middle, highPriceY, lowPriceY, leftPriceY, rightPriceY);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.BarVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */