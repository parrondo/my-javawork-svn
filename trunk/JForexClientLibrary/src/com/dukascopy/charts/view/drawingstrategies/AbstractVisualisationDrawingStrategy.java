/*     */ package com.dukascopy.charts.view.drawingstrategies;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public abstract class AbstractVisualisationDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IVisualisationDrawingStrategy
/*     */ {
/*  21 */   protected static final boolean DEBUG_INFO = System.getProperty("debug.info") != null;
/*     */   protected final DateFormatter dateFormatter;
/*     */   protected final ChartState chartState;
/*     */   protected final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   protected final GeometryCalculator geometryCalculator;
/*     */   protected final ITimeToXMapper timeToXMapper;
/*     */   protected final IValueToYMapper valueToYMapper;
/*     */ 
/*     */   public AbstractVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper)
/*     */   {
/*  38 */     this.dateFormatter = dateFormatter;
/*  39 */     this.chartState = chartState;
/*  40 */     this.dataSequenceProvider = dataSequenceProvider;
/*  41 */     this.geometryCalculator = geometryCalculator;
/*  42 */     this.timeToXMapper = timeToXMapper;
/*  43 */     this.valueToYMapper = valueToYMapper;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  47 */     drawData(g, jComponent);
/*  48 */     if (DEBUG_INFO)
/*  49 */       drawDebugInfo(g, jComponent);
/*     */   }
/*     */ 
/*     */   protected abstract void drawData(Graphics paramGraphics, JComponent paramJComponent);
/*     */ 
/*     */   private void drawDebugInfo(Graphics g, JComponent jComponent) {
/*  56 */     Color color = g.getColor();
/*  57 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.META));
/*     */ 
/*  59 */     drawCandlesCountInfo(g, jComponent);
/*  60 */     drawCandlesVirtualOffset(g, jComponent);
/*  61 */     drawPadding(g, jComponent);
/*  62 */     drawGaps(g, jComponent);
/*  63 */     drawBarSeparators(g, jComponent);
/*     */ 
/*  65 */     g.setColor(color);
/*     */   }
/*     */ 
/*     */   void drawBarSeparators(Graphics g, JComponent jComponent)
/*     */   {
/*  76 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/*  78 */     if (dataSequence != null) {
/*  79 */       long time = dataSequence.getFrom();
/*  80 */       int prevX = 0;
/*  81 */       int prevDiff = 0;
/*  82 */       while (time < dataSequence.getTo()) {
/*  83 */         int x = this.timeToXMapper.xt(time);
/*  84 */         int diff = x - prevX;
/*     */ 
/*  86 */         g.drawLine(x, 0, x, diff == prevDiff ? 10 : 15);
/*     */ 
/*  88 */         prevX = x;
/*  89 */         prevDiff = diff;
/*     */ 
/*  91 */         long xTime = this.timeToXMapper.tx(x);
/*  92 */         int xx = this.timeToXMapper.xt(xTime);
/*  93 */         if (x != xx) {
/*  94 */           g.fillOval(xx - 3, 10, 5, 5);
/*  95 */           g.drawString(String.valueOf((xTime - time) / this.timeToXMapper.getInterval()), xx, 25);
/*  96 */           g.drawString(String.valueOf(xx - x), xx, 35);
/*     */         }
/*     */ 
/* 100 */         time += this.dataSequenceProvider.getInterval();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void drawCandlesVirtualOffset(Graphics g, JComponent jComponent) {
/* 106 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 107 */     int chartShiftHandlerCoordinate = this.chartState.getChartShiftHandlerCoordinate();
/* 108 */     String off = "off:              " + chartShiftHandlerCoordinate;
/* 109 */     String offCandles = "off candles: " + chartShiftHandlerCoordinate / this.geometryCalculator.getDataUnitWidth();
/* 110 */     g.drawString(off, jComponent.getWidth() - fontMetrics.stringWidth(off) - 5, jComponent.getHeight() - fontMetrics.getHeight() * 2 - 4);
/* 111 */     g.drawString(offCandles, jComponent.getWidth() - fontMetrics.stringWidth(offCandles) - 5, jComponent.getHeight() - fontMetrics.getHeight() - 3);
/*     */   }
/*     */ 
/*     */   void drawCandlesCountInfo(Graphics g, JComponent jComponent) {
/* 115 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 116 */     g.drawString("last bar idx:    " + (this.geometryCalculator.getDataUnitsCount() - 1), 5, jComponent.getHeight() - 10 - fontMetrics.getHeight() * 2 - 4);
/* 117 */     g.drawString("last candle idx: " + (this.dataSequenceProvider.getSequenceSize() - 1), 5, jComponent.getHeight() - 10 - fontMetrics.getHeight() - 2);
/* 118 */     g.drawString("last data idx:    " + (((AbstractDataSequence)this.dataSequenceProvider.getDataSequence()).size() - 1), 5, jComponent.getHeight() - 10 - 2);
/*     */   }
/*     */ 
/*     */   void drawPadding(Graphics g, JComponent jComponent) {
/* 122 */     double padding = this.valueToYMapper.getPadding();
/*     */ 
/* 124 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/* 125 */     double min = dataSequence.getMin();
/* 126 */     double max = dataSequence.getMax();
/*     */ 
/* 128 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 129 */     String paddingLabel = "padding: " + Math.round(padding * 100.0D) + "%";
/* 130 */     g.drawString(paddingLabel, jComponent.getWidth() - fontMetrics.stringWidth(paddingLabel) - 5, fontMetrics.getHeight() * 2 + 5);
/*     */ 
/* 132 */     String minLabel = "min: " + min;
/* 133 */     g.drawString(minLabel, jComponent.getWidth() - fontMetrics.stringWidth(minLabel) - 5, fontMetrics.getHeight() * 3 + 5);
/*     */ 
/* 135 */     String maxLabel = "max: " + max;
/* 136 */     g.drawString(maxLabel, jComponent.getWidth() - fontMetrics.stringWidth(maxLabel) - 5, fontMetrics.getHeight() * 4 + 5);
/*     */ 
/* 138 */     String extraLabel = "extra : " + dataSequence.getExtraBefore() + " / " + dataSequence.getExtraAfter();
/* 139 */     g.drawString(extraLabel, jComponent.getWidth() - fontMetrics.stringWidth(extraLabel) - 5, fontMetrics.getHeight() * 5 + 5);
/*     */ 
/* 141 */     String marginLabel = "margin : " + this.dataSequenceProvider.getMargin();
/* 142 */     g.drawString(marginLabel, jComponent.getWidth() - fontMetrics.stringWidth(extraLabel) - 15, fontMetrics.getHeight() * 6 + 5);
/*     */ 
/* 144 */     String geometryLabel = this.geometryCalculator.toString();
/* 145 */     g.drawString(geometryLabel, jComponent.getWidth() - fontMetrics.stringWidth(geometryLabel) - 15, fontMetrics.getHeight() * 7 + 5);
/*     */ 
/* 147 */     g.drawLine(0, this.valueToYMapper.yv(min), jComponent.getWidth(), this.valueToYMapper.yv(min));
/* 148 */     g.drawLine(0, this.valueToYMapper.yv(max), jComponent.getWidth(), this.valueToYMapper.yv(max));
/*     */   }
/*     */ 
/*     */   void drawGaps(Graphics g, JComponent jComponent) {
/* 152 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/* 154 */     long[][] gaps = dataSequence.getGaps();
/* 155 */     if (gaps.length > 0)
/* 156 */       for (long[] gap : gaps) {
/* 157 */         int x = this.timeToXMapper.xt(gap[0]) - this.timeToXMapper.getBarWidth() / 2;
/* 158 */         if (!this.timeToXMapper.isXOutOfRange(x)) {
/* 159 */           g.drawLine(x, 15, x, jComponent.getHeight());
/* 160 */           g.drawString(String.valueOf(gap[1]), x - 3, 10);
/*     */         }
/*     */       }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.AbstractVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */