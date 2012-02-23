/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.util.Calendar;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DailyFilterListener
/*     */   implements LiveFeedListener
/*     */ {
/*  21 */   private static final Logger LOGGER = LoggerFactory.getLogger(DailyFilterListener.class);
/*     */ 
/*  23 */   private static ThreadLocal<Calendar> calendars = new ThreadLocal() {
/*     */     protected Calendar initialValue() {
/*  25 */       return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*     */     }
/*  23 */   };
/*     */   private LiveFeedListener candleListener;
/*  30 */   private double lastClosePrice = (0.0D / 0.0D);
/*     */   private CandleData sundayCandle;
/*     */   private int skip;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */ 
/*     */   public DailyFilterListener(IFeedDataProvider feedDataProvider, LiveFeedListener candleListener, int skip, CandleData lastNonEmptyElement)
/*     */   {
/*  43 */     this.feedDataProvider = feedDataProvider;
/*  44 */     this.candleListener = candleListener;
/*  45 */     this.skip = skip;
/*  46 */     if (lastNonEmptyElement != null) {
/*  47 */       this.lastClosePrice = lastNonEmptyElement.close;
/*  48 */       Calendar calendar = (Calendar)calendars.get();
/*  49 */       calendar.setTimeInMillis(lastNonEmptyElement.time);
/*     */ 
/*  51 */       if (calendar.get(7) == 1)
/*  52 */         this.sundayCandle = lastNonEmptyElement.clone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */   {
/*  59 */     this.candleListener.newTick(instrument, time, ask, bid, askVol, bidVol);
/*     */   }
/*     */ 
/*     */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */   {
/*  64 */     if (period == Period.DAILY_SKIP_SUNDAY)
/*     */     {
/*  66 */       Calendar calendar = (Calendar)calendars.get();
/*  67 */       calendar.setTimeInMillis(time);
/*  68 */       if (calendar.get(7) == 1) {
/*  69 */         if (Double.isNaN(this.lastClosePrice)) {
/*  70 */           if (time > this.feedDataProvider.getTimeOfFirstCandle(instrument, period)) {
/*  71 */             LOGGER.warn("Skipping sunday without previous close price");
/*     */           }
/*  73 */           this.lastClosePrice = open;
/*     */         }
/*  75 */         this.candleListener.newCandle(instrument, period, side, time, this.lastClosePrice, this.lastClosePrice, this.lastClosePrice, this.lastClosePrice, 0.0D);
/*     */       } else {
/*  77 */         this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*     */       }
/*  79 */     } else if (period == Period.DAILY_SUNDAY_IN_MONDAY) {
/*  80 */       Calendar calendar = (Calendar)calendars.get();
/*  81 */       calendar.setTimeInMillis(time);
/*     */ 
/*  83 */       if (calendar.get(7) == 1) {
/*  84 */         if (Double.isNaN(this.lastClosePrice)) {
/*  85 */           if (time > this.feedDataProvider.getTimeOfFirstCandle(instrument, period)) {
/*  86 */             LOGGER.warn("Skipping sunday without previous close price");
/*     */           }
/*  88 */           this.lastClosePrice = open;
/*     */         }
/*  90 */         this.candleListener.newCandle(instrument, period, side, time, this.lastClosePrice, this.lastClosePrice, this.lastClosePrice, this.lastClosePrice, 0.0D);
/*  91 */         this.sundayCandle = new CandleData(time, open, close, low, high, vol);
/*  92 */       } else if (calendar.get(7) == 2)
/*     */       {
/*  94 */         if (this.sundayCandle != null) {
/*  95 */           open = this.sundayCandle.open;
/*  96 */           high = Math.max(high, this.sundayCandle.high);
/*  97 */           low = Math.min(low, this.sundayCandle.low);
/*  98 */           vol = StratUtils.roundHalfEven(vol + this.sundayCandle.vol, 7);
/*  99 */         } else if (time > this.feedDataProvider.getTimeOfFirstCandle(instrument, period)) {
/* 100 */           LOGGER.warn("Passing monday candle without sunday data");
/*     */         }
/* 102 */         this.sundayCandle = null;
/* 103 */         this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*     */       } else {
/* 105 */         this.sundayCandle = null;
/* 106 */         this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*     */       }
/*     */     } else {
/* 109 */       this.candleListener.newCandle(instrument, period, side, time, open, close, low, high, vol);
/*     */     }
/* 111 */     this.lastClosePrice = close;
/*     */   }
/*     */ 
/*     */   public static long calculateDailyFilterFromCorrection(long timeOfTheFirstCandle, Period period, long from) {
/* 115 */     if (period == Period.DAILY_SKIP_SUNDAY)
/*     */     {
/* 117 */       Calendar calendar = (Calendar)calendars.get();
/* 118 */       calendar.setTimeInMillis(from);
/* 119 */       if ((calendar.get(7) == 1) && (from > timeOfTheFirstCandle))
/* 120 */         from -= 86400000L;
/*     */     }
/* 122 */     else if (period == Period.DAILY_SUNDAY_IN_MONDAY)
/*     */     {
/* 124 */       Calendar calendar = (Calendar)calendars.get();
/* 125 */       calendar.setTimeInMillis(from);
/* 126 */       if ((calendar.get(7) == 2) && (from > timeOfTheFirstCandle + 86400000L))
/* 127 */         from -= 172800000L;
/* 128 */       else if ((calendar.get(7) == 1) && (from > timeOfTheFirstCandle)) {
/* 129 */         from -= 86400000L;
/*     */       }
/*     */     }
/* 132 */     return from;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.DailyFilterListener
 * JD-Core Version:    0.6.0
 */