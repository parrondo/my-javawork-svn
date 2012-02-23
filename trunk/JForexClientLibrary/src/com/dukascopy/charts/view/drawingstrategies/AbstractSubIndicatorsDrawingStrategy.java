/*     */ package com.dukascopy.charts.view.drawingstrategies;
/*     */ 
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.indicators.IDrawingIndicator;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.chartbuilder.SubIndicatorGroup;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.drawings.DrawingsLabelHelper;
/*     */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.IndicatorLevelDrawingHelper;
/*     */ import com.dukascopy.charts.view.drawingstrategies.util.SharedValueToYMapperProvider;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractSubIndicatorsDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data> extends IndicatorDrawingStrategy
/*     */ {
/*  53 */   protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSubIndicatorsDrawingStrategy.class);
/*     */   private static final int OFFSET_TO_LABEL = 5;
/*     */   private final SubIndicatorGroup subIndicatorGroup;
/*     */   private final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   private final SubValueToYMapper subValueToYMapper;
/*     */   private final AbstractIndicatorDrawingSupport<DataSequenceClass, DataClass> indicatorDrawingSupport;
/*     */   private final ITimeToXMapper timeToXMapper;
/*     */   private final ChartState chartState;
/*  63 */   private final Map<Color, List<Point>> dummyHandles = new HashMap(0);
/*     */   private final DrawingsLabelHelper drawingsLabelHelper;
/*     */   private final FormattersManager formattersManager;
/*     */ 
/*     */   public AbstractSubIndicatorsDrawingStrategy(SubIndicatorGroup subIndicatorGroup, GeometryCalculator geometryCalculator, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, SubValueToYMapper subCandlesValueToYMapper, ITimeToXMapper timeToXMapper, PathHelper pathHelper, DrawingsLabelHelper drawingsLabelHelper, FormattersManager formattersManager, ChartState chartState)
/*     */   {
/*  79 */     super(pathHelper, chartState);
/*     */ 
/*  81 */     this.subIndicatorGroup = subIndicatorGroup;
/*  82 */     this.dataSequenceProvider = dataSequenceProvider;
/*  83 */     this.subValueToYMapper = subCandlesValueToYMapper;
/*  84 */     this.indicatorDrawingSupport = createIndicatorSupport(geometryCalculator);
/*  85 */     this.indicatorDrawingSupport.setTimeToXMapper(timeToXMapper);
/*  86 */     this.timeToXMapper = timeToXMapper;
/*  87 */     this.chartState = chartState;
/*     */ 
/*  89 */     this.drawingsLabelHelper = drawingsLabelHelper;
/*  90 */     this.formattersManager = formattersManager;
/*     */   }
/*     */ 
/*     */   protected abstract AbstractIndicatorDrawingSupport<DataSequenceClass, DataClass> createIndicatorSupport(GeometryCalculator paramGeometryCalculator);
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  97 */     Color color = g.getColor();
/*  98 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/* 100 */     if (dataSequence.isEmpty()) {
/* 101 */       return;
/*     */     }
/* 103 */     this.indicatorDrawingSupport.setChartData(this.dataSequenceProvider.getInstrument(), this.dataSequenceProvider.getPeriod(), this.dataSequenceProvider.getOfferSide(), dataSequence, false);
/*     */ 
/* 105 */     for (IndicatorWrapper indicatorWrapper : this.subIndicatorGroup.getSubIndicators()) {
/* 106 */       Object[] formulaOutputs = dataSequence.getFormulaOutputs(indicatorWrapper.getId());
/* 107 */       if ((formulaOutputs == null) || (formulaOutputs.length == 0))
/*     */       {
/*     */         continue;
/*     */       }
/* 111 */       IValueToYMapper subValueToYMapper = SharedValueToYMapperProvider.getSharedValueToYMapper(indicatorWrapper, this.subIndicatorGroup, this.subValueToYMapper);
/*     */ 
/* 114 */       if (subValueToYMapper == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 118 */       this.indicatorLevelDrawingHelper.drawLevelsForSubIndicator(g, subValueToYMapper, indicatorWrapper, jComponent.getWidth(), jComponent.getHeight());
/* 119 */       this.indicatorDrawingSupport.setValueToYMapper(subValueToYMapper);
/* 120 */       drawIndicator(g, jComponent, formulaOutputs, indicatorWrapper, subValueToYMapper);
/*     */     }
/*     */ 
/* 129 */     g.setColor(color);
/*     */   }
/*     */ 
/*     */   private Shape[] drawIndicator(Graphics g, JComponent component, Object[] outputValues, IndicatorWrapper indicatorWrapper, IValueToYMapper subValueToYMapper)
/*     */   {
/* 139 */     Color[] outputColors = indicatorWrapper.getOutputColors();
/* 140 */     Color[] outputColors2 = indicatorWrapper.getOutputColors2();
/* 141 */     OutputParameterInfo.DrawingStyle[] drawingStyles = indicatorWrapper.getDrawingStyles();
/* 142 */     int[] lineWidths = indicatorWrapper.getLineWidths();
/* 143 */     IIndicator indicator = indicatorWrapper.getIndicator();
/* 144 */     IndicatorInfo indicatorInfo = indicator.getIndicatorInfo();
/* 145 */     Method selfdrawingMethod = indicatorWrapper.getSelfdrawingMethod();
/*     */ 
/* 147 */     ArrayList shapes = new ArrayList();
/*     */ 
/* 149 */     Composite originalComposite = ((Graphics2D)g).getComposite();
/*     */ 
/* 151 */     int outputIdx = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); outputIdx < j; outputIdx++)
/*     */     {
/* 153 */       OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(outputIdx);
/* 154 */       Object values = outputValues[outputIdx];
/* 155 */       if (outputParameterInfo.isDrawnByIndicator()) {
/* 156 */         float alpha = indicatorWrapper.getOpacityAlphas()[outputIdx];
/* 157 */         if (1.0F != alpha) {
/* 158 */           setComposite(g, AlphaComposite.getInstance(3, alpha));
/*     */         }
/*     */ 
/* 161 */         Point lastPoint = invokeSelfDrawingMethod(selfdrawingMethod, g, outputColors, outputColors2, drawingStyles, lineWidths, indicator, shapes, outputIdx, values);
/* 162 */         if ((!indicatorInfo.isOverChart()) && (indicatorWrapper.showValueOnChart(outputIdx)) && (indicatorWrapper.showOutput(outputIdx)))
/* 163 */           drawLastValueLine(g, component, indicatorWrapper, outputParameterInfo, outputIdx, lastPoint, subValueToYMapper);
/*     */       }
/*     */       else
/*     */       {
/* 167 */         if (!indicatorWrapper.showOutput(outputIdx))
/*     */         {
/*     */           continue;
/*     */         }
/* 171 */         OutputParameterInfo.DrawingStyle drawingStyle = drawingStyles[outputIdx];
/* 172 */         if (drawingStyle == OutputParameterInfo.DrawingStyle.HISTOGRAM)
/*     */         {
/*     */           boolean doubles;
/*     */           int length;
/*     */           boolean doubles;
/* 175 */           if (outputParameterInfo.getType() == OutputParameterInfo.Type.DOUBLE) {
/* 176 */             int length = ((double[])(double[])values).length;
/* 177 */             doubles = true; } else {
/* 178 */             if (outputParameterInfo.getType() != OutputParameterInfo.Type.INT) continue;
/* 179 */             length = ((int[])(int[])values).length;
/* 180 */             doubles = false;
/*     */           }
/*     */ 
/* 184 */           if (length == 0) {
/* 185 */             return (Shape[])shapes.toArray(new Shape[shapes.size()]);
/*     */           }
/*     */ 
/* 188 */           float alpha = indicatorWrapper.getOpacityAlphas()[outputIdx];
/* 189 */           if (1.0F != alpha) {
/* 190 */             setComposite(g, AlphaComposite.getInstance(3, alpha));
/*     */           }
/*     */ 
/* 193 */           Point lastPoint = draw(g, values, outputColors[outputIdx], outputColors2[outputIdx], 1, doubles, drawingStyle, shapes, outputParameterInfo, indicator.getIndicatorInfo(), indicatorWrapper.getOutputShifts()[outputIdx]);
/*     */ 
/* 206 */           if ((!indicatorInfo.isOverChart()) && (indicatorWrapper.showValueOnChart(outputIdx))) {
/* 207 */             drawLastValueLine(g, component, indicatorWrapper, outputParameterInfo, outputIdx, lastPoint, subValueToYMapper);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 213 */       setComposite(g, originalComposite);
/*     */     }
/*     */ 
/* 216 */     int outputIdx = 0; for (int j = indicator.getIndicatorInfo().getNumberOfOutputs(); outputIdx < j; outputIdx++)
/*     */     {
/* 218 */       if (!indicatorWrapper.showOutput(outputIdx))
/*     */       {
/*     */         continue;
/*     */       }
/* 222 */       OutputParameterInfo outputParameterInfo = indicator.getOutputParameterInfo(outputIdx);
/* 223 */       OutputParameterInfo.DrawingStyle drawingStyle = drawingStyles[outputIdx];
/* 224 */       if ((outputParameterInfo.isDrawnByIndicator()) || 
/* 225 */         (drawingStyle == null) || (
/* 225 */         (drawingStyle != OutputParameterInfo.DrawingStyle.DASH_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.DASHDOT_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.DASHDOTDOT_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.DOT_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.DOTS) && (drawingStyle != OutputParameterInfo.DrawingStyle.LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.LEVEL_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE) && (drawingStyle != OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE)))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 241 */       Object values = outputValues[outputIdx];
/*     */       boolean doubles;
/*     */       int length;
/*     */       boolean doubles;
/* 244 */       if (outputParameterInfo.getType() == OutputParameterInfo.Type.DOUBLE) {
/* 245 */         int length = ((double[])(double[])values).length;
/* 246 */         doubles = true;
/*     */       } else {
/* 248 */         if (outputParameterInfo.getType() != OutputParameterInfo.Type.INT) continue;
/* 249 */         length = ((int[])(int[])values).length;
/* 250 */         doubles = false;
/*     */       }
/*     */ 
/* 256 */       if (length == 0) {
/* 257 */         return (Shape[])shapes.toArray(new Shape[shapes.size()]);
/*     */       }
/*     */ 
/* 260 */       float alpha = indicatorWrapper.getOpacityAlphas()[outputIdx];
/* 261 */       if (1.0F != alpha) {
/* 262 */         setComposite(g, AlphaComposite.getInstance(3, alpha));
/*     */       }
/*     */ 
/* 265 */       Point lastPoint = draw(g, values, outputColors[outputIdx], outputColors2[outputIdx], lineWidths[outputIdx], doubles, drawingStyle, shapes, outputParameterInfo, indicator.getIndicatorInfo(), indicatorWrapper.getOutputShifts()[outputIdx]);
/*     */ 
/* 278 */       if ((!indicatorInfo.isOverChart()) && (indicatorWrapper.showValueOnChart(outputIdx))) {
/* 279 */         drawLastValueLine(g, component, indicatorWrapper, outputParameterInfo, outputIdx, lastPoint, subValueToYMapper);
/*     */       }
/*     */ 
/* 282 */       setComposite(g, originalComposite);
/*     */     }
/*     */ 
/* 285 */     return (Shape[])shapes.toArray(new Shape[shapes.size()]);
/*     */   }
/*     */ 
/*     */   private void setComposite(Graphics g, Composite composite)
/*     */   {
/* 290 */     ((Graphics2D)g).setComposite(composite);
/*     */   }
/*     */ 
/*     */   private void drawLastValueLine(Graphics g, JComponent component, IndicatorWrapper wrapper, OutputParameterInfo output, int outputIdx, Point point, IValueToYMapper valueToYMapper)
/*     */   {
/* 303 */     Graphics2D g2d = (Graphics2D)g;
/*     */     int x;
/*     */     int x;
/* 305 */     if (point != null) {
/* 306 */       x = point.x;
/*     */     }
/*     */     else {
/* 309 */       long time = this.dataSequenceProvider.getLastTickTime();
/* 310 */       x = this.timeToXMapper.xt(time);
/*     */     }
/*     */ 
/* 313 */     if ((point == null) || (x <= 0)) {
/* 314 */       return;
/*     */     }
/* 316 */     if (this.timeToXMapper.isXOutOfRange(x + 1)) {
/* 317 */       return;
/*     */     }
/*     */ 
/* 320 */     if (output.getType() == OutputParameterInfo.Type.OBJECT) {
/* 321 */       return;
/*     */     }
/*     */ 
/* 324 */     Object lastValue = this.dataSequenceProvider.getIndicatorLatestValue(wrapper.getId(), outputIdx);
/* 325 */     if (lastValue == null)
/* 326 */       return;
/* 327 */     if (((lastValue instanceof Double)) && 
/* 328 */       (((Double)lastValue).isNaN())) {
/* 329 */       return;
/*     */     }
/*     */ 
/* 333 */     if (valueToYMapper.isYOutOfRange(point.y)) {
/* 334 */       return;
/*     */     }
/*     */ 
/* 337 */     Stroke stroke = g2d.getStroke();
/*     */ 
/* 339 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.LAST_CANDLE_TRACKING_LINE));
/* 340 */     g2d.setStroke(this.chartState.getTheme().getStroke(ITheme.StrokeElement.LAST_CANDLE_TRACKING_LINE_STROKE));
/* 341 */     g2d.drawLine(x + 1, point.y, component.getWidth(), point.y);
/*     */ 
/* 343 */     g2d.setStroke(stroke);
/*     */   }
/*     */ 
/*     */   private Point invokeSelfDrawingMethod(Method selfdrawingMethod, Graphics g, Color[] outputColors, Color[] outputColors2, OutputParameterInfo.DrawingStyle[] drawingStyles, int[] lineWidths, IIndicator indicator, ArrayList<Shape> shapes, int outputIdx, Object values) {
/* 347 */     this.indicatorDrawingSupport.setColor2(outputColors2[outputIdx]);
/*     */     try
/*     */     {
/* 350 */       this.dummyHandles.clear();
/* 351 */       Stroke stroke = getStroke(drawingStyles[outputIdx], lineWidths[outputIdx]);
/* 352 */       Graphics graphicsCopy = g.create();
/*     */       try
/*     */       {
/*     */         Object lastPoint;
/*     */         Object lastPoint;
/* 355 */         if ((indicator instanceof IDrawingIndicator))
/* 356 */           lastPoint = selfdrawingMethod.invoke(indicator, new Object[] { graphicsCopy, Integer.valueOf(outputIdx), values, outputColors[outputIdx], stroke, this.indicatorDrawingSupport, shapes, this.dummyHandles });
/*     */         else {
/* 358 */           lastPoint = selfdrawingMethod.invoke(indicator, new Object[] { graphicsCopy, Integer.valueOf(outputIdx), values, outputColors[outputIdx], this.indicatorDrawingSupport, shapes, this.dummyHandles });
/*     */         }
/*     */ 
/* 361 */         if ((lastPoint instanceof Point)) {
/* 362 */           localPoint = (Point)lastPoint;
/*     */           return localPoint;
/*     */         }
/* 364 */         Point localPoint = null;
/*     */         return localPoint; } finally { graphicsCopy.dispose(); }
/*     */     }
/*     */     catch (Throwable e) {
/* 371 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Exception in drawOutput method: " + StrategyWrapper.representError(indicator, e), e, true);
/* 372 */     }return null;
/*     */   }
/*     */ 
/*     */   private Point draw(Graphics g, Object values, Color color, Color color2, int strokeWidth, boolean doubles, OutputParameterInfo.DrawingStyle style, ArrayList<Shape> shapes, OutputParameterInfo outputParameterInfo, IndicatorInfo indicatorInfo, int shift)
/*     */   {
/* 391 */     Point lastPoint = null;
/*     */ 
/* 393 */     if ((style.isOutputAsLine()) && (color != null) && (color2 != null) && (!color.equals(color2)))
/*     */     {
/* 395 */       GeneralPath path = new GeneralPath();
/* 396 */       boolean initialized = false;
/* 397 */       boolean positive = false;
/*     */ 
/* 399 */       int lastValueIndex = -1;
/* 400 */       int lastStartIndex = 0;
/* 401 */       for (int i = 0; i < (doubles ? ((double[])(double[])values).length : ((int[])(int[])values).length); i++) {
/* 402 */         float value = ((int[])(int[])values)[i];
/* 403 */         float prevValue = lastValueIndex >= 0 ? ((int[])(int[])values)[lastValueIndex] : doubles ? (float)((double[])(double[])values)[lastValueIndex] : (0.0F / 0.0F);
/* 404 */         Color workColor = positive ? color : color2;
/* 405 */         if (!Float.isNaN(value)) {
/* 406 */           if ((i > 0) && (!initialized)) {
/* 407 */             initialized = true;
/* 408 */             positive = (Double.isNaN(prevValue)) || (value >= prevValue);
/* 409 */             workColor = positive ? color : color2;
/*     */           }
/* 411 */           if (!Float.isNaN(prevValue)) {
/* 412 */             if (((value <= prevValue) || (positive)) && ((value >= prevValue) || (!positive))) { if (i != (doubles ? ((double[])(double[])values).length - 1 : ((int[])(int[])values).length - 1));
/*     */             } else {
/* 415 */               positive = !positive;
/* 416 */               int length = lastValueIndex - lastStartIndex;
/* 417 */               if (length > 0) {
/* 418 */                 lastPoint = draw(g, values, workColor, color2, strokeWidth, doubles, style, shapes, outputParameterInfo, indicatorInfo, path, lastStartIndex, length, shift);
/*     */               }
/*     */ 
/* 434 */               lastStartIndex = lastValueIndex;
/*     */             }
/*     */           }
/* 437 */           lastValueIndex = i;
/*     */         }
/* 439 */         if (i == (doubles ? ((double[])(double[])values).length - 1 : ((int[])(int[])values).length - 1)) {
/* 440 */           int length = i - lastStartIndex;
/* 441 */           if (length > 0) {
/* 442 */             lastPoint = draw(g, values, workColor, color2, strokeWidth, doubles, style, shapes, outputParameterInfo, indicatorInfo, path, lastStartIndex, length, shift);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 460 */       return lastPoint;
/*     */     }
/* 462 */     lastPoint = draw(g, values, color, color2, strokeWidth, doubles, style, shapes, outputParameterInfo, indicatorInfo, this.tmpPath, 0, doubles ? ((double[])(double[])values).length : ((int[])(int[])values).length, shift);
/*     */ 
/* 478 */     return lastPoint;
/*     */   }
/*     */ 
/*     */   private Point draw(Graphics g, Object values, Color color, Color color2, int strokeWidth, boolean doubles, OutputParameterInfo.DrawingStyle style, ArrayList<Shape> shapes, OutputParameterInfo outputParameterInfo, IndicatorInfo indicatorInfo, GeneralPath path, int startIndex, int arrayLength, int shift)
/*     */   {
/* 498 */     double lastX = -1.0D;
/* 499 */     double lastY = -1.0D;
/*     */ 
/* 502 */     path.reset();
/*     */ 
/* 504 */     boolean plottingStarted = false;
/* 505 */     GeneralPath twoColorHistogramPath = null;
/*     */ 
/* 507 */     if ((style == OutputParameterInfo.DrawingStyle.LEVEL_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASH_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOT_LINE) || (style == OutputParameterInfo.DrawingStyle.LEVEL_DASHDOTDOT_LINE))
/*     */     {
/* 513 */       if (this.indicatorDrawingSupport.isLastCandleInProgress())
/* 514 */         for (int i = doubles ? ((double[])(double[])values).length - 1 : ((int[])(int[])values).length - 1; i >= 0; i--) {
/* 515 */           float dValue = ((int[])(int[])values)[i];
/* 516 */           if (!Float.isNaN(dValue)) {
/* 517 */             int y = (int)this.indicatorDrawingSupport.getYForValue(dValue);
/* 518 */             plottingStarted = true;
/*     */ 
/* 520 */             Font prevFont = g.getFont();
/* 521 */             Color prevColor = g.getColor();
/*     */ 
/* 523 */             g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.DEFAULT));
/* 524 */             g.setColor(color);
/*     */ 
/* 526 */             Rectangle labelDimension = this.drawingsLabelHelper.drawPriceMarkerLabelAndTextLabel(g, null, null, dValue, this.indicatorDrawingSupport.getChartWidth(), 1.0F, y, this.formattersManager.getValueFormatter());
/*     */ 
/* 528 */             g.setFont(prevFont);
/* 529 */             g.setColor(prevColor);
/*     */ 
/* 531 */             path.moveTo(0.0F, y);
/* 532 */             path.lineTo(labelDimension.x - 5, y);
/* 533 */             path.moveTo(labelDimension.x + labelDimension.width + 3, y);
/* 534 */             path.lineTo(this.indicatorDrawingSupport.getChartWidth(), y);
/* 535 */             break;
/*     */           }
/*     */         }
/*     */     }
/*     */     else {
/* 540 */       boolean histogramTwoColor = outputParameterInfo.isHistogramTwoColor();
/* 541 */       boolean gapAtNaN = outputParameterInfo.isGapAtNaN();
/*     */ 
/* 543 */       float y0 = 0.0F;
/* 544 */       float candleWidth = this.indicatorDrawingSupport.getCandleWidthInPixels();
/* 545 */       if (style == OutputParameterInfo.DrawingStyle.HISTOGRAM) {
/* 546 */         y0 = this.indicatorDrawingSupport.getYForValue(0);
/* 547 */         if (histogramTwoColor) {
/* 548 */           twoColorHistogramPath = new GeneralPath();
/*     */         }
/*     */       }
/*     */ 
/* 552 */       int last = this.indicatorDrawingSupport.getNumberOfCandlesOnScreen();
/* 553 */       int firstCandleIndex = this.indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
/* 554 */       int lastCandleIndex = firstCandleIndex + last - 1;
/* 555 */       int length = doubles ? ((double[])(double[])values).length : ((int[])(int[])values).length;
/*     */ 
/* 557 */       int idx = startIndex;
/* 558 */       idx += (startIndex == 0 ? firstCandleIndex - (shift > 0 ? shift : 0) - 1 : 0);
/*     */ 
/* 560 */       if (idx < 0) {
/* 561 */         idx = 0;
/*     */       }
/*     */ 
/* 566 */       int lastCalculatedIndex = lastCandleIndex - (shift < 0 ? shift : 0) + 1;
/* 567 */       if (startIndex + arrayLength < length) {
/* 568 */         lastCalculatedIndex = startIndex + arrayLength;
/*     */       }
/*     */ 
/* 572 */       if (indicatorInfo.isSparseIndicator()) {
/* 573 */         idx = 0;
/* 574 */         idx += startIndex;
/* 575 */         lastCalculatedIndex = startIndex + arrayLength;
/*     */       }
/*     */ 
/* 579 */       if (lastCalculatedIndex > length - 1) {
/* 580 */         lastCalculatedIndex = length - 1;
/*     */       }
/*     */ 
/* 583 */       boolean gap = false;
/* 584 */       for (; idx <= lastCalculatedIndex; idx++) {
/* 585 */         float value = ((int[])(int[])values)[idx];
/*     */ 
/* 587 */         if (!Float.isNaN(value)) {
/* 588 */           float middle = this.indicatorDrawingSupport.getMiddleOfCandle(idx + shift);
/* 589 */           if (Float.isNaN(middle)) {
/*     */             continue;
/*     */           }
/* 592 */           int y = (int)this.indicatorDrawingSupport.getYForValue(value);
/*     */           int newY;
/*     */           float newX;
/*     */           int newY;
/* 596 */           if (style == OutputParameterInfo.DrawingStyle.DOTS) {
/* 597 */             path.moveTo(middle - 2.0F, y - 1);
/* 598 */             path.lineTo(middle + 1.0F, y - 1);
/* 599 */             path.lineTo(middle + 1.0F, y + 2);
/* 600 */             path.lineTo(middle - 2.0F, y + 2);
/* 601 */             path.lineTo(middle - 2.0F, y - 1);
/* 602 */             if (!plottingStarted) {
/* 603 */               plottingStarted = true;
/*     */             }
/* 605 */             float newX = middle + 1.0F;
/* 606 */             newY = y + 2;
/*     */           }
/* 608 */           else if (style == OutputParameterInfo.DrawingStyle.HISTOGRAM) {
/* 609 */             float x = middle - candleWidth / 2.0F;
/*     */ 
/* 611 */             GeneralPath histoBarPath = path;
/* 612 */             if ((histogramTwoColor) && 
/* 613 */               (y < y0))
/* 614 */               histoBarPath = twoColorHistogramPath;
/*     */             int newY;
/*     */             int newY;
/* 617 */             if (candleWidth < 1.5F) {
/* 618 */               histoBarPath.moveTo(middle, y0);
/* 619 */               histoBarPath.lineTo(middle, y);
/* 620 */               float newX = middle;
/* 621 */               newY = y;
/*     */             }
/*     */             else {
/* 624 */               histoBarPath.moveTo(x, y0);
/* 625 */               histoBarPath.lineTo(x, y);
/* 626 */               histoBarPath.lineTo(x + candleWidth, y);
/* 627 */               histoBarPath.lineTo(x + candleWidth, y0);
/* 628 */               histoBarPath.closePath();
/* 629 */               float newX = x + candleWidth;
/* 630 */               newY = y;
/*     */             }
/* 632 */             if (!plottingStarted)
/* 633 */               plottingStarted = true;
/*     */           }
/*     */           else
/*     */           {
/* 637 */             if ((!plottingStarted) || ((gapAtNaN) && (gap))) {
/* 638 */               path.moveTo(middle, y);
/* 639 */               plottingStarted = true;
/* 640 */               gap = false;
/*     */             } else {
/* 642 */               path.lineTo(middle, y);
/*     */             }
/* 644 */             newX = middle;
/* 645 */             newY = y;
/*     */           }
/* 647 */           if (lastX < newX) {
/* 648 */             lastX = newX;
/* 649 */             lastY = newY;
/*     */           }
/*     */         } else {
/* 652 */           gap = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 657 */     if (plottingStarted) {
/* 658 */       render(g, style, color, color2, strokeWidth, shapes, outputParameterInfo, path, indicatorInfo);
/* 659 */       if (twoColorHistogramPath != null) {
/* 660 */         Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 662 */         g2.setColor(color);
/* 663 */         float candleWidth = this.indicatorDrawingSupport.getCandleWidthInPixels();
/* 664 */         if (candleWidth < 1.5F)
/* 665 */           g2.draw(twoColorHistogramPath);
/*     */         else {
/* 667 */           g2.fill(twoColorHistogramPath);
/*     */         }
/* 669 */         shapes.add(twoColorHistogramPath);
/*     */       }
/*     */     }
/*     */ 
/* 673 */     Point result = new Point();
/* 674 */     result.setLocation(lastX, lastY);
/* 675 */     return result;
/*     */   }
/*     */ 
/*     */   private void render(Graphics g, OutputParameterInfo.DrawingStyle style, Color color, Color color2, int strokeWidth, ArrayList<Shape> shapes, OutputParameterInfo outputParameterInfo, GeneralPath path, IndicatorInfo indicatorInfo) {
/* 679 */     Graphics2D g2 = (Graphics2D)g;
/* 680 */     Stroke oldStroke = g2.getStroke();
/*     */ 
/* 682 */     Stroke stroke = getStroke(style, strokeWidth);
/* 683 */     if (stroke != null) {
/* 684 */       g2.setStroke(stroke);
/*     */     }
/*     */ 
/* 687 */     g2.setColor(color);
/* 688 */     if (style == OutputParameterInfo.DrawingStyle.DOTS) {
/* 689 */       g2.fill(path);
/*     */     }
/* 691 */     else if (style == OutputParameterInfo.DrawingStyle.HISTOGRAM) {
/* 692 */       if (outputParameterInfo.isHistogramTwoColor())
/*     */       {
/* 694 */         g2.setColor(color2);
/*     */       }
/*     */ 
/* 698 */       float candleWidth = this.indicatorDrawingSupport.getCandleWidthInPixels();
/* 699 */       if (candleWidth < 1.5F) {
/* 700 */         g2.draw(path);
/*     */       }
/* 702 */       else if (indicatorInfo.isSparseIndicator())
/*     */       {
/* 706 */         g2.fill(PathHelper.getVisiblePart(path, 0.0D, this.timeToXMapper.getWidth()));
/*     */       }
/* 708 */       else g2.fill(path);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 713 */       g2.draw(path);
/*     */     }
/* 715 */     shapes.add(path);
/* 716 */     g2.setStroke(oldStroke);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.AbstractSubIndicatorsDrawingStrategy
 * JD-Core Version:    0.6.0
 */