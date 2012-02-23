/*     */ package com.dukascopy.api.impl.util;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.LoadingProgressListener;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.feed.IPointAndFigureFeedListener;
/*     */ import com.dukascopy.api.feed.IRangeBarFeedListener;
/*     */ import com.dukascopy.api.feed.IRenkoBarFeedListener;
/*     */ import com.dukascopy.api.feed.ITickBarFeedListener;
/*     */ import com.dukascopy.api.impl.History;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class HistoryUtils
/*     */ {
/*  42 */   private static final Logger LOGGER = LoggerFactory.getLogger(History.class);
/*     */ 
/*  44 */   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/*     */ 
/*     */   public static void throwJFException(Throwable e)
/*     */     throws JFException
/*     */   {
/*     */     Throwable ex;
/*     */     Throwable ex;
/*  57 */     if ((e instanceof PrivilegedActionException)) {
/*  58 */       ex = ((PrivilegedActionException)e).getException();
/*     */     }
/*     */     else {
/*  61 */       ex = e;
/*     */     }
/*  63 */     if ((ex instanceof JFException))
/*  64 */       throw ((JFException)ex);
/*  65 */     if ((ex instanceof IllegalArgumentException))
/*  66 */       throw new JFException(ex.getLocalizedMessage(), ex);
/*  67 */     if ((ex instanceof RuntimeException)) {
/*  68 */       throw ((RuntimeException)ex);
/*     */     }
/*  70 */     LOGGER.error(ex.getMessage(), ex);
/*  71 */     throw new JFException(ex);
/*     */   }
/*     */ 
/*     */   public static <T extends IBar, D extends AbstractPriceAggregationData> List<T> convert(List<D> bars)
/*     */   {
/*  76 */     if (bars == null) {
/*  77 */       return null;
/*     */     }
/*     */ 
/*  80 */     List result = new ArrayList(bars.size());
/*  81 */     for (AbstractPriceAggregationData bar : bars) {
/*  82 */       result.add(bar);
/*     */     }
/*     */ 
/*  85 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean isIntervalValid(IFeedDataProvider feedDataProvider, Instrument instrument, long from, long to)
/*     */   {
/* 105 */     boolean result = isIntervalValid(feedDataProvider, instrument, Period.TICK, from, to);
/* 106 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean isIntervalValid(IFeedDataProvider feedDataProvider, Instrument instrument, Period period, long from, long to)
/*     */   {
/* 116 */     long lastKnownTime = feedDataProvider.getCurrentTime();
/* 117 */     long firstKnownTime = feedDataProvider.getTimeOfFirstCandle(instrument, period);
/*     */ 
/* 119 */     if (!Period.TICK.equals(period)) {
/* 120 */       lastKnownTime = DataCacheUtils.getCandleStartFast(period, lastKnownTime);
/* 121 */       firstKnownTime = DataCacheUtils.getCandleStartFast(period, firstKnownTime);
/*     */     }
/*     */ 
/* 129 */     return (from <= to) && (lastKnownTime >= to) && (firstKnownTime <= from);
/*     */   }
/*     */ 
/*     */   public static void validateTimeInterval(IFeedDataProvider feedDataProvider, Instrument instrument, long from, long to)
/*     */     throws JFException
/*     */   {
/* 148 */     validateTimeInterval(feedDataProvider, instrument, Period.TICK, from, to);
/*     */   }
/*     */ 
/*     */   public static void validateTimeInterval(IFeedDataProvider feedDataProvider, Instrument instrument, Period period, long from, long to)
/*     */     throws JFException
/*     */   {
/* 158 */     if (!isIntervalValid(feedDataProvider, instrument, period, from, to)) {
/* 159 */       String fromStr = DATE_FORMAT.format(new Long(from));
/* 160 */       String toStr = DATE_FORMAT.format(new Long(to));
/*     */ 
/* 162 */       throw new JFException("Requested time interval from [" + fromStr + "] to [" + toStr + "] GMT is not valid");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void validatePointAndFigureParams(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */     throws JFException
/*     */   {
/* 172 */     if ((instrument == null) || (offerSide == null) || (priceRange == null) || (reversalAmount == null))
/* 173 */       throw new JFException("Params could not be null: Instrument=" + instrument + " OfferSide=" + offerSide + " PriceRange=" + priceRange + " ReversalAmount=" + reversalAmount);
/*     */   }
/*     */ 
/*     */   public static void validatePointAndFigureParams(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, IPointAndFigureFeedListener listener, LoadingProgressListener loadingProgress)
/*     */     throws JFException
/*     */   {
/* 184 */     validatePointAndFigureParams(instrument, offerSide, priceRange, reversalAmount);
/* 185 */     if (listener == null) {
/* 186 */       throw new JFException("PointAndFigureFeedListener could not be null");
/*     */     }
/* 188 */     if (loadingProgress == null)
/* 189 */       throw new JFException("LoadingProgressListener could not be null");
/*     */   }
/*     */ 
/*     */   public static void validateTickBarParams(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*     */     throws JFException
/*     */   {
/* 198 */     if ((instrument == null) || (offerSide == null) || (tickBarSize == null))
/* 199 */       throw new JFException("Params could not be null: Instrument=" + instrument + " OfferSide=" + offerSide + " TickBarSize=" + tickBarSize);
/*     */   }
/*     */ 
/*     */   public static void validateTickBarParams(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarFeedListener listener, LoadingProgressListener loadingProgress)
/*     */     throws JFException
/*     */   {
/* 210 */     validateTickBarParams(instrument, offerSide, tickBarSize);
/* 211 */     if (listener == null) {
/* 212 */       throw new JFException("TickBarFeedListener could not be null");
/*     */     }
/* 214 */     if (loadingProgress == null)
/* 215 */       throw new JFException("LoadingProgressListener could not be null");
/*     */   }
/*     */ 
/*     */   public static void validateRangeBarParams(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*     */     throws JFException
/*     */   {
/* 225 */     if ((instrument == null) || (offerSide == null) || (priceRange == null))
/* 226 */       throw new JFException("Params could not be null: Instrument=" + instrument + " OfferSide=" + offerSide + " PriceRange=" + priceRange);
/*     */   }
/*     */ 
/*     */   public static void validateRangeBarParams(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IRangeBarFeedListener listener, LoadingProgressListener loadingProgress)
/*     */     throws JFException
/*     */   {
/* 237 */     validateRangeBarParams(instrument, offerSide, priceRange);
/* 238 */     if (listener == null) {
/* 239 */       throw new JFException("RangeBarFeedListener could not be null");
/*     */     }
/* 241 */     if (loadingProgress == null)
/* 242 */       throw new JFException("LoadingProgressListener could not be null");
/*     */   }
/*     */ 
/*     */   public static void validateRenkoBarParams(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*     */     throws JFException
/*     */   {
/* 252 */     if ((instrument == null) || (offerSide == null) || (priceRange == null))
/* 253 */       throw new JFException("Params could not be null: Instrument=" + instrument + " OfferSide=" + offerSide + " PriceRange=" + priceRange);
/*     */   }
/*     */ 
/*     */   public static void validateRenkoBarParams(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IRenkoBarFeedListener listener, LoadingProgressListener loadingProgress)
/*     */     throws JFException
/*     */   {
/* 264 */     validateRenkoBarParams(instrument, offerSide, priceRange);
/* 265 */     if (listener == null) {
/* 266 */       throw new JFException("RenkoBarFeedListener could not be null");
/*     */     }
/* 268 */     if (loadingProgress == null)
/* 269 */       throw new JFException("LoadingProgressListener could not be null");
/*     */   }
/*     */ 
/*     */   public static void validateBeforeTimeAfter(IFeedDataProvider feedDataProvider, Instrument instrument, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*     */     throws JFException
/*     */   {
/* 280 */     if (numberOfBarsBefore < 0) {
/* 281 */       throw new JFException("NumberOfBarsBefore must be >= 0");
/*     */     }
/* 283 */     if (numberOfBarsAfter < 0) {
/* 284 */       throw new JFException("NumberOfBarsAfter must be >= 0");
/*     */     }
/*     */ 
/* 287 */     long lastKnownTime = feedDataProvider.getCurrentTime();
/* 288 */     long firstKnownTime = feedDataProvider.getTimeOfFirstCandle(instrument, Period.TICK);
/*     */ 
/* 290 */     if ((lastKnownTime < time) || (firstKnownTime > time))
/* 291 */       throw new JFException("Passed Time [" + DATE_FORMAT.format(new Long(time)) + "] has to be in interval [" + DATE_FORMAT.format(new Long(firstKnownTime)) + "; " + DATE_FORMAT.format(new Long(lastKnownTime)) + "]");
/*     */   }
/*     */ 
/*     */   public static long correctRequestTime(long time, PriceRange priceRange, ReversalAmount reversalAmount)
/*     */   {
/*     */     Period period;
/*     */     Period period;
/* 306 */     if (reversalAmount != null) {
/* 307 */       period = TimeDataUtils.getSuitablePeriod(priceRange, reversalAmount);
/*     */     }
/*     */     else {
/* 310 */       period = TimeDataUtils.getSuitablePeriod(priceRange);
/*     */     }
/*     */ 
/* 313 */     long value = Period.TICK.equals(period) ? time : DataCacheUtils.getCandleStartFast(period, time);
/*     */ 
/* 318 */     return value;
/*     */   }
/*     */ 
/*     */   public static long correctRequestTime(long time, PriceRange priceRange) {
/* 322 */     return correctRequestTime(time, priceRange, null);
/*     */   }
/*     */ 
/*     */   public static void validateShift(int shift) throws JFException {
/* 326 */     if (shift < 0)
/* 327 */       throw new JFException("shift < 0");
/*     */   }
/*     */ 
/*     */   public static <T> T getByShift(Loadable<T> loadable, long inProgressBarTime, int shift)
/*     */     throws Exception
/*     */   {
/* 346 */     int barsCount = 0;
/* 347 */     long from = inProgressBarTime;
/* 348 */     long to = inProgressBarTime - 1L;
/*     */ 
/* 350 */     while (barsCount < shift) {
/* 351 */       from = to - loadable.getStep();
/* 352 */       from = loadable.correctTime(from);
/* 353 */       to = loadable.correctTime(to);
/*     */ 
/* 355 */       List bars = loadable.load(from, to);
/*     */       Iterator i$;
/* 357 */       if (barsCount + bars.size() < shift) {
/* 358 */         barsCount += bars.size();
/*     */       }
/*     */       else {
/* 361 */         Collections.reverse(bars);
/* 362 */         for (i$ = bars.iterator(); i$.hasNext(); ) { Object tick = i$.next();
/* 363 */           barsCount++;
/* 364 */           if (barsCount == shift) {
/* 365 */             return tick;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 370 */       to = from - 1L;
/*     */     }
/*     */ 
/* 373 */     return null;
/*     */   }
/*     */ 
/*     */   public static void validate(Instrument instrument, Period period, OfferSide offerSide, Filter filter)
/*     */     throws JFException
/*     */   {
/* 382 */     if ((instrument == null) || (offerSide == null) || (period == null) || (filter == null))
/* 383 */       throw new JFException("Params could not be null: Instrument=" + instrument + " OfferSide=" + offerSide + " Period=" + period + " Filter=" + filter);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ 
/*     */   public static abstract interface Loadable<T>
/*     */   {
/*     */     public abstract List<T> load(long paramLong1, long paramLong2)
/*     */       throws Exception;
/*     */ 
/*     */     public abstract long correctTime(long paramLong);
/*     */ 
/*     */     public abstract long getStep();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.util.HistoryUtils
 * JD-Core Version:    0.6.0
 */