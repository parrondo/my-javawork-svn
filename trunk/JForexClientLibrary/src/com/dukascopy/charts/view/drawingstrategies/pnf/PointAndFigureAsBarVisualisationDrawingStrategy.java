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
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.LineMetrics;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ 
/*     */ public class PointAndFigureAsBarVisualisationDrawingStrategy extends AbstractPointAndFigureVisualisationDrawingStrategy
/*     */ {
/*     */   public PointAndFigureAsBarVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*     */   {
/*  43 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, true, pathHelper);
/*     */   }
/*     */ 
/*     */   private Rectangle getBarRect(int middle, int dataUnitWidth, int yHigh, int yLow)
/*     */   {
/*  61 */     int xLeft = middle - dataUnitWidth / 2;
/*  62 */     int xRight = middle + dataUnitWidth / 2;
/*     */ 
/*  64 */     Rectangle r = new Rectangle(xLeft, yHigh, xRight - xLeft, yLow - yHigh);
/*     */ 
/*  66 */     return r;
/*     */   }
/*     */ 
/*     */   private int getFontSize(int dataUnitWidth, int dataUnitHeight)
/*     */   {
/*  76 */     return Math.min(dataUnitWidth, dataUnitHeight) - 6;
/*     */   }
/*     */ 
/*     */   protected void drawCandles(Graphics g, Color neutralColor, Color positiveColor, Color negativeColor, Color positiveBorderColor, Color negativeBorderColor)
/*     */   {
/*  88 */     super.drawCandles(g, neutralColor, positiveColor, negativeColor, positiveBorderColor, negativeBorderColor);
/*  89 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/*  91 */     drawBoxNumbers(g2);
/*     */   }
/*     */ 
/*     */   private void drawBoxNumbers(Graphics2D g2) {
/*  95 */     PointAndFigureDataSequence sequence = (PointAndFigureDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/*  97 */     if (sequence.isEmpty()) {
/*  98 */       return;
/*     */     }
/*     */ 
/* 101 */     int candleWidth = this.geometryCalculator.getDataUnitWidthWithoutOverhead();
/* 102 */     if (candleWidth > 9) {
/* 103 */       double oneBoxSize = this.chartState.getPriceRange().getPipCount() * this.dataSequenceProvider.getInstrument().getPipValue();
/* 104 */       g2.setColor(Color.YELLOW);
/*     */ 
/* 106 */       for (int i = 0; i < sequence.size(); i++)
/*     */       {
/* 108 */         PointAndFigureData data = (PointAndFigureData)sequence.getData(i);
/* 109 */         int boxCount = getBoxesCount(data, oneBoxSize);
/*     */ 
/* 111 */         int middle = this.timeToXMapper.xt(data.time);
/* 112 */         int yLow = this.valueToYMapper.yv(data.low);
/* 113 */         int yHigh = this.valueToYMapper.yv(data.high);
/*     */ 
/* 115 */         Rectangle r = getBarRect(middle, candleWidth, yHigh, yLow);
/*     */ 
/* 117 */         Font f = g2.getFont();
/* 118 */         g2.setFont(new Font(f.getName(), f.getStyle(), getFontSize(candleWidth, r.height)));
/*     */ 
/* 120 */         String boxCountStr = String.valueOf(boxCount);
/* 121 */         FontRenderContext frc = g2.getFontRenderContext();
/* 122 */         LineMetrics lm = g2.getFont().getLineMetrics(boxCountStr, frc);
/*     */ 
/* 124 */         float sw = (float)g2.getFont().getStringBounds(boxCountStr, frc).getWidth();
/* 125 */         float sh = lm.getAscent() + lm.getDescent();
/* 126 */         float sx = r.x + (r.width - sw) / 2.0F;
/* 127 */         float sy = r.y + (r.height + sh) / 2.0F - lm.getDescent();
/*     */ 
/* 129 */         g2.drawString(boxCountStr, sx, sy);
/* 130 */         g2.setFont(f);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void plotSinglePriceRange(GeneralPath path, GeneralPath borderPath, PointAndFigureData barData, int dataUnitWidth, int barMiddleX, int openPriceY, int highPriceY, int lowPriceY, int closePriceY, boolean drawCandleCanvas)
/*     */   {
/* 148 */     Rectangle r = getBarRect(barMiddleX, dataUnitWidth, highPriceY, lowPriceY);
/* 149 */     Shape body = new Rectangle2D.Float(r.x, r.y, r.width, r.height);
/* 150 */     path.append(body, false);
/*     */ 
/* 152 */     if (drawCandleCanvas)
/* 153 */       borderPath.append(body, false);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureAsBarVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */