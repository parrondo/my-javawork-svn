/*     */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.feed.IPriceAggregationBar;
/*     */ import com.dukascopy.api.impl.TimedData;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TimeDataUtils
/*     */ {
/*     */   public static <T> void reverseArray(T[] src, T[] dest)
/*     */   {
/*  27 */     if ((src.length <= 0) || (dest.length <= 0)) {
/*  28 */       return;
/*     */     }
/*  30 */     int i = dest.length - 1; for (int k = 0; i >= 0; k++) {
/*  31 */       dest[k] = src[i];
/*     */ 
/*  30 */       i--;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T> void copyArray(T[] src, int srcPos, T[] dest, int destPos, int length)
/*     */   {
/*  37 */     System.arraycopy(src, srcPos, dest, destPos, length);
/*     */   }
/*     */ 
/*     */   public static int isConsistent(List<CandleData> buffer) {
/*  41 */     if (buffer == null) {
/*  42 */       return -1;
/*     */     }
/*     */ 
/*  45 */     return isConsistent((CandleData[])buffer.toArray(new CandleData[buffer.size()]), null);
/*     */   }
/*     */ 
/*     */   public static int isConsistent(List<CandleData> buffer, Period period) {
/*  49 */     if (buffer == null) {
/*  50 */       return -1;
/*     */     }
/*     */ 
/*  53 */     return isConsistent((CandleData[])buffer.toArray(new CandleData[buffer.size()]), period);
/*     */   }
/*     */ 
/*     */   public static int isConsistent(CandleData[] buffer, Period period)
/*     */   {
/*  63 */     if (buffer == null) {
/*  64 */       return -1;
/*     */     }
/*     */ 
/*  67 */     for (int i = 0; i < buffer.length - 1; i++) {
/*  68 */       CandleData currentData = buffer[i];
/*  69 */       CandleData nextData = buffer[(i + 1)];
/*  70 */       if (period != null) {
/*  71 */         if (currentData.getTime() + period.getInterval() != nextData.getTime()) {
/*  72 */           return i + 1;
/*     */         }
/*  74 */         if (currentData.getTime() != DataCacheUtils.getCandleStartFast(period, currentData.getTime())) {
/*  75 */           return i + 1;
/*     */         }
/*     */       }
/*  78 */       if (currentData.getTime() >= nextData.getTime()) {
/*  79 */         return i + 1;
/*     */       }
/*     */     }
/*     */ 
/*  83 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int isConsistent(AbstractPriceAggregationData[] buffer, boolean checkOHLC) {
/*  87 */     if (buffer == null) {
/*  88 */       return -1;
/*     */     }
/*     */ 
/*  91 */     for (int i = 0; i < buffer.length - 1; i++) {
/*  92 */       AbstractPriceAggregationData iData = buffer[i];
/*     */ 
/*  94 */       if ((iData == null) || (iData.getTime() > iData.getEndTime()))
/*     */       {
/*  98 */         return i;
/*     */       }
/*     */ 
/* 101 */       AbstractPriceAggregationData nextData = buffer[(i + 1)];
/*     */ 
/* 103 */       if ((checkOHLC) && (DataCacheUtils.isTheSameTradingSession(iData.getTime(), nextData.getTime())) && (iData.getLow() < nextData.getOpen()) && (nextData.getOpen() < iData.getHigh()))
/*     */       {
/* 109 */         return i;
/*     */       }
/*     */ 
/* 112 */       if ((nextData == null) || (nextData.getTime() > nextData.getEndTime()) || (iData.getTime() >= nextData.getTime()) || (iData.getEndTime() >= nextData.getEndTime()))
/*     */       {
/* 118 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 122 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int arePricesConsistent(PointAndFigureData[] buffer, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/* 130 */     PointAndFigureData previousData = null;
/* 131 */     for (int i = 0; i < buffer.length; i++) {
/* 132 */       PointAndFigureData currentData = buffer[i];
/* 133 */       if (previousData != null) {
/* 134 */         if (previousData.isRising().booleanValue()) {
/* 135 */           if (currentData.isRising().booleanValue()) {
/* 136 */             return i;
/*     */           }
/* 138 */           if (previousData.getHigh() - currentData.getHigh() > reversalAmount.getAmount() * priceRange.getPipCount())
/* 139 */             return i;
/*     */         }
/*     */         else
/*     */         {
/* 143 */           if (!currentData.isRising().booleanValue()) {
/* 144 */             return i;
/*     */           }
/* 146 */           if (currentData.getHigh() - previousData.getLow() > reversalAmount.getAmount() * priceRange.getPipCount()) {
/* 147 */             return i;
/*     */           }
/*     */         }
/*     */       }
/* 151 */       previousData = currentData;
/*     */     }
/* 153 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int isConsistent(AbstractPriceAggregationData[] buffer) {
/* 157 */     return isConsistent(buffer, false);
/*     */   }
/*     */ 
/*     */   public static int isRenkoConsistent(RenkoData[] buffer)
/*     */   {
/* 162 */     if (buffer == null) {
/* 163 */       return -1;
/*     */     }
/*     */ 
/* 166 */     int result = isConsistent(buffer);
/* 167 */     if (result > -1) {
/* 168 */       return result;
/*     */     }
/*     */ 
/* 172 */     RenkoData previous = null;
/* 173 */     for (int i = 0; i < buffer.length; i++) {
/* 174 */       RenkoData rd = buffer[i];
/*     */ 
/* 176 */       if (previous != null) {
/* 177 */         if ((rd.getHigh() == previous.getHigh()) && (rd.getLow() == previous.getLow()))
/*     */         {
/* 181 */           return i;
/*     */         }
/* 183 */         if ((rd.getHigh() != previous.getLow()) && (rd.getLow() != previous.getHigh()))
/*     */         {
/* 187 */           return i;
/*     */         }
/*     */       }
/*     */ 
/* 191 */       previous = rd;
/*     */     }
/*     */ 
/* 194 */     return result;
/*     */   }
/*     */ 
/*     */   public static int isConsistent(TickBarData[] buffer)
/*     */   {
/* 199 */     if (buffer == null) {
/* 200 */       return -1;
/*     */     }
/*     */ 
/* 203 */     long ticksCount = -9223372036854775808L;
/* 204 */     TickBarData nextBar = null;
/*     */ 
/* 206 */     for (int i = 0; i < buffer.length; i++) {
/* 207 */       nextBar = buffer.length > i + 1 ? buffer[(i + 1)] : null;
/*     */ 
/* 209 */       TickBarData data = buffer[i];
/* 210 */       if (ticksCount == -9223372036854775808L) {
/* 211 */         ticksCount = data.getFormedElementsCount();
/*     */       }
/* 213 */       else if ((nextBar != null) && (DataCacheUtils.isTheSameTradingSession(data.getTime(), nextBar.getTime())) && (ticksCount != data.getFormedElementsCount()))
/*     */       {
/* 218 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 222 */     return isConsistent(buffer, false);
/*     */   }
/*     */ 
/*     */   public static void shiftBufferLeft(Object[] buffer, Object[] rightPartAppenderBuffer) {
/* 226 */     shiftBufferLeft(buffer, rightPartAppenderBuffer, 0);
/*     */   }
/*     */ 
/*     */   public static void shiftBufferLeft(Object[] buffer, Object[] rightPartAppenderBuffer, int startFromAppenderBufferIndex) {
/* 230 */     if (buffer.length < rightPartAppenderBuffer.length) {
/* 231 */       throw new IllegalArgumentException("Buffer length must be greater that appender buffer length");
/*     */     }
/*     */ 
/* 234 */     int rightPartAppenderBufferSize = rightPartAppenderBuffer.length - startFromAppenderBufferIndex;
/*     */ 
/* 236 */     System.arraycopy(buffer, rightPartAppenderBufferSize, buffer, 0, buffer.length - rightPartAppenderBufferSize);
/* 237 */     System.arraycopy(rightPartAppenderBuffer, startFromAppenderBufferIndex, buffer, buffer.length - rightPartAppenderBufferSize, rightPartAppenderBufferSize);
/*     */   }
/*     */ 
/*     */   public static void shiftBufferRight(Object[] buffer, Object[] leftPartAppenderBuffer) {
/* 241 */     shiftBufferRight(buffer, leftPartAppenderBuffer, leftPartAppenderBuffer.length);
/*     */   }
/*     */ 
/*     */   public static void shiftBufferRight(Object[] buffer, Object[] leftPartAppenderBuffer, int appenderBufferSize) {
/* 245 */     if (buffer.length < leftPartAppenderBuffer.length) {
/* 246 */       throw new IllegalArgumentException("Buffer length must be greater that appender buffer length");
/*     */     }
/*     */ 
/* 249 */     int leftPartAppenderBufferSize = appenderBufferSize;
/*     */ 
/* 251 */     Object[] tempArray = new Object[buffer.length - leftPartAppenderBufferSize];
/*     */ 
/* 253 */     System.arraycopy(buffer, 0, tempArray, 0, tempArray.length);
/* 254 */     System.arraycopy(leftPartAppenderBuffer, 0, buffer, 0, leftPartAppenderBufferSize);
/* 255 */     System.arraycopy(tempArray, 0, buffer, leftPartAppenderBufferSize, tempArray.length);
/*     */   }
/*     */ 
/*     */   public static Period getSuitablePeriod(PriceRange priceRange) {
/* 259 */     return getSuitablePeriod(priceRange.getPipCount());
/*     */   }
/*     */ 
/*     */   public static Period getSuitablePeriod(PriceRange priceRange, ReversalAmount reversalAmount) {
/* 263 */     return getSuitablePeriod(priceRange.getPipCount(), reversalAmount.getAmount());
/*     */   }
/*     */ 
/*     */   public static Period getSuitablePeriod(int pipsCount, int reversalAmount) {
/* 267 */     int count = pipsCount + reversalAmount;
/* 268 */     return getSuitablePeriod(count);
/*     */   }
/*     */ 
/*     */   public static Period getSuitablePeriod(int pipsCount) {
/* 272 */     if (pipsCount <= 2) {
/* 273 */       return Period.TICK;
/*     */     }
/* 275 */     if (pipsCount <= 10) {
/* 276 */       return Period.ONE_MIN;
/*     */     }
/*     */ 
/* 279 */     return Period.ONE_HOUR;
/*     */   }
/*     */ 
/*     */   public static <T extends IPriceAggregationBar> int timeIndex(T[] datas, long time)
/*     */   {
/* 285 */     if ((datas == null) || (datas.length <= 0)) {
/* 286 */       return -1;
/*     */     }
/*     */ 
/* 289 */     int first = 0;
/* 290 */     int upto = datas.length;
/*     */ 
/* 292 */     while (first < upto) {
/* 293 */       int mid = (first + upto) / 2;
/*     */ 
/* 295 */       int next = mid + 1;
/* 296 */       int previous = mid - 1;
/*     */ 
/* 298 */       IPriceAggregationBar data = datas[mid];
/* 299 */       IPriceAggregationBar nextData = (next >= 0) && (next < datas.length) ? datas[next] : null;
/* 300 */       IPriceAggregationBar previousData = (previous >= 0) && (previous < datas.length) ? datas[previous] : null;
/*     */ 
/* 302 */       if ((data.getTime() <= time) && (time <= data.getEndTime())) {
/* 303 */         return mid;
/*     */       }
/* 305 */       if ((nextData != null) && (data.getEndTime() < time) && (time <= nextData.getEndTime())) {
/* 306 */         return next;
/*     */       }
/* 308 */       if ((previousData != null) && (previousData.getTime() <= time) && (time < data.getTime())) {
/* 309 */         return previous;
/*     */       }
/* 311 */       if (time < data.getTime()) {
/* 312 */         upto = mid;
/*     */       }
/* 314 */       else if (time > data.getTime()) {
/* 315 */         first = mid + 1;
/*     */       }
/*     */       else {
/* 318 */         return mid;
/*     */       }
/*     */     }
/*     */ 
/* 322 */     return -1;
/*     */   }
/*     */ 
/*     */   public static <T extends TimedData> int approximateTimeIndex(T[] datas, long time)
/*     */   {
/* 327 */     if ((datas == null) || (datas.length <= 0)) {
/* 328 */       return -1;
/*     */     }
/*     */ 
/* 331 */     int left = 0;
/* 332 */     int right = datas.length - 1;
/*     */ 
/* 334 */     while (left < right) {
/* 335 */       int mid = left + right >>> 1;
/*     */ 
/* 337 */       int next = mid + 1;
/* 338 */       int previous = mid - 1;
/*     */ 
/* 340 */       TimedData data = datas[mid];
/* 341 */       TimedData nextData = (next >= 0) && (next < datas.length) ? datas[next] : null;
/* 342 */       TimedData previousData = (previous >= 0) && (previous < datas.length) ? datas[previous] : null;
/*     */ 
/* 344 */       if (data.getTime() == time) {
/* 345 */         return mid;
/*     */       }
/* 347 */       if ((nextData != null) && (data.getTime() < time) && (time <= nextData.getTime())) {
/* 348 */         return next;
/*     */       }
/* 350 */       if ((previousData != null) && (previousData.getTime() <= time) && (time < data.getTime())) {
/* 351 */         return previous;
/*     */       }
/* 353 */       if (time < data.getTime()) {
/* 354 */         right = mid;
/*     */       }
/* 356 */       else if (time > data.getTime()) {
/* 357 */         left = mid + 1;
/*     */       }
/*     */       else {
/* 360 */         return mid;
/*     */       }
/*     */     }
/*     */ 
/* 364 */     return -1;
/*     */   }
/*     */ 
/*     */   public static <T extends TimedData> int nearestTimeIndex(T[] datas, long time)
/*     */   {
/* 372 */     if ((datas == null) || (datas.length <= 0)) {
/* 373 */       return -1;
/*     */     }
/*     */ 
/* 376 */     int idx = approximateTimeIndex(datas, time);
/*     */ 
/* 378 */     if (time == datas[idx].getTime())
/* 379 */       return idx;
/* 380 */     if (time < datas[idx].getTime()) {
/* 381 */       if (idx == 0) {
/* 382 */         return idx;
/*     */       }
/* 384 */       long leftBorder = datas[idx].getTime() - (datas[idx].getTime() - datas[(idx - 1)].getTime() >>> 1);
/*     */ 
/* 386 */       return time < leftBorder ? idx - 1 : idx;
/*     */     }
/* 388 */     if (time > datas[idx].getTime()) {
/* 389 */       if (idx == datas.length - 1) {
/* 390 */         return idx;
/*     */       }
/* 392 */       long rightBorder = datas[(idx + 1)].getTime() - (datas[(idx + 1)].getTime() - datas[idx].getTime() >>> 1);
/*     */ 
/* 394 */       return time > rightBorder ? idx + 1 : idx;
/*     */     }
/*     */ 
/* 398 */     return -1;
/*     */   }
/*     */ 
/*     */   public static <T extends TimedData> int strictTimeIndex(T[] datas, long time) {
/* 402 */     if ((datas == null) || (datas.length <= 0)) {
/* 403 */       return -1;
/*     */     }
/*     */ 
/* 406 */     int first = 0;
/* 407 */     int upto = datas.length;
/*     */ 
/* 409 */     while (first < upto) {
/* 410 */       int mid = (first + upto) / 2;
/*     */ 
/* 412 */       TimedData data = datas[mid];
/*     */ 
/* 414 */       if (data.getTime() == time) {
/* 415 */         return mid;
/*     */       }
/* 417 */       if (time < data.getTime()) {
/* 418 */         upto = mid;
/*     */       }
/* 420 */       else if (time > data.getTime()) {
/* 421 */         first = mid + 1;
/*     */       }
/*     */       else {
/* 424 */         return mid;
/*     */       }
/*     */     }
/*     */ 
/* 428 */     return -1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils
 * JD-Core Version:    0.6.0
 */