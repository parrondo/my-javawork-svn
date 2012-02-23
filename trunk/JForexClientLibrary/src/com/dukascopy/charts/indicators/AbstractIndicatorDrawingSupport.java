/*     */ package com.dukascopy.charts.indicators;
/*     */ 
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import java.awt.Color;
/*     */ 
/*     */ public abstract class AbstractIndicatorDrawingSupport<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IIndicatorDrawingSupport
/*     */ {
/*     */   protected Instrument instrument;
/*     */   protected Period period;
/*     */   protected OfferSide offerSide;
/*     */   protected DataSequenceClass dataSequence;
/*     */   protected final GeometryCalculator geometryCalculator;
/*     */   protected IValueToYMapper valueToYMapper;
/*     */   protected ITimeToXMapper timeToXMapper;
/*     */   protected boolean isChartPanel;
/*     */   protected Color color2;
/*     */ 
/*     */   public AbstractIndicatorDrawingSupport(GeometryCalculator geometryCalculator)
/*     */   {
/*  29 */     this.geometryCalculator = geometryCalculator;
/*     */   }
/*     */ 
/*     */   public float getCandleWidthInPixels()
/*     */   {
/*  34 */     return this.geometryCalculator.getDataUnitWidthWithoutOverhead();
/*     */   }
/*     */ 
/*     */   public float getSpaceBetweenCandlesInPixels()
/*     */   {
/*  39 */     return this.geometryCalculator.getDataUnitWidth() - this.geometryCalculator.getDataUnitWidthWithoutOverhead();
/*     */   }
/*     */ 
/*     */   public float getMiddleOfCandle(int index)
/*     */   {
/*  44 */     IBar[] candles = getCandles();
/*  45 */     if (candles.length == 0) {
/*  46 */       return (0.0F / 0.0F);
/*     */     }
/*     */ 
/*  49 */     if (this.period == Period.TICK)
/*  50 */       this.period = Period.ONE_SEC;
/*     */     long time;
/*  52 */     if (index >= candles.length) {
/*  53 */       int numberOfCandlesFromLastIndex = index - candles.length + 2;
/*  54 */       long lastCandleTime = candles[(candles.length - 1)].getTime();
/*  55 */       long time = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, lastCandleTime, numberOfCandlesFromLastIndex);
/*  56 */       long[][] gaps = this.dataSequence.getGaps();
/*  57 */       for (long[] gap : gaps)
/*  58 */         if ((gap[0] > lastCandleTime) && (gap[0] <= time)) {
/*  59 */           numberOfCandlesFromLastIndex = (int)(numberOfCandlesFromLastIndex + gap[1]);
/*  60 */           time = DataCacheUtils.getTimeForNCandlesForwardFast(this.period, lastCandleTime, numberOfCandlesFromLastIndex);
/*     */         }
/*     */     }
/*  63 */     else if (index < 0) {
/*  64 */       int numberOfCandlesToFirstIndex = -index + 1;
/*  65 */       long firstCandleTime = candles[0].getTime();
/*  66 */       long time = DataCacheUtils.getTimeForNCandlesBackFast(this.period, firstCandleTime, numberOfCandlesToFirstIndex);
/*  67 */       long[][] gaps = this.dataSequence.getGaps();
/*  68 */       for (int i = gaps.length - 1; i >= 0; i--) {
/*  69 */         long[] gap = gaps[i];
/*  70 */         if ((gap[0] > firstCandleTime) && (gap[0] <= time)) {
/*  71 */           numberOfCandlesToFirstIndex = (int)(numberOfCandlesToFirstIndex + gap[1]);
/*  72 */           time = DataCacheUtils.getTimeForNCandlesBackFast(this.period, firstCandleTime, numberOfCandlesToFirstIndex);
/*     */         }
/*     */       }
/*     */     } else {
/*  76 */       time = candles[index].getTime();
/*     */     }
/*  78 */     if (this.period == Period.ONE_SEC) {
/*  79 */       return this.timeToXMapper.xt(time + 500L);
/*     */     }
/*     */ 
/*  82 */     return this.timeToXMapper.xt(time);
/*     */   }
/*     */ 
/*     */   public int getNumberOfCandlesOnScreen()
/*     */   {
/*  88 */     return this.dataSequence.size();
/*     */   }
/*     */ 
/*     */   public float getYForValue(double value)
/*     */   {
/*  93 */     if (Double.isNaN(value))
/*     */     {
/*  95 */       return (0.0F / 0.0F);
/*     */     }
/*  97 */     return this.valueToYMapper.yv(value);
/*     */   }
/*     */ 
/*     */   public float getYForValue(int value)
/*     */   {
/* 102 */     if (value == -2147483648)
/*     */     {
/* 104 */       return (0.0F / 0.0F);
/*     */     }
/* 106 */     return this.valueToYMapper.yv(value);
/*     */   }
/*     */ 
/*     */   public int getXForTime(long time)
/*     */   {
/* 111 */     return this.timeToXMapper.xt(time);
/*     */   }
/*     */ 
/*     */   public int getIndexOfFirstCandleOnScreen()
/*     */   {
/* 116 */     return getExtraBefore();
/*     */   }
/*     */ 
/*     */   public void setValueToYMapper(IValueToYMapper valueToYMapper) {
/* 120 */     this.valueToYMapper = valueToYMapper;
/*     */   }
/*     */ 
/*     */   public void setTimeToXMapper(ITimeToXMapper timeToXMapper) {
/* 124 */     this.timeToXMapper = timeToXMapper;
/*     */   }
/*     */ 
/*     */   public void setChartData(Instrument instrument, Period period, OfferSide offerSide, DataSequenceClass dataSequence, boolean isChartPanel)
/*     */   {
/* 129 */     this.instrument = instrument;
/* 130 */     this.period = period;
/* 131 */     this.offerSide = offerSide;
/* 132 */     this.dataSequence = dataSequence;
/* 133 */     this.isChartPanel = isChartPanel;
/*     */   }
/*     */ 
/*     */   public void setColor2(Color color) {
/* 137 */     this.color2 = color;
/*     */   }
/*     */ 
/*     */   protected int getExtraBefore() {
/* 141 */     return this.dataSequence.getExtraBefore();
/*     */   }
/*     */ 
/*     */   public boolean isLastCandleInProgress()
/*     */   {
/* 146 */     return this.dataSequence.isIncludesLatestData();
/*     */   }
/*     */ 
/*     */   public int getChartWidth()
/*     */   {
/* 151 */     return this.timeToXMapper.getWidth();
/*     */   }
/*     */ 
/*     */   public int getChartHeight()
/*     */   {
/* 156 */     return this.valueToYMapper.getHeight();
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 161 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 166 */     return this.period;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide()
/*     */   {
/* 171 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   public boolean isChartPanel()
/*     */   {
/* 176 */     return this.isChartPanel;
/*     */   }
/*     */ 
/*     */   public Color getDowntrendColor()
/*     */   {
/* 181 */     return this.color2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.indicators.AbstractIndicatorDrawingSupport
 * JD-Core Version:    0.6.0
 */