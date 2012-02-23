/*     */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import java.awt.geom.GeneralPath;
/*     */ 
/*     */ public class PointAndFigureAsBoxVisualisationDrawingStrategy extends AbstractPointAndFigureVisualisationDrawingStrategy
/*     */ {
/*     */   public PointAndFigureAsBoxVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*     */   {
/*  36 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, false, pathHelper);
/*     */   }
/*     */ 
/*     */   private void plotBullishFigure(GeneralPath path, int x, int y, int width, int height)
/*     */   {
/*  55 */     if (width < 2) {
/*  56 */       path.moveTo(x, y);
/*  57 */       path.lineTo(x, y + height);
/*     */     }
/*  59 */     else if (height < 2) {
/*  60 */       path.moveTo(x, y);
/*  61 */       path.lineTo(x + width, y);
/*     */     }
/*     */     else {
/*  64 */       path.moveTo(x, y);
/*  65 */       path.lineTo(x + width, y + height);
/*     */ 
/*  67 */       path.moveTo(x + width, y);
/*  68 */       path.lineTo(x, y + height);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void plotBearishFigure(GeneralPath path, int x, int y, int width, int height)
/*     */   {
/*  79 */     if (width < 2) {
/*  80 */       path.moveTo(x, y);
/*  81 */       path.lineTo(x, y + height);
/*     */     }
/*  83 */     else if (height < 2) {
/*  84 */       path.moveTo(x, y);
/*  85 */       path.lineTo(x + width, y);
/*     */     }
/*     */     else {
/*  88 */       Shape body = new Ellipse2D.Float(x, y, width, height);
/*  89 */       path.append(body, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath borderPath, PointAndFigureData barData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*     */   {
/* 106 */     double oneBoxSize = this.chartState.getPriceRange().getPipCount() * this.dataSequenceProvider.getInstrument().getPipValue();
/*     */ 
/* 108 */     int boxHeightPix = this.valueToYMapper.yv(oneBoxSize) - this.valueToYMapper.yv(2.0D * oneBoxSize);
/* 109 */     int boxCount = getBoxesCount(barData, oneBoxSize);
/*     */ 
/* 111 */     int x = barMiddleX - dataUnitWidth / 2;
/*     */ 
/* 113 */     for (int i = 0; i < boxCount; i++) {
/* 114 */       int y = this.valueToYMapper.yv(barData.low + (i + 1) * oneBoxSize);
/*     */ 
/* 116 */       if (Boolean.TRUE.equals(barData.isRising())) {
/* 117 */         plotBullishFigure(path, x, y + 1, dataUnitWidth, boxHeightPix - 2);
/*     */       }
/*     */       else
/* 120 */         plotBearishFigure(path, x, y + 1, dataUnitWidth, boxHeightPix - 2);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureAsBoxVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */