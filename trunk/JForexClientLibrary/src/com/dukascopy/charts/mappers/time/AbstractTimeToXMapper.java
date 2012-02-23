/*     */ package com.dukascopy.charts.mappers.time;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public abstract class AbstractTimeToXMapper<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements ITimeToXMapper
/*     */ {
/*  19 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() { } ;
/*     */   protected final ChartState chartState;
/*     */   protected final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   protected final GeometryCalculator geometryCalculator;
/*  25 */   protected DataSequenceClass dataSequence = null;
/*  26 */   protected long[][] gaps = (long[][])null;
/*  27 */   protected int diff = 0;
/*  28 */   protected long startTime = -1L;
/*  29 */   protected long endTime = -1L;
/*     */ 
/*     */   public AbstractTimeToXMapper(ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator)
/*     */   {
/*  36 */     this.chartState = chartState;
/*  37 */     this.dataSequenceProvider = dataSequenceProvider;
/*  38 */     this.geometryCalculator = geometryCalculator;
/*     */   }
/*     */ 
/*     */   public long tx(int x)
/*     */   {
/*  43 */     sync();
/*     */ 
/*  45 */     if (this.startTime <= 0L) {
/*  46 */       return -1L;
/*     */     }
/*     */ 
/*  49 */     return time(this.startTime, x - this.diff);
/*     */   }
/*     */ 
/*     */   public int xt(long time)
/*     */   {
/*  54 */     sync();
/*     */ 
/*  56 */     if (this.startTime <= 0L) {
/*  57 */       return -2147483648;
/*     */     }
/*     */ 
/*  60 */     long gapsLength = gapsLength(this.startTime, time);
/*  61 */     long timeInterval = time - this.startTime;
/*     */ 
/*  63 */     if (timeInterval >= 0L) {
/*  64 */       return this.diff + x(timeInterval - gapsLength);
/*     */     }
/*  66 */     return this.diff + x(timeInterval + gapsLength);
/*     */   }
/*     */ 
/*     */   public final int getBarWidth()
/*     */   {
/*  72 */     return this.geometryCalculator.getDataUnitWidth();
/*     */   }
/*     */ 
/*     */   public final int getWidth()
/*     */   {
/*  77 */     return this.geometryCalculator.getPaneWidth();
/*     */   }
/*     */ 
/*     */   public final boolean isXOutOfRange(int x)
/*     */   {
/*  82 */     return (x < 0) || (x > getWidth());
/*     */   }
/*     */ 
/*     */   private synchronized void sync()
/*     */   {
/*  88 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/*  90 */     if (this.dataSequence != dataSequence) {
/*  91 */       this.dataSequence = dataSequence;
/*     */     }
/*     */ 
/*  94 */     int sequenceSize = this.dataSequenceProvider.intervalsCount(dataSequence);
/*     */ 
/*  96 */     if (sequenceSize <= 2) {
/*  97 */       long onScreenCandlesCount = this.geometryCalculator.getPaneWidth() / getBarWidth();
/*  98 */       long timeForOnScreenCandles = this.dataSequenceProvider.getInterval() * (onScreenCandlesCount - this.dataSequenceProvider.getMargin());
/*     */ 
/* 100 */       this.endTime = this.dataSequenceProvider.getTime();
/* 101 */       this.startTime = (this.endTime - timeForOnScreenCandles);
/*     */     } else {
/* 103 */       this.startTime = dataSequence.getFrom();
/* 104 */       this.endTime = dataSequence.getTo();
/*     */     }
/*     */ 
/* 109 */     if (sequenceSize == 0) {
/* 110 */       this.gaps = ((long[][])null);
/* 111 */       this.diff = 0;
/*     */     } else {
/* 113 */       this.gaps = dataSequence.getGaps();
/* 114 */       this.diff = ((this.geometryCalculator.getDataUnitsCount() - sequenceSize - this.dataSequenceProvider.getMargin()) * getBarWidth());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long time(long startTime, int x)
/*     */   {
/* 122 */     long interval = getInterval();
/* 123 */     double intervals = x / getBarWidth();
/*     */ 
/* 125 */     if (this.gaps == null) {
/* 126 */       return startTime + ()(intervals * interval);
/*     */     }
/*     */ 
/* 129 */     long time = startTime;
/* 130 */     for (long[] gap : this.gaps) {
/* 131 */       long gapStart = gap[0];
/* 132 */       long gapLength = gap[1];
/* 133 */       long gapEnd = gapStart + gapLength * interval;
/*     */ 
/* 135 */       if ((gapEnd <= time) || ((time == startTime) && (gapStart <= startTime) && (gapEnd >= startTime)))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 140 */       int intervalsBetween = (int)((gapStart - time) / interval);
/* 141 */       if (intervalsBetween > intervals)
/*     */       {
/*     */         break;
/*     */       }
/* 145 */       time = gapEnd;
/* 146 */       intervals -= intervalsBetween;
/*     */     }
/*     */ 
/* 149 */     return time + ()(intervals * interval);
/*     */   }
/*     */ 
/*     */   private int x(long timeInterval)
/*     */   {
/* 156 */     long interval = getInterval();
/* 157 */     double bars = timeInterval / interval;
/*     */ 
/* 159 */     return (int)(bars * getBarWidth());
/*     */   }
/*     */ 
/*     */   private long gapsLength(long from, long till) {
/* 163 */     long gapsLength = 0L;
/*     */ 
/* 165 */     boolean forward = from <= till;
/*     */ 
/* 167 */     if (this.gaps == null) {
/* 168 */       return gapsLength;
/*     */     }
/*     */ 
/* 171 */     long interval = getInterval();
/*     */ 
/* 173 */     for (long[] gap : this.gaps) {
/* 174 */       long gapStart = gap[0];
/* 175 */       long gapLength = gap[1];
/* 176 */       long gapEnd = gapStart + gapLength * interval;
/*     */ 
/* 178 */       if (forward) {
/* 179 */         if (from > gapStart) {
/* 180 */           if (from >= gapEnd) continue;
/* 181 */           gapsLength += (gapEnd - from) / interval; continue;
/*     */         }
/*     */ 
/* 185 */         if (till <= gapStart)
/* 186 */           break;
/*     */       }
/*     */       else {
/* 189 */         if (till > gapStart) {
/* 190 */           if (till >= gapEnd) continue;
/* 191 */           gapsLength += (gapEnd - till) / interval; continue;
/*     */         }
/*     */ 
/* 195 */         if (from <= gapStart)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/* 200 */       gapsLength += gapLength;
/*     */     }
/*     */ 
/* 203 */     return gapsLength * interval;
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 208 */     return this.chartState.getPeriod();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.mappers.time.AbstractTimeToXMapper
 * JD-Core Version:    0.6.0
 */