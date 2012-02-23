/*     */ package com.dukascopy.charts.view.drawingstrategies;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public abstract class AbstractPriceAggregationVisualisationDrawingStrategy<DS extends AbstractPriceAggregationDataSequence<D>, D extends AbstractPriceAggregationData> extends AbstractVisualisationDrawingStrategy<DS, D>
/*     */ {
/*  37 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private final boolean fillable;
/*  43 */   private final GeneralPath positivePath = new GeneralPath();
/*  44 */   private final GeneralPath negativePath = new GeneralPath();
/*  45 */   private final GeneralPath neutralPath = new GeneralPath();
/*  46 */   private final GeneralPath positiveBorderPath = new GeneralPath();
/*  47 */   private final GeneralPath negativeBorderPath = new GeneralPath();
/*     */   private final PathHelper pathHelper;
/*     */ 
/*     */   public AbstractPriceAggregationVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<DS, D> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, boolean fillable, PathHelper pathHelper)
/*     */   {
/*  63 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper);
/*     */ 
/*  72 */     this.fillable = fillable;
/*  73 */     this.pathHelper = pathHelper;
/*     */   }
/*     */ 
/*     */   protected void drawData(Graphics g, JComponent jComponent)
/*     */   {
/*  79 */     this.neutralPath.reset();
/*  80 */     this.positivePath.reset();
/*  81 */     this.negativePath.reset();
/*  82 */     this.positiveBorderPath.reset();
/*  83 */     this.negativeBorderPath.reset();
/*  84 */     this.pathHelper.resetPoints();
/*     */ 
/*  86 */     AbstractPriceAggregationDataSequence dataSequence = (AbstractPriceAggregationDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/*  88 */     if (dataSequence.isEmpty()) {
/*  89 */       return;
/*     */     }
/*     */ 
/*  93 */     int barWidth = this.geometryCalculator.getDataUnitWidthWithoutOverhead();
/*     */ 
/*  95 */     boolean drawCandleCanvas = ChartSettings.getBoolean(ChartSettings.Option.CANDLE_CANVAS);
/*     */ 
/*  97 */     for (int i = 0; i < dataSequence.size(); i++) {
/*  98 */       AbstractPriceAggregationData barData = (AbstractPriceAggregationData)dataSequence.getData(i);
/*     */ 
/* 100 */       GeneralPath path = null;
/* 101 */       GeneralPath borderPath = null;
/*     */ 
/* 103 */       Boolean trend = detectTrend((AbstractPriceAggregationData)dataSequence.getData(i - 1), barData, (AbstractPriceAggregationData)dataSequence.getData(i + 1));
/*     */ 
/* 105 */       if (Boolean.FALSE.equals(trend)) {
/* 106 */         path = this.negativePath;
/* 107 */         borderPath = this.negativeBorderPath;
/* 108 */       } else if (Boolean.TRUE.equals(trend)) {
/* 109 */         path = this.positivePath;
/* 110 */         borderPath = this.positiveBorderPath;
/*     */       } else {
/* 112 */         path = this.neutralPath;
/* 113 */         borderPath = this.neutralPath;
/*     */       }
/*     */ 
/* 116 */       int middle = this.timeToXMapper.xt(barData.time);
/* 117 */       int openPriceY = this.valueToYMapper.yv(barData.open);
/* 118 */       int closePriceY = this.valueToYMapper.yv(barData.close);
/* 119 */       int highPriceY = this.valueToYMapper.yv(barData.high);
/* 120 */       int lowPriceY = this.valueToYMapper.yv(barData.low);
/*     */ 
/* 122 */       plotSinglePriceRange(path, borderPath, barData, barWidth, middle, openPriceY, highPriceY, lowPriceY, closePriceY, drawCandleCanvas);
/*     */ 
/* 135 */       this.pathHelper.savePoints(i, middle, highPriceY, lowPriceY, openPriceY, closePriceY);
/*     */     }
/*     */ 
/* 145 */     if ((ChartSettings.getBoolean(ChartSettings.Option.LAST_CANDLE_TRACKING)) && ((dataSequence.isLatestDataVisible()) || (ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING))))
/*     */     {
/* 149 */       drawLastCandleMarker(g, jComponent, (AbstractPriceAggregationData)this.dataSequenceProvider.getLastKnownData());
/*     */     }
/*     */ 
/* 152 */     Color neutralColor = this.chartState.getTheme().getColor(isFillable() ? ITheme.ChartElement.DOJI_CANDLE : ITheme.ChartElement.NEUTRAL_BAR);
/* 153 */     Color positiveColor = this.chartState.getTheme().getColor(isFillable() ? ITheme.ChartElement.CANDLE_BULL : ITheme.ChartElement.BAR_UP);
/* 154 */     Color negativeColor = this.chartState.getTheme().getColor(isFillable() ? ITheme.ChartElement.CANDLE_BEAR : ITheme.ChartElement.BAR_DOWN);
/* 155 */     Color positiveBorderColor = this.chartState.getTheme().getColor(ITheme.ChartElement.CANDLE_BULL_BORDER);
/* 156 */     Color negativeBorderColor = this.chartState.getTheme().getColor(ITheme.ChartElement.CANDLE_BEAR_BORDER);
/*     */ 
/* 158 */     drawCandles(g, neutralColor, positiveColor, negativeColor, positiveBorderColor, negativeBorderColor);
/*     */   }
/*     */ 
/*     */   protected abstract void plotSinglePriceRange(GeneralPath paramGeneralPath1, GeneralPath paramGeneralPath2, D paramD, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean);
/*     */ 
/*     */   protected Boolean detectTrend(D previousBarData, D barData, D nextBarData)
/*     */   {
/* 179 */     if (barData.getOpen() > barData.getClose())
/* 180 */       return Boolean.FALSE;
/* 181 */     if (barData.getOpen() < barData.getClose()) {
/* 182 */       return Boolean.TRUE;
/*     */     }
/* 184 */     return null;
/*     */   }
/*     */ 
/*     */   protected void drawCandles(Graphics g, Color neutralColor, Color positiveColor, Color negativeColor, Color positiveBorderColor, Color negativeBorderColor)
/*     */   {
/* 189 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 191 */     g2d.setColor(positiveColor);
/* 192 */     if (isFillable())
/* 193 */       g2d.fill(this.positivePath);
/*     */     else {
/* 195 */       g2d.draw(this.positivePath);
/*     */     }
/*     */ 
/* 198 */     g2d.setColor(negativeColor);
/* 199 */     if (isFillable())
/* 200 */       g2d.fill(this.negativePath);
/*     */     else {
/* 202 */       g2d.draw(this.negativePath);
/*     */     }
/*     */ 
/* 205 */     g2d.setColor(neutralColor);
/* 206 */     g2d.draw(this.neutralPath);
/*     */ 
/* 208 */     g2d.setColor(positiveBorderColor);
/* 209 */     g2d.draw(this.positiveBorderPath);
/*     */ 
/* 211 */     g2d.setColor(negativeBorderColor);
/* 212 */     g2d.draw(this.negativeBorderPath);
/*     */   }
/*     */ 
/*     */   protected void drawLastCandleMarker(Graphics g, JComponent jComponent, D lastBarData, long drawForTime)
/*     */   {
/* 221 */     if (lastBarData == null) {
/* 222 */       return;
/*     */     }
/*     */ 
/* 225 */     Graphics2D g2d = (Graphics2D)g;
/* 226 */     int x = 0;
/*     */ 
/* 228 */     if (!ChartSettings.getBoolean(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING))
/*     */     {
/* 230 */       x = this.timeToXMapper.xt(drawForTime);
/* 231 */       if (this.timeToXMapper.isXOutOfRange(x)) {
/* 232 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 236 */     int y = this.valueToYMapper.yv(lastBarData.close);
/*     */ 
/* 238 */     Stroke stroke = g2d.getStroke();
/*     */ 
/* 240 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE));
/* 241 */     g2d.setStroke(this.chartState.getTheme().getStroke(ITheme.StrokeElement.LAST_CANDLE_TRACKING_LINE_STROKE));
/* 242 */     g2d.drawLine(x + 1, y, jComponent.getWidth(), y);
/*     */ 
/* 244 */     g2d.setStroke(stroke);
/*     */   }
/*     */ 
/*     */   protected void drawLastCandleMarker(Graphics g, JComponent jComponent, D lastBarData) {
/* 248 */     if (lastBarData == null) {
/* 249 */       return;
/*     */     }
/* 251 */     drawLastCandleMarker(g, jComponent, lastBarData, lastBarData.time);
/*     */   }
/*     */ 
/*     */   protected boolean isFillable() {
/* 255 */     return this.fillable;
/*     */   }
/*     */ 
/*     */   protected GeneralPath getNeutralPath() {
/* 259 */     return this.neutralPath;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  39 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.AbstractPriceAggregationVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */