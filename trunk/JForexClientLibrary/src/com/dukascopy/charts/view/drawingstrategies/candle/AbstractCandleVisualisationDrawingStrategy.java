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
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.view.drawingstrategies.AbstractVisualisationDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import javax.swing.JComponent;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractCandleVisualisationDrawingStrategy extends AbstractVisualisationDrawingStrategy<CandleDataSequence, CandleData>
/*     */ {
/*  32 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCandleVisualisationDrawingStrategy.class);
/*     */   private final boolean fillable;
/*  35 */   protected final GeneralPath positivePath = new GeneralPath();
/*  36 */   protected final GeneralPath negativePath = new GeneralPath();
/*  37 */   protected final GeneralPath positiveBorderPath = new GeneralPath();
/*  38 */   protected final GeneralPath negativeBorderPath = new GeneralPath();
/*  39 */   protected final GeneralPath neutralPath = new GeneralPath();
/*     */   protected final PathHelper pathHelper;
/*     */ 
/*     */   public AbstractCandleVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<CandleDataSequence, CandleData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper, boolean fillable)
/*     */   {
/*  53 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper);
/*  54 */     this.fillable = fillable;
/*  55 */     this.pathHelper = pathHelper;
/*     */   }
/*     */ 
/*     */   public void drawData(Graphics g, JComponent jComponent)
/*     */   {
/*  61 */     this.positivePath.reset();
/*  62 */     this.negativePath.reset();
/*  63 */     this.positiveBorderPath.reset();
/*  64 */     this.negativeBorderPath.reset();
/*  65 */     this.neutralPath.reset();
/*  66 */     this.pathHelper.resetPoints();
/*     */ 
/*  68 */     CandleDataSequence candleDataSequence = (CandleDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/*  70 */     if (!candleDataSequence.isEmpty()) {
/*  71 */       if (LOGGER.isTraceEnabled()) {
/*  72 */         LOGGER.trace(candleDataSequence.toString());
/*     */ 
/*  74 */         LOGGER.trace("candlesCount = " + this.geometryCalculator.getDataUnitsCount() + " candleDataSize = " + candleDataSequence.size() + " margin = " + this.dataSequenceProvider.getMargin());
/*     */       }
/*     */ 
/*  81 */       plotCandles(g, candleDataSequence);
/*     */ 
/*  83 */       if ((ChartSettings.getBoolean(ChartSettings.Option.LAST_CANDLE_TRACKING)) && ((candleDataSequence.isLatestDataVisible()) || (ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING))))
/*     */       {
/*  87 */         drawLastCandleMarker(g, jComponent, (CandleData)this.dataSequenceProvider.getLastKnownData());
/*     */       }
/*     */ 
/*  90 */       Color neutralColor = this.chartState.getTheme().getColor(isFillable() ? ITheme.ChartElement.DOJI_CANDLE : ITheme.ChartElement.NEUTRAL_BAR);
/*  91 */       Color positiveColor = getPositiveColor();
/*  92 */       Color negativeColor = getNegativeColor();
/*  93 */       Color positiveBorderColor = this.chartState.getTheme().getColor(ITheme.ChartElement.CANDLE_BULL_BORDER);
/*  94 */       Color negativeBorderColor = this.chartState.getTheme().getColor(ITheme.ChartElement.CANDLE_BEAR_BORDER);
/*     */ 
/*  96 */       drawCandles(g, neutralColor, positiveColor, negativeColor, positiveBorderColor, negativeBorderColor);
/*     */     }
/*  98 */     else if (LOGGER.isTraceEnabled()) {
/*  99 */       LOGGER.trace("candleDataSequence.isEmpty()=" + candleDataSequence.isEmpty());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Color getPositiveColor()
/*     */   {
/* 108 */     Color color = this.chartState.getTheme().getColor(isFillable() ? ITheme.ChartElement.CANDLE_BULL : ITheme.ChartElement.BAR_UP);
/* 109 */     return color;
/*     */   }
/*     */ 
/*     */   protected Color getNegativeColor()
/*     */   {
/* 116 */     Color color = this.chartState.getTheme().getColor(isFillable() ? ITheme.ChartElement.CANDLE_BEAR : ITheme.ChartElement.BAR_DOWN);
/* 117 */     return color;
/*     */   }
/*     */ 
/*     */   void drawCandles(Graphics g, Color neutralColor, Color positiveColor, Color negativeColor, Color positiveBorderColor, Color negativeBorderColor) {
/* 121 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 123 */     g2d.setColor(positiveColor);
/* 124 */     if (isFillable())
/* 125 */       g2d.fill(this.positivePath);
/*     */     else {
/* 127 */       g2d.draw(this.positivePath);
/*     */     }
/*     */ 
/* 130 */     g2d.setColor(negativeColor);
/* 131 */     if (isFillable())
/* 132 */       g2d.fill(this.negativePath);
/*     */     else {
/* 134 */       g2d.draw(this.negativePath);
/*     */     }
/*     */ 
/* 137 */     g2d.setColor(neutralColor);
/* 138 */     g2d.draw(this.neutralPath);
/*     */ 
/* 140 */     g2d.setColor(positiveBorderColor);
/* 141 */     g2d.draw(this.positiveBorderPath);
/*     */ 
/* 143 */     g2d.setColor(negativeBorderColor);
/* 144 */     g2d.draw(this.negativeBorderPath);
/*     */   }
/*     */ 
/*     */   boolean isFillable() {
/* 148 */     return (this.fillable) && (this.geometryCalculator.getDataUnitWidthWithoutOverhead() > 1);
/*     */   }
/*     */   protected abstract void plotCandles(Graphics paramGraphics, CandleDataSequence paramCandleDataSequence);
/*     */ 
/*     */   void drawLastCandleMarker(Graphics g, JComponent jComponent, CandleData lastCandle) {
/* 154 */     if (lastCandle == null) {
/* 155 */       return;
/*     */     }
/* 157 */     Graphics2D g2d = (Graphics2D)g;
/* 158 */     int x = 0;
/*     */ 
/* 160 */     if (!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING))
/*     */     {
/* 162 */       x = this.timeToXMapper.xt(lastCandle.time);
/* 163 */       if (this.timeToXMapper.isXOutOfRange(x)) {
/* 164 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 168 */     int y = this.valueToYMapper.yv(lastCandle.close);
/*     */ 
/* 170 */     Stroke stroke = g2d.getStroke();
/*     */ 
/* 172 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE));
/* 173 */     g2d.setStroke(this.chartState.getTheme().getStroke(ITheme.StrokeElement.LAST_CANDLE_TRACKING_LINE_STROKE));
/* 174 */     g2d.drawLine(x + 1, y, jComponent.getWidth(), y);
/*     */ 
/* 176 */     g2d.setStroke(stroke);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.AbstractCandleVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */