/*     */ package com.dukascopy.charts.view.swing;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.drawings.IPatternWidgetChartObject.Pattern;
/*     */ import com.dukascopy.api.indicators.IndicatorInfo;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import com.dukascopy.charts.drawings.PatternWidgetChartObject;
/*     */ import com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.indicators.CandlesIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.indicators.DefaultPriceAggregationIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.indicators.TicksIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.CandleDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pricerange.PriceRangeDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.renko.RenkoDataSequence;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.tickbar.TickBarDataSequence;
/*     */ import com.dukascopy.indicators.pattern.AbstractPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.AbstractPatternIndicator.IndexValue;
/*     */ import com.dukascopy.indicators.pattern.AscendingTrianglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.ChannelDownPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.ChannelUpPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.DescendingTrianglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.DoubleBottomPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.DoubleTopPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.FallingWedgePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.HeadAndShouldersPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.InverseHeadAndShouldersPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.InverseRectanglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.PennantPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.RectanglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.RisingWedgePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.TrianglePatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.TripleBottomPatternIndicator;
/*     */ import com.dukascopy.indicators.pattern.TripleTopPatternIndicator;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.swing.SwingWorker;
/*     */ 
/*     */ public class PatternWidgetCalculationModule extends AbstractPatternIndicator
/*     */ {
/*     */   private static final int CANDLES_COUNT_TO_ANALYZE = 4000;
/*     */   private static final int CANDLES_COUNT_TO_ANALYZE_MAGNITUDE = 150;
/*     */   private final PatternChartWidgetPanel widgetPanel;
/*     */   private final PatternWidgetChartObject chartObject;
/*     */   private final ChartState chartState;
/*     */   private final Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders;
/*     */   private final AbstractIndicatorDrawingSupport<CandleDataSequence, CandleData> candlesDrawingSupport;
/*     */   private final AbstractIndicatorDrawingSupport<TickDataSequence, TickData> ticksDrawingSupport;
/*     */   private final AbstractIndicatorDrawingSupport<PriceRangeDataSequence, PriceRangeData> priceRangeDrawingSupport;
/*     */   private final AbstractIndicatorDrawingSupport<PointAndFigureDataSequence, PointAndFigureData> pointAndFigureDrawingSupport;
/*     */   private final AbstractIndicatorDrawingSupport<TickBarDataSequence, TickBarData> tickBarDrawingSupport;
/*     */   private final AbstractIndicatorDrawingSupport<RenkoDataSequence, RenkoData> renkoDrawingSupport;
/*  89 */   private final List<PatternOutput> foundPatterns = new ArrayList(100);
/*     */ 
/*  93 */   private final List<PatternOutput> calculationPatterns = new ArrayList(100);
/*     */ 
/*  97 */   private final Map<IPatternWidgetChartObject.Pattern, AbstractPatternIndicator> indicatorsMap = new EnumMap(IPatternWidgetChartObject.Pattern.class) { } ;
/*     */   private IPatternWidgetChartObject.Pattern activePattern;
/*     */   private AbstractPatternIndicator activePatternCalculation;
/*     */   private AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data> activeDataProvider;
/*     */   private AbstractDataSequence<? extends Data> actualBufferedDataSequence;
/*     */   private Graphics mainChartGraphics;
/*     */   private long lastAnalyzedFrom;
/*     */   private long lastAnalyzedTo;
/*     */   private PatternOutput selectedPattern;
/*     */ 
/*     */   public PatternWidgetCalculationModule(PatternChartWidgetPanel widgetPanel, PatternWidgetChartObject chartObject, ChartState chartState, Map<DataType, AbstractDataSequenceProvider<? extends AbstractDataSequence<? extends Data>, ? extends Data>> allDataSequenceProviders, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper)
/*     */   {
/* 141 */     this.widgetPanel = widgetPanel;
/* 142 */     this.chartObject = chartObject;
/* 143 */     this.chartState = chartState;
/*     */ 
/* 145 */     this.allDataSequenceProviders = allDataSequenceProviders;
/*     */ 
/* 147 */     this.candlesDrawingSupport = createDrawingSupport(new CandlesIndicatorDrawingSupport(geometryCalculator), timeToXMapper, valueToYMapper);
/* 148 */     this.ticksDrawingSupport = createDrawingSupport(new TicksIndicatorDrawingSupport(geometryCalculator), timeToXMapper, valueToYMapper);
/* 149 */     this.priceRangeDrawingSupport = createDrawingSupport(new DefaultPriceAggregationIndicatorDrawingSupport(geometryCalculator), timeToXMapper, valueToYMapper);
/* 150 */     this.pointAndFigureDrawingSupport = createDrawingSupport(new DefaultPriceAggregationIndicatorDrawingSupport(geometryCalculator), timeToXMapper, valueToYMapper);
/* 151 */     this.tickBarDrawingSupport = createDrawingSupport(new DefaultPriceAggregationIndicatorDrawingSupport(geometryCalculator), timeToXMapper, valueToYMapper);
/* 152 */     this.renkoDrawingSupport = createDrawingSupport(new DefaultPriceAggregationIndicatorDrawingSupport(geometryCalculator), timeToXMapper, valueToYMapper);
/*     */   }
/*     */ 
/*     */   private <S extends AbstractIndicatorDrawingSupport<? extends AbstractDataSequence<? extends Data>, ? extends Data>> S createDrawingSupport(S support, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper)
/*     */   {
/* 160 */     support.setTimeToXMapper(timeToXMapper);
/* 161 */     support.setValueToYMapper(valueToYMapper);
/*     */ 
/* 163 */     return support;
/*     */   }
/*     */ 
/*     */   protected IndicatorInfo createIndicatorInfo()
/*     */   {
/* 170 */     return new IndicatorInfo("", "", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
/*     */   }
/*     */ 
/*     */   public int getMinPatternPivotPointNumber()
/*     */   {
/* 175 */     int result = this.activePatternCalculation.getMinPatternPivotPointNumber();
/* 176 */     return result;
/*     */   }
/*     */ 
/*     */   public int getMaxPatternPivotPointNumber()
/*     */   {
/* 181 */     int result = this.activePatternCalculation.getMaxPatternPivotPointNumber();
/* 182 */     return result;
/*     */   }
/*     */ 
/*     */   public AbstractPatternIndicator.IndexValue[] constructUppperAsymptote(double[] cleanPattern, double[] pivotPointsData, int dirtyPatternStartIndex, int dirtyPatternSize)
/*     */   {
/* 187 */     AbstractPatternIndicator.IndexValue[] result = this.activePatternCalculation.constructUppperAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize);
/* 188 */     return result;
/*     */   }
/*     */ 
/*     */   public AbstractPatternIndicator.IndexValue[] constructBottomAsymptote(double[] cleanPattern, double[] pivotPointsData, int dirtyPatternStartIndex, int dirtyPatternSize)
/*     */   {
/* 193 */     AbstractPatternIndicator.IndexValue[] result = this.activePatternCalculation.constructBottomAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize);
/* 194 */     return result;
/*     */   }
/*     */ 
/*     */   public double getCalculatedPatternQuality()
/*     */   {
/* 199 */     double result = this.activePatternCalculation.getCalculatedPatternQuality();
/* 200 */     return result;
/*     */   }
/*     */ 
/*     */   protected void checkPatterns(double[][] sourceData, double[] pivotPointsData, Object[] patternContainer, Object[] firstAsymptoteContainer, Object[] secondAsymptoteContainer, int offset)
/*     */   {
/* 213 */     Set selectedPatterns = this.chartObject.getPatternsToAnalyze();
/*     */ 
/* 215 */     for (IPatternWidgetChartObject.Pattern pattern : selectedPatterns) {
/* 216 */       this.activePattern = pattern;
/* 217 */       this.activePatternCalculation = ((AbstractPatternIndicator)this.indicatorsMap.get(pattern));
/*     */ 
/* 219 */       this.activePatternCalculation.setWholePatternCalculation(false);
/* 220 */       int pivotPointsCount = getMinPatternPivotPointNumber();
/* 221 */       double[] cleanPattern = new double[pivotPointsCount];
/*     */ 
/* 223 */       for (int i = pivotPointsData.length - pivotPointsCount - 1; i > 0; i--) {
/* 224 */         int dirtyPatternSize = preparePatternArray(cleanPattern, pivotPointsData, i);
/*     */ 
/* 226 */         if (dirtyPatternSize > 0) {
/* 227 */           int barsTillEnd = pivotPointsData.length - i - dirtyPatternSize;
/* 228 */           double averageBarsPerPivot = dirtyPatternSize / (pivotPointsCount - 1);
/* 229 */           if (barsTillEnd > averageBarsPerPivot) {
/*     */             break;
/*     */           }
/* 232 */           checkPattern(sourceData, cleanPattern, pivotPointsData, i, dirtyPatternSize);
/*     */ 
/* 235 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 239 */       if (!this.chartObject.isOnlyEmerging())
/*     */       {
/* 242 */         this.activePatternCalculation.setWholePatternCalculation(true);
/* 243 */         for (int points = getMinPatternPivotPointNumber(); points <= getMaxPatternPivotPointNumber(); points++) {
/* 244 */           cleanPattern = new double[points];
/*     */ 
/* 246 */           for (int i = 0; i < pivotPointsData.length - points; i++) {
/* 247 */             int dirtyPatternSize = preparePatternArray(cleanPattern, pivotPointsData, i);
/*     */ 
/* 249 */             if (dirtyPatternSize > 0)
/* 250 */               checkPattern(sourceData, cleanPattern, pivotPointsData, i, dirtyPatternSize);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkPattern(double[][] sourceData, double[] cleanPattern, double[] pivotPointsData, int dirtyPatterStartIndex, int dirtyPatternSize)
/*     */   {
/* 259 */     boolean checkResult = this.activePatternCalculation.checkPattern(cleanPattern, pivotPointsData, dirtyPatterStartIndex, dirtyPatternSize);
/*     */ 
/* 261 */     if (checkResult)
/*     */     {
/* 264 */       int quality = (int)getCalculatedPatternQuality();
/*     */ 
/* 266 */       double patternPriceSpread = getPatternPriceSpread(cleanPattern);
/* 267 */       int magnitude = calculateMagnitude(patternPriceSpread, dirtyPatterStartIndex, dirtyPatternSize);
/*     */ 
/* 269 */       if (magnitude < getMinMagnitude()) {
/* 270 */         return;
/*     */       }
/*     */ 
/* 273 */       AbstractPatternIndicator.IndexValue[] patternPoints = constructPattern(cleanPattern, pivotPointsData, dirtyPatterStartIndex, dirtyPatternSize);
/* 274 */       AbstractPatternIndicator.IndexValue[] upperAsymptotePoints = constructUppperAsymptote(cleanPattern, pivotPointsData, dirtyPatterStartIndex, dirtyPatternSize);
/* 275 */       AbstractPatternIndicator.IndexValue[] bottomAsymptotePoints = constructBottomAsymptote(cleanPattern, pivotPointsData, dirtyPatterStartIndex, dirtyPatternSize);
/*     */ 
/* 277 */       for (AbstractPatternIndicator.IndexValue point : patternPoints) {
/* 278 */         point.setIndex(point.getIndex() - dirtyPatterStartIndex);
/*     */       }
/* 280 */       for (AbstractPatternIndicator.IndexValue point : upperAsymptotePoints) {
/* 281 */         point.setIndex(point.getIndex() - dirtyPatterStartIndex);
/*     */       }
/* 283 */       if (bottomAsymptotePoints != null) {
/* 284 */         for (AbstractPatternIndicator.IndexValue point : bottomAsymptotePoints) {
/* 285 */           point.setIndex(point.getIndex() - dirtyPatterStartIndex);
/*     */         }
/*     */       }
/*     */ 
/* 289 */       boolean wholePattern = this.activePatternCalculation.isWholePatternCalculation();
/* 290 */       if ((!wholePattern) && (!this.actualBufferedDataSequence.isLatestDataVisible())) {
/* 291 */         return;
/*     */       }
/*     */ 
/* 294 */       if (!wholePattern)
/*     */       {
/* 296 */         boolean acceptableTrend = this.activePatternCalculation.checkEmergingPatternPostTrend(sourceData, dirtyPatterStartIndex, dirtyPatternSize, patternPoints, upperAsymptotePoints, bottomAsymptotePoints);
/*     */ 
/* 304 */         if (!acceptableTrend) {
/* 305 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 309 */       PatternOutput patternOutput = new PatternOutput(this.activePattern, wholePattern, quality, magnitude, this.actualBufferedDataSequence.getData()[dirtyPatterStartIndex].getTime(), dirtyPatterStartIndex, dirtyPatternSize, patternPoints, upperAsymptotePoints, bottomAsymptotePoints);
/*     */ 
/* 321 */       if (isPatternUnique(patternOutput))
/* 322 */         this.calculationPatterns.add(patternOutput);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int calculateMagnitude(double patternSpread, int startIndex, int length)
/*     */   {
/*     */     CandleData[] data;
/* 330 */     if ((this.actualBufferedDataSequence instanceof TickDataSequence)) {
/* 331 */       data = ((TickDataSequence)this.actualBufferedDataSequence).getOneSecCandlesBid();
/*     */     }
/*     */     else
/*     */     {
/*     */       CandleData[] data;
/* 332 */       if ((this.actualBufferedDataSequence.getData() instanceof CandleData[]))
/* 333 */         data = (CandleData[])(CandleData[])this.actualBufferedDataSequence.getData();
/*     */       else
/* 335 */         throw new IllegalStateException("Unknown Data format");
/*     */     }
/*     */     CandleData[] data;
/* 338 */     int barsBefore = Math.min(150, startIndex);
/* 339 */     int barsAfter = 150 - barsBefore;
/*     */ 
/* 341 */     double dataMax = data[(startIndex - barsBefore)].getHigh();
/* 342 */     double dataMin = data[(startIndex - barsBefore)].getLow();
/*     */ 
/* 344 */     int stopIndex = Math.min(startIndex + length + barsAfter, data.length);
/* 345 */     for (int i = startIndex - barsBefore + 1; i < stopIndex; i++) {
/* 346 */       dataMax = Math.max(dataMax, data[i].getHigh());
/* 347 */       dataMin = Math.min(dataMin, data[i].getLow());
/*     */     }
/*     */ 
/* 350 */     double fullSpread = dataMax - dataMin;
/*     */ 
/* 352 */     return (int)Math.min(100.0D, patternSpread / fullSpread * 100.0D);
/*     */   }
/*     */ 
/*     */   private boolean isPatternUnique(PatternOutput newPattern) {
/* 356 */     for (PatternOutput patternOutp : this.calculationPatterns) {
/* 357 */       if (patternOutp.equals(newPattern)) {
/* 358 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 362 */     return true;
/*     */   }
/*     */ 
/*     */   public void recalculateIfNecessary(Graphics g)
/*     */   {
/* 367 */     this.activeDataProvider = ((AbstractDataSequenceProvider)this.allDataSequenceProviders.get(this.chartState.getDataType()));
/*     */ 
/* 369 */     this.actualBufferedDataSequence = ((AbstractDataSequence)this.activeDataProvider.getBufferedDataSequence(4000, 4000));
/* 370 */     if (this.actualBufferedDataSequence == null) {
/* 371 */       return;
/*     */     }
/*     */ 
/* 374 */     boolean needRecalculate = (this.lastAnalyzedFrom != this.actualBufferedDataSequence.getFrom()) || (this.lastAnalyzedTo != this.actualBufferedDataSequence.getTo());
/* 375 */     if (needRecalculate)
/*     */     {
/* 378 */       recalculate(g);
/*     */     }
/* 380 */     else drawPatterns(g);
/*     */   }
/*     */ 
/*     */   public void recalculate(Graphics g)
/*     */   {
/* 386 */     this.lastAnalyzedFrom = this.actualBufferedDataSequence.getFrom();
/* 387 */     this.lastAnalyzedTo = this.actualBufferedDataSequence.getTo();
/*     */ 
/* 389 */     SwingWorker analysator = new SwingWorker(g)
/*     */     {
/*     */       protected List<PatternWidgetCalculationModule.PatternOutput> doInBackground() throws Exception
/*     */       {
/* 393 */         return PatternWidgetCalculationModule.this.doCalculations();
/*     */       }
/*     */ 
/*     */       protected void done()
/*     */       {
/* 398 */         PatternWidgetCalculationModule.this.foundPatterns.clear();
/* 399 */         PatternWidgetCalculationModule.this.foundPatterns.addAll(PatternWidgetCalculationModule.this.calculationPatterns);
/*     */ 
/* 401 */         PatternWidgetCalculationModule.this.widgetPanel.updateInfoTab();
/*     */ 
/* 403 */         PatternWidgetCalculationModule.this.drawPatterns(this.val$g);
/*     */       }
/*     */     };
/* 407 */     analysator.execute();
/*     */   }
/*     */ 
/*     */   private List<PatternOutput> doCalculations()
/*     */   {
/* 415 */     int inputDataLength = setupDataForCalculations(this.actualBufferedDataSequence);
/*     */ 
/* 417 */     Object[] patternOutputs = new Object[inputDataLength];
/* 418 */     setOutputParameter(0, patternOutputs);
/*     */ 
/* 420 */     Object[] upperAssymptoteOutputs = new Object[inputDataLength];
/* 421 */     setOutputParameter(1, upperAssymptoteOutputs);
/*     */ 
/* 423 */     Object[] lowerAssymptoteOutputs = new Object[inputDataLength];
/* 424 */     setOutputParameter(2, lowerAssymptoteOutputs);
/*     */ 
/* 426 */     this.calculationPatterns.clear();
/* 427 */     calculate(0, inputDataLength);
/*     */ 
/* 429 */     if (getSortResultsByCriteria() == 0) {
/* 430 */       Collections.sort(this.calculationPatterns, new Comparator()
/*     */       {
/*     */         public int compare(PatternWidgetCalculationModule.PatternOutput o1, PatternWidgetCalculationModule.PatternOutput o2)
/*     */         {
/* 434 */           return o1.quality > o2.quality ? -1 : o1.quality == o2.quality ? 0 : 1;
/*     */         }
/*     */       });
/*     */     }
/*     */     else {
/* 440 */       Collections.sort(this.calculationPatterns, new Comparator()
/*     */       {
/*     */         public int compare(PatternWidgetCalculationModule.PatternOutput o1, PatternWidgetCalculationModule.PatternOutput o2)
/*     */         {
/* 444 */           return o1.magnitude > o2.magnitude ? -1 : o1.magnitude == o2.magnitude ? 0 : 1;
/*     */         }
/*     */ 
/*     */       });
/*     */     }
/*     */ 
/* 451 */     return this.calculationPatterns;
/*     */   }
/*     */ 
/*     */   public void drawPatterns(Graphics g)
/*     */   {
/* 456 */     if (g != null) {
/* 457 */       this.mainChartGraphics = g;
/*     */     }
/* 459 */     Graphics2D g2d = (Graphics2D)this.mainChartGraphics;
/* 460 */     long time = ((AbstractDataSequence)this.activeDataProvider.getDataSequence()).getFrom();
/* 461 */     Data[] bufferedData = this.actualBufferedDataSequence.getData();
/* 462 */     int delta = TimeDataUtils.approximateTimeIndex(bufferedData, time) - ((AbstractDataSequence)this.activeDataProvider.getDataSequence()).getExtraBefore();
/*     */ 
/* 464 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.activeDataProvider.getDataSequence();
/*     */ 
/* 466 */     AbstractIndicatorDrawingSupport support = null;
/* 467 */     switch (5.$SwitchMap$com$dukascopy$api$DataType[this.chartState.getDataType().ordinal()]) { case 1:
/* 468 */       support = this.candlesDrawingSupport;
/* 469 */       this.candlesDrawingSupport.setChartData(this.chartState.getInstrument(), this.chartState.getPeriod(), this.chartState.getOfferSide(), (CandleDataSequence)dataSequence, true);
/*     */ 
/* 475 */       break;
/*     */     case 2:
/* 476 */       support = this.ticksDrawingSupport;
/* 477 */       this.ticksDrawingSupport.setChartData(this.chartState.getInstrument(), this.chartState.getPeriod(), this.chartState.getOfferSide(), (TickDataSequence)dataSequence, true);
/*     */ 
/* 483 */       break;
/*     */     case 3:
/* 484 */       support = this.priceRangeDrawingSupport;
/* 485 */       this.priceRangeDrawingSupport.setChartData(this.chartState.getInstrument(), this.chartState.getPeriod(), this.chartState.getOfferSide(), (PriceRangeDataSequence)dataSequence, true);
/*     */ 
/* 491 */       break;
/*     */     case 4:
/* 492 */       support = this.pointAndFigureDrawingSupport;
/* 493 */       this.pointAndFigureDrawingSupport.setChartData(this.chartState.getInstrument(), this.chartState.getPeriod(), this.chartState.getOfferSide(), (PointAndFigureDataSequence)dataSequence, true);
/*     */ 
/* 499 */       break;
/*     */     case 5:
/* 500 */       support = this.tickBarDrawingSupport;
/* 501 */       this.tickBarDrawingSupport.setChartData(this.chartState.getInstrument(), this.chartState.getPeriod(), this.chartState.getOfferSide(), (TickBarDataSequence)dataSequence, true);
/*     */ 
/* 507 */       break;
/*     */     case 6:
/* 509 */       support = this.renkoDrawingSupport;
/* 510 */       this.renkoDrawingSupport.setChartData(this.chartState.getInstrument(), this.chartState.getPeriod(), this.chartState.getOfferSide(), (RenkoDataSequence)dataSequence, true);
/*     */     }
/*     */ 
/* 520 */     if (isShowAllPatterns()) {
/* 521 */       for (PatternOutput patternOutput : this.foundPatterns) {
/* 522 */         g2d.setColor(Color.blue);
/* 523 */         drawIndexValues(patternOutput.patternPoints, g2d, support, patternOutput.patternPosIndex - delta);
/* 524 */         g2d.setColor(Color.red);
/* 525 */         drawIndexValues(patternOutput.firstAsymptotePoints, g2d, support, patternOutput.patternPosIndex - delta);
/* 526 */         g2d.setColor(Color.green);
/* 527 */         drawIndexValues(patternOutput.secondAsymptotePoints, g2d, support, patternOutput.patternPosIndex - delta);
/*     */       }
/* 529 */     } else if (this.selectedPattern != null) {
/* 530 */       g2d.setColor(Color.blue);
/* 531 */       drawIndexValues(this.selectedPattern.patternPoints, g2d, support, this.selectedPattern.patternPosIndex - delta);
/* 532 */       g2d.setColor(Color.red);
/* 533 */       drawIndexValues(this.selectedPattern.firstAsymptotePoints, g2d, support, this.selectedPattern.patternPosIndex - delta);
/* 534 */       g2d.setColor(Color.green);
/* 535 */       drawIndexValues(this.selectedPattern.secondAsymptotePoints, g2d, support, this.selectedPattern.patternPosIndex - delta);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int setupDataForCalculations(AbstractDataSequence<? extends Data> dataSequence)
/*     */   {
/*     */     CandleData[] data;
/* 541 */     if ((dataSequence instanceof TickDataSequence)) {
/* 542 */       data = ((TickDataSequence)dataSequence).getOneSecCandlesBid();
/*     */     }
/*     */     else
/*     */     {
/*     */       CandleData[] data;
/* 543 */       if ((dataSequence.getData() instanceof CandleData[]))
/* 544 */         data = (CandleData[])(CandleData[])dataSequence.getData();
/*     */       else
/* 546 */         throw new IllegalStateException("Unknown Data format");
/*     */     }
/*     */     CandleData[] data;
/* 550 */     double[][] inputs = new double[5][data.length];
/* 551 */     for (int i = 0; i < data.length; i++) {
/* 552 */       inputs[0][i] = data[i].time;
/* 553 */       inputs[1][i] = data[i].close;
/* 554 */       inputs[2][i] = data[i].high;
/* 555 */       inputs[3][i] = data[i].low;
/* 556 */       inputs[4][i] = data[i].vol;
/*     */     }
/* 558 */     setInputParameter(0, inputs);
/*     */ 
/* 560 */     return data.length;
/*     */   }
/*     */ 
/*     */   public List<PatternOutput> getFoundPatterns()
/*     */   {
/* 567 */     return this.foundPatterns;
/*     */   }
/*     */ 
/*     */   public void setPivotPointsPrice(String price) {
/* 571 */     setPivotPointCalculationPrice(price);
/*     */   }
/*     */ 
/*     */   public void setPatternQuality(double quality)
/*     */   {
/* 576 */     super.setPatternQuality(quality);
/*     */ 
/* 578 */     for (Map.Entry entry : this.indicatorsMap.entrySet())
/* 579 */       ((AbstractPatternIndicator)entry.getValue()).setPatternQuality(quality);
/*     */   }
/*     */ 
/*     */   public int getMinMagnitude()
/*     */   {
/* 584 */     return this.chartObject.getDesiredMinMagnitude();
/*     */   }
/*     */ 
/*     */   private int getSortResultsByCriteria() {
/* 588 */     return this.chartObject.getSortPatternsByCriteria();
/*     */   }
/*     */ 
/*     */   private boolean isShowAllPatterns() {
/* 592 */     return this.chartObject.isShowAll();
/*     */   }
/*     */ 
/*     */   public void setSelectedPattern(PatternOutput pattern) {
/* 596 */     this.selectedPattern = pattern;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean checkEmergingPatternPostTrend(double[][] sourceData, int dirtyPatterStartIndex, int dirtyPatternSize, AbstractPatternIndicator.IndexValue[] patternPoints, AbstractPatternIndicator.IndexValue[] upperAsymptotePoints, AbstractPatternIndicator.IndexValue[] bottomAsymptotePoints)
/*     */   {
/* 609 */     throw new IllegalStateException("");
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean checkPattern(double[] cleanPattern, double[] pivotPointsData, int dirtyPatterStartIndex, int dirtyPatternSize)
/*     */   {
/* 719 */     throw new IllegalStateException("should be used only in patterns implementation");
/*     */   }
/*     */ 
/*     */   public class PatternOutput
/*     */   {
/*     */     private final IPatternWidgetChartObject.Pattern pattern;
/*     */     private final boolean wholePattern;
/*     */     private final int quality;
/*     */     private final int magnitude;
/*     */     private final long time;
/*     */     private final int patternPosIndex;
/*     */     private final int length;
/*     */     private final AbstractPatternIndicator.IndexValue[] patternPoints;
/*     */     private final AbstractPatternIndicator.IndexValue[] firstAsymptotePoints;
/*     */     private final AbstractPatternIndicator.IndexValue[] secondAsymptotePoints;
/*     */ 
/*     */     PatternOutput(IPatternWidgetChartObject.Pattern pattern, boolean wholePattern, int quality, int magnitude, long time, int patternPosIndex, int length, AbstractPatternIndicator.IndexValue[] patternPoints, AbstractPatternIndicator.IndexValue[] firstAsymptotePoints, AbstractPatternIndicator.IndexValue[] secondAsymptotePoints)
/*     */     {
/* 640 */       this.pattern = pattern;
/* 641 */       this.wholePattern = wholePattern;
/* 642 */       this.quality = quality;
/* 643 */       this.magnitude = magnitude;
/* 644 */       this.time = time;
/* 645 */       this.patternPosIndex = patternPosIndex;
/* 646 */       this.length = length;
/* 647 */       this.patternPoints = patternPoints;
/* 648 */       this.firstAsymptotePoints = firstAsymptotePoints;
/* 649 */       this.secondAsymptotePoints = secondAsymptotePoints;
/*     */     }
/*     */ 
/*     */     public IPatternWidgetChartObject.Pattern getPattern() {
/* 653 */       return this.pattern;
/*     */     }
/*     */     public boolean isWholePattern() {
/* 656 */       return this.wholePattern;
/*     */     }
/*     */     public int getQuality() {
/* 659 */       return this.quality;
/*     */     }
/*     */     public long getTime() {
/* 662 */       return this.time;
/*     */     }
/*     */     public int getMagnitude() {
/* 665 */       return this.magnitude;
/*     */     }
/*     */     public int getLength() {
/* 668 */       return this.length;
/*     */     }
/*     */ 
/*     */     private boolean isIdenticalIndeces(AbstractPatternIndicator.IndexValue[] firstPatternPoints, AbstractPatternIndicator.IndexValue[] secondPatternPoints) {
/* 672 */       if (firstPatternPoints.length != secondPatternPoints.length) {
/* 673 */         return false;
/*     */       }
/* 675 */       for (int i = 0; i < firstPatternPoints.length; i++) {
/* 676 */         if (firstPatternPoints[i].getIndex() != secondPatternPoints[i].getIndex()) {
/* 677 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 681 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 687 */       if (this == obj)
/* 688 */         return true;
/* 689 */       if (obj == null)
/* 690 */         return false;
/* 691 */       if (getClass() != obj.getClass()) {
/* 692 */         return false;
/*     */       }
/* 694 */       PatternOutput second = (PatternOutput)obj;
/* 695 */       return (this.pattern.equals(second.pattern)) && (this.wholePattern == second.wholePattern) && (this.time == second.time) && (isIdenticalIndeces(this.patternPoints, second.patternPoints));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 703 */       int prime = 31;
/* 704 */       int result = 1;
/* 705 */       result = 31 * result + (this.pattern == null ? 0 : this.pattern.hashCode());
/* 706 */       result += (this.wholePattern ? 1 : 0);
/* 707 */       for (AbstractPatternIndicator.IndexValue indexValue : this.patternPoints) {
/* 708 */         result = 31 * result + indexValue.getIndex();
/*     */       }
/* 710 */       result = (int)(31 * result + this.time);
/* 711 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.PatternWidgetCalculationModule
 * JD-Core Version:    0.6.0
 */