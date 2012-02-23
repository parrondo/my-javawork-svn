/*     */ package com.dukascopy.charts.view.drawingstrategies;
/*     */ 
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.LevelInfo;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.indicators.IDrawingIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*     */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.IndicatorLevelDrawingHelper;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public abstract class AbstractIndicatorDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data> extends IndicatorDrawingStrategy
/*     */ {
/*     */   private static final int OFFSET_TO_LABEL = 5;
/*     */   private final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   private final AbstractIndicatorDrawingSupport<DataSequenceClass, DataClass> indicatorDrawingSupport;
/*     */   private final DrawingsLabelHelper drawingsLabelHelper;
/*     */   private final FormattersManager formattersManager;
/*     */   private final ChartState chartState;
/*     */   private final ITimeToXMapper timeToXMapper;
/*     */   private final IValueToYMapper valueToYMapper;
/*     */   private Map<Integer, double[]> averageFormulaOutputs;
/*     */ 
/*     */   public AbstractIndicatorDrawingStrategy(AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator, IValueToYMapper valueToYMapper, ITimeToXMapper timeToXMapper, PathHelper pathHelper, DrawingsLabelHelper drawingsLabelHelper, FormattersManager formattersManager, ChartState chartState)
/*     */   {
/*  78 */     super(pathHelper, chartState);
/*     */ 
/*  80 */     this.dataSequenceProvider = dataSequenceProvider;
/*  81 */     this.indicatorDrawingSupport = createIndicatorDrawingSupport(geometryCalculator);
/*  82 */     this.indicatorDrawingSupport.setValueToYMapper(valueToYMapper);
/*  83 */     this.indicatorDrawingSupport.setTimeToXMapper(timeToXMapper);
/*  84 */     this.drawingsLabelHelper = drawingsLabelHelper;
/*  85 */     this.formattersManager = formattersManager;
/*  86 */     this.chartState = chartState;
/*     */ 
/*  88 */     this.timeToXMapper = timeToXMapper;
/*  89 */     this.valueToYMapper = valueToYMapper;
/*  90 */     this.averageFormulaOutputs = Collections.synchronizedMap(new HashMap());
/*     */   }
/*     */ 
/*     */   protected abstract AbstractIndicatorDrawingSupport<DataSequenceClass, DataClass> createIndicatorDrawingSupport(GeometryCalculator paramGeometryCalculator);
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent)
/*     */   {
/*  98 */     this.pathHelper.resetIndicatorPaths();
/*     */ 
/* 100 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/* 101 */     if (dataSequence.isEmpty()) {
/* 102 */       return;
/*     */     }
/* 104 */     this.indicatorDrawingSupport.setChartData(this.dataSequenceProvider.getInstrument(), this.dataSequenceProvider.getPeriod(), this.dataSequenceProvider.getOfferSide(), dataSequence, true);
/* 105 */     List indicators = this.dataSequenceProvider.getIndicators();
/* 106 */     for (IndicatorWrapper indicatorWrapper : indicators)
/*     */     {
/* 111 */       if (!indicatorWrapper.getIndicator().getIndicatorInfo().isOverChart()) {
/*     */         continue;
/*     */       }
/* 114 */       Object[] formulaOutputs = dataSequence.getFormulaOutputs(indicatorWrapper.getId());
/* 115 */       if (formulaOutputs == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 119 */       int[] shifts = indicatorWrapper.getOutputShifts();
/* 120 */       if (indicatorWrapper.isLevelsEnabled()) {
/* 121 */         this.averageFormulaOutputs.put(Integer.valueOf(indicatorWrapper.getId()), this.indicatorLevelDrawingHelper.getAverageFormulaOutput(formulaOutputs, shifts));
/*     */       }
/*     */ 
/* 124 */       IIndicator indicator = indicatorWrapper.getIndicator();
/* 125 */       IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/*     */ 
/* 127 */       List[] indicatorPaths = new List[indicatorInfo.getNumberOfOutputs()];
/* 128 */       Map indicatorHandlePaths = new HashMap();
/* 129 */       Map indicatorLevelsPath = new HashMap();
/*     */ 
/* 132 */       Color[] outputColors = indicatorWrapper.getOutputColors();
/* 133 */       Color[] outputColors2 = indicatorWrapper.getOutputColors2();
/*     */ 
/* 135 */       int i = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); i < j; i++)
/*     */       {
/* 137 */         if (indicatorWrapper.getShowOutputs()[i] == 0)
/*     */         {
/*     */           continue;
/*     */         }
/* 141 */         List currentOutputPath = new ArrayList();
/*     */ 
/* 143 */         OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(i);
/* 144 */         int shift = indicatorWrapper.getOutputShifts()[i];
/*     */ 
/* 146 */         Composite originalComposite = ((Graphics2D)g).getComposite();
/* 147 */         if (1.0F != indicatorWrapper.getOpacityAlphas()[i]) {
/* 148 */           setComposite(g, AlphaComposite.getInstance(3, indicatorWrapper.getOpacityAlphas()[i]));
/*     */         }
/*     */ 
/* 151 */         OutputParameterInfo.DrawingStyle style = indicatorWrapper.getDrawingStyles()[i];
/* 152 */         if (outputParameterInfo.isDrawnByIndicator()) {
/* 153 */           Method drawOutputMethod = indicatorWrapper.getSelfdrawingMethod();
/* 154 */           Point lastPoint = null;
/*     */           try {
/* 156 */             Stroke stroke = getStroke(style, indicatorWrapper.getLineWidths()[i]);
/* 157 */             Graphics graphicsCopy = g.create();
/*     */             try {
/* 159 */               this.indicatorDrawingSupport.setColor2(outputColors2[i]);
/* 160 */               if ((indicator instanceof IDrawingIndicator))
/* 161 */                 lastPoint = (Point)drawOutputMethod.invoke(indicator, new Object[] { graphicsCopy, Integer.valueOf(i), formulaOutputs[i], outputColors[i], stroke, this.indicatorDrawingSupport, currentOutputPath, indicatorHandlePaths });
/*     */               else {
/* 163 */                 lastPoint = (Point)drawOutputMethod.invoke(indicator, new Object[] { graphicsCopy, Integer.valueOf(i), formulaOutputs[i], outputColors[i], this.indicatorDrawingSupport, currentOutputPath, indicatorHandlePaths });
/*     */               }
/* 165 */               if ((indicatorWrapper.showValueOnChart(i)) && (indicatorInfo.isOverChart()) && 
/* 166 */                 (lastPoint != null))
/* 167 */                 drawLastValueLine(g, jComponent, indicatorWrapper, outputParameterInfo, i, lastPoint);
/*     */             }
/*     */             finally
/*     */             {
/* 171 */               graphicsCopy.dispose();
/*     */             }
/*     */           } catch (Throwable e) {
/* 174 */             NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Exception in drawOutput method: " + StrategyWrapper.representError(indicator, e), e, true);
/*     */           }
/* 176 */         } else if ((style == OutputParameterInfo.DrawingStyle.PATTERN_BOOL) || (style == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) || (style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH))
/*     */         {
/* 180 */           if (outputParameterInfo.getType() != OutputParameterInfo.Type.INT) {
/* 181 */             setComposite(g, originalComposite);
/* 182 */             continue;
/*     */           }
/*     */ 
/* 185 */           drawCandlestickOutput(g, i, formulaOutputs[i], this.indicatorDrawingSupport, indicatorWrapper, currentOutputPath);
/*     */         }
/* 193 */         else if ((indicatorInfo.isOverChart()) && (
/* 194 */           (style == OutputParameterInfo.DrawingStyle.LINE) || (style == OutputParameterInfo.DrawingStyle.DASH_LINE) || (style == OutputParameterInfo.DrawingStyle.DASHDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.DASHDOTDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.DOT_LINE) || (style == OutputParameterInfo.DrawingStyle.DOTS) || (style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) || (style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN) || (style == OutputParameterInfo.DrawingStyle.LEVEL_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE)))
/*     */         {
/* 208 */           if (outputParameterInfo.getType() != OutputParameterInfo.Type.DOUBLE) {
/* 209 */             setComposite(g, originalComposite);
/* 210 */             continue;
/*     */           }
/* 212 */           Point lastPoint = drawSingleOutput(g, indicatorWrapper, (double[])(double[])formulaOutputs[i], i, shift, this.indicatorDrawingSupport, currentOutputPath, indicatorHandlePaths, indicatorLevelsPath);
/*     */ 
/* 224 */           if (indicatorWrapper.showValueOnChart(i)) {
/* 225 */             drawLastValueLine(g, jComponent, indicatorWrapper, outputParameterInfo, i, lastPoint);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 230 */         indicatorPaths[i] = currentOutputPath;
/*     */ 
/* 232 */         setComposite(g, originalComposite);
/*     */       }
/* 234 */       this.pathHelper.addIndicatorPathsFor(indicatorWrapper, indicatorPaths);
/* 235 */       this.pathHelper.addIndicatorHandlePathsFor(indicatorWrapper, indicatorHandlePaths);
/* 236 */       this.pathHelper.addIndicatorLevelsPath(indicatorWrapper, indicatorLevelsPath);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setComposite(Graphics g, Composite composite) {
/* 241 */     ((Graphics2D)g).setComposite(composite);
/*     */   }
/*     */ 
/*     */   private void drawCandlestickOutput(Graphics g, int outputIndex, Object objValues, IIndicatorDrawingSupport indicatorDrawingSupport, IndicatorWrapper indicatorWrapper, List<Shape> indicatorPath)
/*     */   {
/* 252 */     OutputParameterInfo outputParameterInfo = indicatorWrapper.getIndicator().getOutputParameterInfo(outputIndex);
/* 253 */     OutputParameterInfo.DrawingStyle style = indicatorWrapper.getDrawingStyles()[outputIndex];
/* 254 */     GeneralPath bullishPath = new GeneralPath();
/* 255 */     GeneralPath bearishPath = null;
/* 256 */     GeneralPath gettingBullishPath = null;
/* 257 */     GeneralPath gettingBearishPath = null;
/* 258 */     if (style == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) {
/* 259 */       bearishPath = new GeneralPath();
/*     */     }
/* 261 */     if (style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH) {
/* 262 */       bearishPath = new GeneralPath();
/* 263 */       gettingBullishPath = new GeneralPath();
/* 264 */       gettingBearishPath = new GeneralPath();
/*     */     }
/*     */ 
/* 267 */     int[] values = (int[])(int[])objValues;
/* 268 */     if (values == null) {
/* 269 */       return;
/*     */     }
/*     */ 
/* 272 */     int shift = indicatorWrapper.getOutputShifts()[outputIndex];
/*     */ 
/* 274 */     IBar[] candles = indicatorDrawingSupport.getCandles();
/* 275 */     boolean patternExists = false;
/* 276 */     int firstCandleIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
/* 277 */     int lastCandleIndex = firstCandleIndex + indicatorDrawingSupport.getNumberOfCandlesOnScreen() - 1;
/* 278 */     float candleWidth = indicatorDrawingSupport.getCandleWidthInPixels();
/* 279 */     float spaceBetweenCandles = indicatorDrawingSupport.getSpaceBetweenCandlesInPixels();
/* 280 */     int idx = firstCandleIndex - (shift > 0 ? shift : 0) - 1;
/* 281 */     if (idx < 0) {
/* 282 */       idx = 0;
/*     */     }
/* 284 */     int lastCalculatedIndex = lastCandleIndex - (shift < 0 ? shift : 0) + 1;
/* 285 */     if (lastCalculatedIndex > values.length - 1) {
/* 286 */       lastCalculatedIndex = values.length - 1;
/*     */     }
/* 288 */     for (; idx <= lastCalculatedIndex; idx++) {
/* 289 */       int value = values[idx];
/* 290 */       if ((values[idx] != -2147483648) && (values[idx] != 0)) {
/* 291 */         float middle = indicatorDrawingSupport.getMiddleOfCandle(idx + shift);
/* 292 */         if (Float.isNaN(middle)) {
/*     */           continue;
/*     */         }
/* 295 */         IBar bar = candles[idx];
/* 296 */         float leftPriceY = indicatorDrawingSupport.getYForValue(bar.getOpen());
/* 297 */         float rightPriceY = indicatorDrawingSupport.getYForValue(candles[idx].getClose());
/*     */ 
/* 299 */         float y = Math.min(leftPriceY, rightPriceY);
/* 300 */         float height = Math.abs(leftPriceY - rightPriceY) + 1.0F;
/*     */ 
/* 302 */         if ((style == OutputParameterInfo.DrawingStyle.PATTERN_BOOL) || ((style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH) && (value > 100)) || ((style == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) && (value > 0)))
/*     */         {
/* 304 */           drawBrackets(bullishPath, (int)middle, candleWidth, y, height, spaceBetweenCandles);
/* 305 */           patternExists = true;
/* 306 */         } else if (((style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH) && (value < -100)) || ((style == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) && (value < 0)))
/*     */         {
/* 308 */           drawBrackets(bearishPath, (int)middle, candleWidth, y, height, spaceBetweenCandles);
/* 309 */           patternExists = true;
/* 310 */         } else if ((style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH) && (value > 0) && (value <= 100)) {
/* 311 */           drawBrackets(gettingBullishPath, (int)middle, candleWidth, y, height, spaceBetweenCandles);
/* 312 */           patternExists = true;
/* 313 */         } else if ((style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH) && (value < 0) && (value >= -100)) {
/* 314 */           drawBrackets(gettingBearishPath, (int)middle, candleWidth, y, height, spaceBetweenCandles);
/* 315 */           patternExists = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 320 */     if (patternExists) {
/* 321 */       Graphics2D g2 = (Graphics2D)g;
/* 322 */       Stroke oldStroke = g2.getStroke();
/*     */ 
/* 324 */       g2.setStroke(new BasicStroke(candleWidth > 1.0F ? 2.0F : 1.0F));
/* 325 */       if ((style == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) || (style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH)) {
/* 326 */         if ((indicatorWrapper.getOutputColors() != null) && (indicatorWrapper.getOutputColors().length > outputIndex)) {
/* 327 */           g2.setColor(indicatorWrapper.getOutputColors()[outputIndex]);
/*     */         }
/*     */         else
/* 330 */           g2.setColor(Color.GREEN);
/*     */       }
/* 332 */       else if ((style == OutputParameterInfo.DrawingStyle.PATTERN_BOOL) && 
/* 333 */         (indicatorWrapper.getOutputColors() != null) && (indicatorWrapper.getOutputColors().length > outputIndex)) {
/* 334 */         g2.setColor(indicatorWrapper.getOutputColors()[outputIndex]);
/*     */       }
/*     */ 
/* 337 */       g2.draw(bullishPath);
/* 338 */       indicatorPath.add(bullishPath);
/*     */ 
/* 340 */       if ((style == OutputParameterInfo.DrawingStyle.PATTERN_BULL_BEAR) || (style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH)) {
/* 341 */         if ((indicatorWrapper.getOutputColors2() != null) && (indicatorWrapper.getOutputColors2().length > outputIndex)) {
/* 342 */           g2.setColor(indicatorWrapper.getOutputColors2()[outputIndex]);
/*     */         }
/*     */         else {
/* 345 */           g2.setColor(Color.RED);
/*     */         }
/* 347 */         g2.draw(bearishPath);
/* 348 */         indicatorPath.add(bearishPath);
/*     */       }
/* 350 */       if (style == OutputParameterInfo.DrawingStyle.PATTERN_STRENGTH) {
/* 351 */         if ((indicatorWrapper.getOutputColors() != null) && (indicatorWrapper.getOutputColors().length > outputIndex)) {
/* 352 */           g2.setColor(indicatorWrapper.getOutputColors()[outputIndex].brighter());
/*     */         }
/*     */         else {
/* 355 */           g2.setColor(Color.GREEN.brighter());
/*     */         }
/* 357 */         g2.draw(gettingBullishPath);
/* 358 */         indicatorPath.add(gettingBullishPath);
/* 359 */         if ((indicatorWrapper.getOutputColors2() != null) && (indicatorWrapper.getOutputColors2().length > outputIndex)) {
/* 360 */           g2.setColor(indicatorWrapper.getOutputColors2()[outputIndex].brighter());
/*     */         }
/*     */         else {
/* 363 */           g2.setColor(Color.RED.brighter());
/*     */         }
/* 365 */         g2.draw(gettingBearishPath);
/* 366 */         indicatorPath.add(gettingBearishPath);
/*     */       }
/* 368 */       g2.setStroke(oldStroke);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawBrackets(GeneralPath path, float middle, float candleWidth, float y, float height, float spaceBetweenCandles)
/*     */   {
/* 377 */     float halfWidth = (candleWidth - 1.0F) / 2.0F;
/* 378 */     float leftBracketSide = middle - halfWidth - 1.0F - (spaceBetweenCandles > 2.6F ? 1 : 0);
/* 379 */     float leftBracketAppendix = leftBracketSide + (int)(halfWidth / 2.0F + 0.5D);
/* 380 */     float bottomCoord = y + height + 1.0F + (candleWidth > 1.0F ? 1 : 0);
/* 381 */     path.moveTo(leftBracketAppendix, y - 2.0F);
/* 382 */     path.lineTo(leftBracketSide, y - 2.0F);
/* 383 */     path.lineTo(leftBracketSide, bottomCoord);
/* 384 */     path.lineTo(leftBracketAppendix, bottomCoord);
/*     */ 
/* 386 */     float rightBracketSide = middle + halfWidth + 1.0F + (spaceBetweenCandles > 2.6F ? 1 : 0) + (candleWidth > 1.0F ? 1 : 0);
/* 387 */     float rightBracketAppendix = rightBracketSide - (int)(halfWidth / 2.0F + 0.5D);
/* 388 */     path.moveTo(rightBracketAppendix, y - 2.0F);
/* 389 */     path.lineTo(rightBracketSide, y - 2.0F);
/* 390 */     path.lineTo(rightBracketSide, bottomCoord);
/* 391 */     path.lineTo(rightBracketAppendix, bottomCoord);
/*     */   }
/*     */ 
/*     */   private Point drawSingleOutput(Graphics g, IndicatorWrapper indicatorWrapper, double[] values, int outputIdx, int shift, IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> indicatorPath, Map<Color, List<Point>> indicatorHandlePath, Map<LevelInfo, Shape> indicatorLevelsPath)
/*     */   {
/* 406 */     OutputParameterInfo.DrawingStyle style = indicatorWrapper.getDrawingStyles()[outputIdx];
/* 407 */     Color color = indicatorWrapper.getOutputColors()[outputIdx];
/* 408 */     Color color2 = indicatorWrapper.getOutputColors2()[outputIdx];
/*     */ 
/* 410 */     Point lastPoint = null;
/*     */ 
/* 412 */     if ((style.isOutputAsLine()) && (color != null) && (color2 != null) && (!color.equals(color2)))
/*     */     {
/* 414 */       GeneralPath path = new GeneralPath();
/* 415 */       List handlesPoints = new ArrayList();
/* 416 */       boolean initialized = false;
/* 417 */       boolean positive = false;
/*     */ 
/* 419 */       int lastValueIndex = -1;
/* 420 */       int lastStartIndex = 0;
/* 421 */       for (int i = 0; i < values.length; i++) {
/* 422 */         double dValue = values[i];
/* 423 */         double prevValue = lastValueIndex >= 0 ? values[lastValueIndex] : (0.0D / 0.0D);
/* 424 */         Color workColor = positive ? color : color2;
/* 425 */         if (!Double.isNaN(dValue)) {
/* 426 */           if ((i > 0) && (!initialized)) {
/* 427 */             initialized = true;
/* 428 */             positive = (Double.isNaN(prevValue)) || (dValue >= prevValue);
/* 429 */             workColor = positive ? color : color2;
/*     */           }
/* 431 */           if ((!Double.isNaN(prevValue)) && (
/* 432 */             ((dValue > prevValue) && (!positive)) || ((dValue < prevValue) && (positive)) || (i == values.length - 1)))
/*     */           {
/* 435 */             positive = !positive;
/* 436 */             int length = lastValueIndex - lastStartIndex;
/* 437 */             if (length > 0) {
/* 438 */               lastPoint = drawSingleOutput(g, indicatorWrapper, values, workColor, outputIdx, indicatorDrawingSupport, indicatorPath, indicatorHandlePath, indicatorLevelsPath, path, handlesPoints, lastStartIndex, length, length == values.length, shift);
/*     */             }
/*     */ 
/* 455 */             lastStartIndex = lastValueIndex;
/*     */           }
/*     */ 
/* 458 */           lastValueIndex = i;
/*     */         }
/* 460 */         if (i == values.length - 1) {
/* 461 */           int length = i - lastStartIndex;
/* 462 */           if (length > 0) {
/* 463 */             lastPoint = drawSingleOutput(g, indicatorWrapper, values, workColor, outputIdx, indicatorDrawingSupport, indicatorPath, indicatorHandlePath, indicatorLevelsPath, path, handlesPoints, lastStartIndex, length, true, shift);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 483 */       return lastPoint;
/*     */     }
/* 485 */     lastPoint = drawSingleOutput(g, indicatorWrapper, values, color, outputIdx, indicatorDrawingSupport, indicatorPath, indicatorHandlePath, indicatorLevelsPath, this.tmpPath, this.tmpHandlesPoints, 0, values.length, true, shift);
/*     */ 
/* 502 */     this.averageFormulaOutputs.remove(Integer.valueOf(indicatorWrapper.getId()));
/* 503 */     return lastPoint;
/*     */   }
/*     */ 
/*     */   private Point drawSingleOutput(Graphics g, IndicatorWrapper indicatorWrapper, double[] values, Color color, int outputIdx, IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> indicatorPath, Map<Color, List<Point>> indicatorHandlePath, Map<LevelInfo, Shape> indicatorLevelsPath, GeneralPath path, List<Point> handlesPoints, int startIndex, int length, boolean lastOutput, int shift)
/*     */   {
/* 524 */     OutputParameterInfo outputParameterInfo = indicatorWrapper.getIndicator().getOutputParameterInfo(outputIdx);
/*     */ 
/* 526 */     int lineWidth = indicatorWrapper.getLineWidths()[outputIdx];
/* 527 */     OutputParameterInfo.DrawingStyle style = indicatorWrapper.getDrawingStyles()[outputIdx];
/*     */ 
/* 529 */     IIndicator indicator = indicatorWrapper.getIndicator();
/*     */ 
/* 531 */     double lastX = -1.0D;
/* 532 */     double lastY = -1.0D;
/*     */ 
/* 534 */     path.reset();
/* 535 */     handlesPoints.clear();
/*     */ 
/* 537 */     double[] averageOutput = (double[])this.averageFormulaOutputs.get(Integer.valueOf(indicatorWrapper.getId()));
/*     */ 
/* 539 */     List indicatorLevelDrawingBeans = new ArrayList(indicatorWrapper.getLevelInfoList().size());
/*     */ 
/* 541 */     if (averageOutput != null) {
/* 542 */       for (LevelInfo levelInfo : indicatorWrapper.getLevelInfoList())
/*     */       {
/*     */         IndicatorLevelDrawingHelper tmp146_143 = this.indicatorLevelDrawingHelper; tmp146_143.getClass(); indicatorLevelDrawingBeans.add(new IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean(tmp146_143, levelInfo));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 549 */     boolean plottingStarted = false;
/*     */ 
/* 551 */     if ((style == OutputParameterInfo.DrawingStyle.LEVEL_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE))
/*     */     {
/* 557 */       if (indicatorDrawingSupport.isLastCandleInProgress())
/* 558 */         for (int i = values.length - 1; i >= 0; i--) {
/* 559 */           double dValue = values[i];
/*     */ 
/* 561 */           if (!Double.isNaN(dValue)) {
/* 562 */             int y = (int)indicatorDrawingSupport.getYForValue(dValue);
/* 563 */             plottingStarted = true;
/*     */ 
/* 565 */             Font prevFont = g.getFont();
/* 566 */             Color prevColor = g.getColor();
/*     */ 
/* 568 */             g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.DEFAULT));
/* 569 */             g.setColor(color);
/* 570 */             Rectangle labelDimension = this.drawingsLabelHelper.drawPriceMarkerLabelAndTextLabel(g, null, null, dValue, indicatorDrawingSupport.getChartWidth(), 1.0F, y, this.formattersManager.getValueFormatter());
/*     */ 
/* 572 */             g.setFont(prevFont);
/* 573 */             g.setColor(prevColor);
/*     */ 
/* 575 */             path.moveTo(0.0F, y);
/* 576 */             path.lineTo(labelDimension.x - 5, y);
/* 577 */             path.moveTo(labelDimension.x + labelDimension.width + 3, y);
/* 578 */             path.lineTo(indicatorDrawingSupport.getChartWidth(), y);
/* 579 */             break;
/*     */           }
/*     */         }
/*     */     }
/*     */     else {
/* 584 */       int last = indicatorDrawingSupport.getNumberOfCandlesOnScreen();
/* 585 */       int firstCandleIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
/* 586 */       int lastCandleIndex = firstCandleIndex + last - 1;
/* 587 */       double oneFourth = last > 5 ? last / 3.0D : last > 20 ? last / 4.0D : last / 2.0D;
/*     */ 
/* 589 */       char arrowChar = 'x';
/* 590 */       int xCorrection = 0;
/* 591 */       int yCorrection = 0;
/* 592 */       if ((style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) || (style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN)) {
/* 593 */         arrowChar = indicator.getOutputParameterInfo(outputIdx).getArrowSymbol();
/* 594 */         Font font = new Font(g.getFont().getName(), 0, 9);
/* 595 */         FontMetrics fontMetrics = g.getFontMetrics(font);
/* 596 */         float candleWidth = indicatorDrawingSupport.getCandleWidthInPixels();
/* 597 */         while (fontMetrics.charWidth(arrowChar) < candleWidth) {
/* 598 */           font = new Font(g.getFont().getName(), 0, font.getSize() + 1);
/* 599 */           fontMetrics = g.getFontMetrics(font);
/*     */         }
/* 601 */         g.setFont(font);
/* 602 */         g.setColor(color);
/* 603 */         xCorrection = -fontMetrics.charWidth(arrowChar) / 2;
/* 604 */         if (style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP)
/* 605 */           yCorrection = fontMetrics.getAscent();
/*     */         else {
/* 607 */           yCorrection = -fontMetrics.getDescent();
/*     */         }
/*     */       }
/*     */ 
/* 611 */       int idx = startIndex;
/* 612 */       idx += (startIndex == 0 ? firstCandleIndex - (shift > 0 ? shift : 0) - 1 : 0);
/*     */ 
/* 614 */       if (idx < 0) {
/* 615 */         idx = 0;
/*     */       }
/*     */ 
/* 620 */       int lastCalculatedIndex = lastCandleIndex - (shift < 0 ? shift : 0) + 1;
/* 621 */       if (startIndex + length < values.length) {
/* 622 */         lastCalculatedIndex = startIndex + length;
/*     */       }
/*     */ 
/* 627 */       if (indicator.getIndicatorInfo().isSparseIndicator()) {
/* 628 */         idx = 0;
/* 629 */         idx += startIndex;
/* 630 */         lastCalculatedIndex = startIndex + length;
/*     */       }
/*     */ 
/* 633 */       if (lastCalculatedIndex > values.length - 1) {
/* 634 */         lastCalculatedIndex = values.length - 1;
/*     */       }
/* 636 */       boolean gap = false;
/* 637 */       boolean gapAtNaN = indicator.getOutputParameterInfo(outputIdx).isGapAtNaN();
/*     */ 
/* 639 */       for (; idx <= lastCalculatedIndex; idx++) {
/* 640 */         double dValue = values[idx];
/* 641 */         int idxFromFirst = idx - firstCandleIndex;
/*     */ 
/* 643 */         if (!Double.isNaN(dValue)) {
/* 644 */           if (dValue < 0.0D) {
/* 645 */             dValue = -dValue;
/*     */           }
/* 647 */           float middle = indicatorDrawingSupport.getMiddleOfCandle(idx + shift);
/*     */ 
/* 649 */           if (Double.isNaN(middle)) {
/*     */             continue;
/*     */           }
/* 652 */           int y = (int)indicatorDrawingSupport.getYForValue(dValue);
/*     */ 
/* 654 */           int[] levelYList = new int[indicatorLevelDrawingBeans.size()];
/* 655 */           if (averageOutput != null) {
/* 656 */             double averageValue = averageOutput[idx];
/* 657 */             if (!Double.isNaN(averageValue)) {
/* 658 */               if (averageValue < 0.0D) {
/* 659 */                 averageValue = -averageValue;
/*     */               }
/* 661 */               for (int i = 0; i < indicatorLevelDrawingBeans.size(); i++) {
/* 662 */                 levelYList[i] = (int)indicatorDrawingSupport.getYForValue(averageValue + this.chartState.getInstrument().getPipValue() * ((IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean)indicatorLevelDrawingBeans.get(i)).getLevelInfo().getValue());
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 667 */           float newX = -1.0F;
/* 668 */           int newY = -1;
/*     */ 
/* 670 */           if ((style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) || (style == OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN)) {
/* 671 */             g.drawString(Character.toString(arrowChar), (int)(middle + xCorrection), y + yCorrection);
/*     */ 
/* 673 */             newX = middle + xCorrection;
/* 674 */             newY = y + yCorrection;
/*     */           }
/*     */ 
/* 678 */           IndicatorInfo info = indicator.getIndicatorInfo();
/* 679 */           boolean isZigZagIndicator = (info != null) && (info.getName() != null) && (info.getName().equalsIgnoreCase("ZIGZAG"));
/* 680 */           if ((idxFromFirst == (int)oneFourth) || ((last > 5) && (idxFromFirst == (int)(oneFourth * 2.0D))) || ((last > 20) && (idxFromFirst == (int)(oneFourth * 3.0D)))) {
/* 681 */             if (!isZigZagIndicator)
/* 682 */               handlesPoints.add(new Point((int)middle, y));
/*     */           }
/* 684 */           else if (style == OutputParameterInfo.DrawingStyle.DOTS) {
/* 685 */             path.moveTo(middle - 2.0F, y - 1);
/* 686 */             path.lineTo(middle + 1.0F, y - 1);
/* 687 */             path.lineTo(middle + 1.0F, y + 2);
/* 688 */             path.lineTo(middle - 2.0F, y + 2);
/* 689 */             path.lineTo(middle - 2.0F, y - 1);
/* 690 */             if (!plottingStarted) {
/* 691 */               plottingStarted = true;
/*     */             }
/* 693 */             newX = middle + 1.0F;
/* 694 */             newY = y + 2;
/*     */           }
/* 696 */           if ((style != OutputParameterInfo.DrawingStyle.DOTS) && (style != OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP) && (style != OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN)) {
/* 697 */             if ((!plottingStarted) || ((gapAtNaN) && (gap))) {
/* 698 */               path.moveTo(middle, y);
/* 699 */               plottingStarted = true;
/* 700 */               gap = false;
/* 701 */               for (int i = 0; i < levelYList.length; i++)
/* 702 */                 ((IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean)indicatorLevelDrawingBeans.get(i)).getLevelPath().moveTo(middle, levelYList[i]);
/*     */             }
/*     */             else {
/* 705 */               path.lineTo(middle, y);
/* 706 */               for (int i = 0; i < levelYList.length; i++) {
/* 707 */                 ((IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean)indicatorLevelDrawingBeans.get(i)).getLevelPath().lineTo(middle, levelYList[i]);
/*     */               }
/*     */             }
/*     */ 
/* 711 */             if ((isZigZagIndicator) && (y >= 0) && (middle >= 0.0F)) {
/* 712 */               handlesPoints.add(new Point((int)middle, y));
/*     */             }
/*     */ 
/* 715 */             newX = middle;
/* 716 */             newY = y;
/*     */           }
/*     */ 
/* 719 */           if (lastX < newX) {
/* 720 */             lastX = newX;
/* 721 */             lastY = newY;
/*     */           }
/*     */ 
/*     */         }
/* 726 */         else if (!plottingStarted) {
/* 727 */           oneFourth = last > 5 ? (last + idxFromFirst) / 3.0D : last > 20 ? (last + idxFromFirst) / 4.0D : (last + idxFromFirst) / 2.0D;
/*     */         }
/*     */         else {
/* 730 */           gap = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 735 */     if (lastOutput) {
/* 736 */       for (int i = 0; i < indicatorLevelDrawingBeans.size(); i++) {
/* 737 */         IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean indicatorLevelDrawingBean = (IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean)indicatorLevelDrawingBeans.get(i);
/* 738 */         indicatorLevelDrawingBean.setLastX((int)lastX);
/* 739 */         double lastYValue = this.valueToYMapper.vy((int)lastY);
/* 740 */         indicatorLevelDrawingBean.setLastY((int)indicatorDrawingSupport.getYForValue(lastYValue + this.chartState.getInstrument().getPipValue() * ((IndicatorLevelDrawingHelper.IndicatorLevelDrawingBean)indicatorLevelDrawingBeans.get(i)).getLevelInfo().getValue()));
/*     */       }
/*     */     }
/*     */ 
/* 744 */     if (plottingStarted) {
/* 745 */       render(g, style, indicatorPath, indicatorHandlePath, color, lineWidth, path, handlesPoints);
/* 746 */       this.indicatorLevelDrawingHelper.drawLevelsForIndicator(g, indicatorLevelDrawingBeans, indicatorLevelsPath, lastOutput);
/*     */     }
/*     */ 
/* 749 */     Point result = new Point();
/* 750 */     result.setLocation(lastX, lastY);
/* 751 */     return result;
/*     */   }
/*     */ 
/*     */   private void render(Graphics g, OutputParameterInfo.DrawingStyle style, List<Shape> indicatorPath, Map<Color, List<Point>> indicatorHandlePath, Color color, int strokeWidth, GeneralPath path, List<Point> handlesPoints) {
/* 755 */     Graphics2D g2 = (Graphics2D)g;
/* 756 */     Stroke oldStroke = g2.getStroke();
/*     */ 
/* 758 */     Stroke stroke = getStroke(style, strokeWidth);
/* 759 */     if (stroke != null) {
/* 760 */       g2.setStroke(stroke);
/*     */     }
/*     */ 
/* 763 */     g2.setColor(color);
/* 764 */     if (style == OutputParameterInfo.DrawingStyle.DOTS)
/* 765 */       g2.fill(path);
/*     */     else {
/* 767 */       g2.draw(path);
/*     */     }
/*     */ 
/* 770 */     indicatorPath.add((Shape)path.clone());
/* 771 */     List points = (List)indicatorHandlePath.get(color);
/* 772 */     List currentPoints = new ArrayList(handlesPoints);
/* 773 */     if (points != null)
/* 774 */       points.addAll(currentPoints);
/*     */     else {
/* 776 */       points = currentPoints;
/*     */     }
/* 778 */     indicatorHandlePath.put(color, points);
/* 779 */     g2.setStroke(oldStroke);
/*     */   }
/*     */ 
/*     */   private void drawLastValueLine(Graphics g, JComponent component, IndicatorWrapper wrapper, OutputParameterInfo output, int outputIdx, Point point)
/*     */   {
/* 791 */     OutputParameterInfo.DrawingStyle style = wrapper.getDrawingStyles()[outputIdx];
/* 792 */     if ((style == OutputParameterInfo.DrawingStyle.LEVEL_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE))
/*     */     {
/* 798 */       return;
/*     */     }
/*     */ 
/* 801 */     Graphics2D g2d = (Graphics2D)g;
/* 802 */     int x = -1;
/* 803 */     if (point != null) {
/* 804 */       x = point.x;
/*     */     } else {
/* 806 */       long time = this.dataSequenceProvider.getLastTickTime();
/* 807 */       x = this.timeToXMapper.xt(time);
/*     */     }
/* 809 */     if ((point == null) || (x <= 0)) {
/* 810 */       return;
/*     */     }
/* 812 */     if (this.timeToXMapper.isXOutOfRange(x + 1)) {
/* 813 */       return;
/*     */     }
/*     */ 
/* 816 */     if (output.getType() == OutputParameterInfo.Type.OBJECT) {
/* 817 */       return;
/*     */     }
/*     */ 
/* 820 */     Object lastValue = this.dataSequenceProvider.getIndicatorLatestValue(wrapper.getId(), outputIdx);
/*     */ 
/* 822 */     if (lastValue == null)
/* 823 */       return;
/* 824 */     if (((lastValue instanceof Double)) && (
/* 825 */       (((Double)lastValue).isNaN()) || ((output.isGapAtNaN()) && (this.indicatorDrawingSupport.getYForValue(((Double)lastValue).doubleValue()) != point.y))))
/*     */     {
/* 827 */       return;
/*     */     }
/*     */ 
/* 831 */     if (this.valueToYMapper.isYOutOfRange(point.y)) {
/* 832 */       return;
/*     */     }
/*     */ 
/* 835 */     Stroke stroke = g2d.getStroke();
/*     */ 
/* 837 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE));
/* 838 */     g2d.setStroke(this.chartState.getTheme().getStroke(ITheme.StrokeElement.LAST_CANDLE_TRACKING_LINE_STROKE));
/* 839 */     g2d.drawLine(x + 1, point.y, component.getWidth(), point.y);
/*     */ 
/* 841 */     g2d.setStroke(stroke);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.AbstractIndicatorDrawingStrategy
 * JD-Core Version:    0.6.0
 */