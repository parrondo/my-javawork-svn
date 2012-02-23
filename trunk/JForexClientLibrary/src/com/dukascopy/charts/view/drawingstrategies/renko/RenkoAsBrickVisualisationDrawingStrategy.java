/*     */ package com.dukascopy.charts.view.drawingstrategies.renko;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.view.drawingstrategies.AbstractPriceAggregationVisualisationDrawingStrategy;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class RenkoAsBrickVisualisationDrawingStrategy extends AbstractPriceAggregationVisualisationDrawingStrategy<RenkoDataSequence, RenkoData>
/*     */ {
/*     */   public RenkoAsBrickVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<RenkoDataSequence, RenkoData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*     */   {
/*  40 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, true, pathHelper);
/*     */   }
/*     */ 
/*     */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath borderPath, RenkoData barData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*     */   {
/*  66 */     int height = Math.abs(highPriceY - lowPriceY) + 1;
/*  67 */     int x = barMiddleX - dataUnitWidth / 2;
/*  68 */     int y = Math.min(highPriceY, lowPriceY);
/*     */ 
/*  70 */     if (height > 1) {
/*  71 */       if (dataUnitWidth > 1)
/*     */       {
/*  74 */         Shape body = new Rectangle2D.Float(x, y, dataUnitWidth - (drawCandleCanvas ? 1 : 0), height - (drawCandleCanvas ? 1 : 0));
/*     */ 
/*  81 */         path.append(body, false);
/*  82 */         if (drawCandleCanvas)
/*  83 */           borderPath.append(body, false);
/*     */       }
/*     */       else {
/*  86 */         borderPath.moveTo(x, y);
/*  87 */         borderPath.lineTo(x, y + height);
/*     */       }
/*     */     } else {
/*  90 */       borderPath.moveTo(x, y);
/*  91 */       borderPath.lineTo(x + dataUnitWidth - 1, y);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawLastCandleMarker(Graphics g, JComponent jComponent, RenkoData lastBarData)
/*     */   {
/*  98 */     if (lastBarData != null) {
/*  99 */       long time = lastBarData.time;
/* 100 */       if (lastBarData.getInProgressRenko() != null) {
/* 101 */         lastBarData = lastBarData.getInProgressRenko();
/*     */       }
/* 103 */       super.drawLastCandleMarker(g, jComponent, lastBarData, time);
/*     */     }
/*     */     else {
/* 106 */       super.drawLastCandleMarker(g, jComponent, lastBarData);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Boolean detectTrend(RenkoData previousBarData, RenkoData barData, RenkoData nextBarData)
/*     */   {
/* 116 */     return barData.isRising();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.renko.RenkoAsBrickVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */