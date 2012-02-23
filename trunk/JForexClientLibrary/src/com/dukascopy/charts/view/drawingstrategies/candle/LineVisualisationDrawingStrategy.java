/*     */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.LineConstructionMethod;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public class LineVisualisationDrawingStrategy extends AbstractCandleVisualisationDrawingStrategy
/*     */ {
/*     */   public LineVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<CandleDataSequence, CandleData> candlesDataSequenceProvider, GeometryCalculator candlesGeometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*     */   {
/*  32 */     super(dateFormatter, chartState, candlesDataSequenceProvider, candlesGeometryCalculator, timeToXMapper, valueToYMapper, pathHelper, false);
/*     */   }
/*     */ 
/*     */   protected void plotCandles(Graphics g, CandleDataSequence dataSequence)
/*     */   {
/*  37 */     double previousAveragePrice = -1.0D;
/*  38 */     float previousMiddleX = -1.0F;
/*  39 */     double previousMiddleY = -1.0D;
/*     */ 
/*  41 */     CandleData[] data = (CandleData[])dataSequence.getData();
/*  42 */     int dataSequenceSize = data.length;
/*  43 */     int extraBefore = dataSequence.getExtraBefore();
/*  44 */     int extraAfter = dataSequenceSize - dataSequence.getExtraAfter();
/*     */ 
/*  49 */     extraBefore = extraBefore > 0 ? extraBefore - 1 : extraBefore;
/*  50 */     extraAfter = extraAfter + 1 <= data.length ? extraAfter + 1 : extraAfter;
/*     */ 
/*  52 */     for (int idx = extraBefore; idx < extraAfter; idx++) {
/*  53 */       CandleData candleData = data[idx];
/*  54 */       if (candleData == null) {
/*     */         continue;
/*     */       }
/*  57 */       float currentMiddleX = this.timeToXMapper.xt(candleData.time);
/*  58 */       float highPriceY = this.valueToYMapper.yv(candleData.high);
/*  59 */       float lowPriceY = this.valueToYMapper.yv(candleData.low);
/*  60 */       float openPriceY = this.valueToYMapper.yv(candleData.open);
/*  61 */       float closePriceY = this.valueToYMapper.yv(candleData.close);
/*     */ 
/*  63 */       ChartSettings.LineConstructionMethod method = (ChartSettings.LineConstructionMethod)ChartSettings.get(ChartSettings.Option.LINE_CONSTRUCTION_METHOD);
/*     */ 
/*  65 */       double currentMiddleY = method.getY(openPriceY, closePriceY, highPriceY, lowPriceY);
/*  66 */       double currentAveragePrice = method.getY(candleData.open, candleData.close, candleData.high, candleData.low);
/*     */ 
/*  68 */       GeneralPath path = null;
/*  69 */       if (previousAveragePrice <= currentAveragePrice)
/*  70 */         path = this.positivePath;
/*     */       else {
/*  72 */         path = this.negativePath;
/*     */       }
/*     */ 
/*  75 */       if (idx == dataSequenceSize - 1) {
/*  76 */         currentMiddleY = this.valueToYMapper.yv(candleData.close);
/*     */       }
/*     */ 
/*  79 */       if ((previousMiddleX != -1.0F) && (previousMiddleY != -1.0D)) {
/*  80 */         path.moveTo(previousMiddleX, previousMiddleY);
/*  81 */         path.lineTo(currentMiddleX, currentMiddleY);
/*     */       }
/*     */ 
/*  84 */       previousMiddleX = currentMiddleX;
/*  85 */       previousMiddleY = currentMiddleY;
/*  86 */       previousAveragePrice = currentAveragePrice;
/*     */ 
/*  88 */       this.pathHelper.savePoints(idx, currentMiddleX, highPriceY, lowPriceY, openPriceY, closePriceY);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Color getPositiveColor()
/*     */   {
/*  94 */     Color color = this.chartState.getTheme().getColor(ITheme.ChartElement.LINE_UP);
/*  95 */     return color;
/*     */   }
/*     */ 
/*     */   protected Color getNegativeColor()
/*     */   {
/* 100 */     Color color = this.chartState.getTheme().getColor(ITheme.ChartElement.LINE_DOWN);
/* 101 */     return color;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.LineVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */