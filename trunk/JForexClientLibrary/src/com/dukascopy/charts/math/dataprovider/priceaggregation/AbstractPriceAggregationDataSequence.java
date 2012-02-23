/*     */ package com.dukascopy.charts.math.dataprovider.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class AbstractPriceAggregationDataSequence<T extends AbstractPriceAggregationData> extends AbstractDataSequence<T>
/*     */ {
/*     */   public AbstractPriceAggregationDataSequence(Period period, long from, long to, int extraBefore, int extraAfter, T[] data, Map<Integer, Object[]> formulaOutputs, Map<Integer, IndicatorWrapper> indicators, boolean latestDataVisible, boolean includesLatestData)
/*     */   {
/*  28 */     super(period, from, to, extraBefore, extraAfter, data, new long[0][], formulaOutputs, indicators, latestDataVisible, includesLatestData);
/*     */ 
/*  42 */     calculateMinMax();
/*     */   }
/*     */ 
/*     */   protected int getVisibleSequenceStartIndex()
/*     */   {
/*  47 */     int startIndex = this.extraBefore > 0 ? this.extraBefore - 1 : 0;
/*  48 */     return startIndex;
/*     */   }
/*     */ 
/*     */   protected int getVisibleSequenceEndIndex() {
/*  52 */     int endIndex = ((AbstractPriceAggregationData[])this.data).length - (this.extraAfter > 0 ? this.extraAfter : 0) - 1;
/*  53 */     return endIndex;
/*     */   }
/*     */ 
/*     */   protected int getVisibleSequenceSize() {
/*  57 */     int size = getVisibleSequenceEndIndex() - getVisibleSequenceStartIndex();
/*  58 */     return size;
/*     */   }
/*     */ 
/*     */   public void calculateMasterDataMinMax()
/*     */   {
/*  63 */     if (((AbstractPriceAggregationData[])this.data).length == 0) {
/*  64 */       return;
/*     */     }
/*     */ 
/*  67 */     int minMaxIndexStart = getVisibleSequenceStartIndex();
/*  68 */     int minMaxIndexEnd = getVisibleSequenceEndIndex();
/*  69 */     this.min = ((AbstractPriceAggregationData[])this.data)[minMaxIndexStart].getLow();
/*  70 */     this.max = ((AbstractPriceAggregationData[])this.data)[minMaxIndexStart].getHigh();
/*     */ 
/*  72 */     for (int i = minMaxIndexStart; i <= minMaxIndexEnd; i++) {
/*  73 */       AbstractPriceAggregationData currentData = ((AbstractPriceAggregationData[])this.data)[i];
/*     */ 
/*  75 */       double high = getHigh(currentData);
/*  76 */       double low = getLow(currentData);
/*     */ 
/*  78 */       if (this.max < high) {
/*  79 */         this.max = high;
/*     */       }
/*     */ 
/*  82 */       if (this.min > low) {
/*  83 */         this.min = low;
/*     */       }
/*     */     }
/*     */ 
/*  87 */     if (this.max != this.min) {
/*  88 */       return;
/*     */     }
/*     */ 
/*  91 */     this.max += 1.0D;
/*     */   }
/*     */ 
/*     */   protected double getHigh(T bar) {
/*  95 */     return bar.getHigh();
/*     */   }
/*     */ 
/*     */   protected double getLow(T bar) {
/*  99 */     return bar.getLow();
/*     */   }
/*     */ 
/*     */   public int indexOf(long time)
/*     */   {
/* 107 */     int first = 0;
/* 108 */     int upto = size();
/*     */ 
/* 110 */     while (first < upto) {
/* 111 */       int mid = (first + upto) / 2;
/*     */ 
/* 113 */       AbstractPriceAggregationData data = (AbstractPriceAggregationData)getData(mid);
/*     */ 
/* 115 */       if ((data.getTime() == time) && (time == data.getEndTime())) {
/* 116 */         return mid;
/*     */       }
/* 118 */       if ((data.getTime() <= time) && (time < data.getEndTime())) {
/* 119 */         return mid;
/*     */       }
/* 121 */       if (time < data.getTime()) {
/* 122 */         upto = mid;
/*     */       }
/* 124 */       else if (time > data.getTime()) {
/* 125 */         first = mid + 1;
/*     */       }
/*     */       else {
/* 128 */         return mid;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 136 */     AbstractPriceAggregationData data1 = (AbstractPriceAggregationData)getData(first - 1);
/* 137 */     AbstractPriceAggregationData data2 = (AbstractPriceAggregationData)getData(first);
/* 138 */     if ((data1 != null) && (data2 != null) && 
/* 139 */       (data1.getTime() < time) && (time < data2.getTime())) {
/* 140 */       return first - 1;
/*     */     }
/*     */ 
/* 144 */     long INACCURACY_INTERVAL = 1000L;
/*     */ 
/* 146 */     if (isCloseToTime(data1, time, 1000L)) {
/* 147 */       return first - 1;
/*     */     }
/*     */ 
/* 153 */     return interpolateTimeForIndex(time);
/*     */   }
/*     */ 
/*     */   private boolean isCloseToTime(T data, long time, long inaccuracyInterval)
/*     */   {
/* 162 */     return (data != null) && (
/* 158 */       ((time > data.time) && (time - data.time <= inaccuracyInterval)) || ((time < data.time) && (data.time - time <= inaccuracyInterval)));
/*     */   }
/*     */ 
/*     */   private int interpolateTimeForIndex(long time)
/*     */   {
/* 176 */     AbstractPriceAggregationData firstData = (AbstractPriceAggregationData)getData(0);
/* 177 */     AbstractPriceAggregationData lastData = (AbstractPriceAggregationData)getLastData();
/*     */ 
/* 179 */     if ((firstData == null) || (lastData == null)) {
/* 180 */       return -1;
/*     */     }
/*     */ 
/* 183 */     long time1 = firstData.getTime();
/* 184 */     long time2 = lastData.getTime();
/*     */ 
/* 186 */     double timeDelta = Math.abs(time1 - time2);
/* 187 */     double indexDelta = size();
/*     */ 
/* 192 */     double indicesInSecond = indexDelta / timeDelta;
/*     */ 
/* 194 */     Double result = new Double(-1.0D);
/*     */ 
/* 196 */     if (time > time2)
/*     */     {
/* 200 */       result = Double.valueOf((time - time2) * indicesInSecond + size());
/*     */     }
/*     */     else
/*     */     {
/* 206 */       result = Double.valueOf(-1L * (time1 - time) * indicesInSecond);
/*     */     }
/*     */ 
/* 209 */     int intResult = result.intValue();
/* 210 */     if ((intResult == 0) && (size() > 0))
/*     */     {
/* 215 */       intResult = -1;
/*     */     }
/* 217 */     return intResult;
/*     */   }
/*     */ 
/*     */   public int getInterpolatedTimeInterval(int index) {
/* 221 */     int index1 = 0;
/* 222 */     int index2 = ((AbstractPriceAggregationData[])this.data).length - 1;
/*     */ 
/* 224 */     double indexDelta = Math.abs(index1 - index2);
/*     */ 
/* 226 */     long time1 = ((AbstractPriceAggregationData[])this.data)[index1].getTime();
/* 227 */     long time2 = ((AbstractPriceAggregationData[])this.data)[index2].getTime();
/*     */ 
/* 229 */     double timeDelta = Math.abs(time1 - time2);
/*     */ 
/* 234 */     double indicesInSecond = indexDelta / timeDelta;
/*     */ 
/* 236 */     Double result = new Double(-1.0D);
/*     */ 
/* 238 */     if (index > index2)
/*     */     {
/* 242 */       result = Double.valueOf((index - index2) / indicesInSecond);
/*     */     }
/*     */     else
/*     */     {
/* 248 */       result = Double.valueOf((index1 - index) / indicesInSecond);
/*     */     }
/*     */ 
/* 251 */     return result.intValue();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataSequence
 * JD-Core Version:    0.6.0
 */