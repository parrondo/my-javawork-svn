/*     */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TesterDataLoader
/*     */ {
/*  30 */   private static final Logger LOGGER = LoggerFactory.getLogger(TesterDataLoader.class);
/*     */   private long from;
/*     */   private long to;
/*     */   private Set<Instrument> instruments;
/*     */   private Period period;
/*     */   private LoadingProgressListener loadingProgressListener;
/*     */ 
/*     */   public TesterDataLoader(long from, long to, Set<Instrument> instruments, LoadingProgressListener loadingProgressListener)
/*     */     throws IllegalArgumentException
/*     */   {
/*  39 */     this.from = from;
/*  40 */     this.to = to;
/*  41 */     if (to <= from) {
/*  42 */       DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
/*  43 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  44 */       throw new IllegalArgumentException("Incorrect time interval. [" + format.format(Long.valueOf(from)) + "] >= [" + format.format(Long.valueOf(to)) + "]");
/*     */     }
/*  46 */     this.instruments = new HashSet(instruments);
/*  47 */     this.period = Period.TICK;
/*  48 */     this.loadingProgressListener = loadingProgressListener;
/*     */   }
/*     */ 
/*     */   public void loadData() {
/*  52 */     Iterator selectedInstrumentsIterator = this.instruments.iterator();
/*  53 */     int selectedInstrumentsCount = this.instruments.size();
/*     */     Instrument instrument;
/*  55 */     if (selectedInstrumentsIterator.hasNext())
/*  56 */       instrument = (Instrument)selectedInstrumentsIterator.next();
/*     */     else
/*  58 */       return;
/*     */     Instrument instrument;
/*  61 */     int value = 0;
/*  62 */     for (int i = 0; i < selectedInstrumentsCount; i++) {
/*  63 */       int j = this.period == Period.TICK ? 0 : 1; for (int k = Period.values().length; j < k; j++) {
/*  64 */         Period period = Period.values()[j];
/*  65 */         if (period == Period.TICK) {
/*  66 */           value += DataCacheUtils.separateChunksForCache(period, this.from, this.to).length;
/*  67 */         } else if (DataCacheUtils.isCandleBasicFast(period)) {
/*  68 */           long firstCandle = DataCacheUtils.getCandleStartFast(period, this.from);
/*  69 */           long lastCandle = DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, this.to));
/*  70 */           if (firstCandle <= lastCandle) {
/*  71 */             value += DataCacheUtils.separateChunksForCache(period, firstCandle, lastCandle).length * 2;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  77 */     int totalValue = value;
/*     */ 
/*  79 */     IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/*  80 */     LoadingProgressListener loadingProgressWrapper = new LoadingProgressListener(instrument, totalValue, selectedInstrumentsIterator) {
/*  81 */       private Instrument currentInstrument = this.val$instrument;
/*  82 */       private int currentValue = 0;
/*  83 */       private Period currentPeriod = TesterDataLoader.this.period == Period.TICK ? TesterDataLoader.this.period : Period.TEN_SECS;
/*  84 */       private OfferSide currentSide = OfferSide.ASK;
/*     */ 
/*     */       public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*     */       {
/*     */         long value;
/*     */         long value;
/*  88 */         if (this.currentPeriod == Period.TICK)
/*  89 */           value = (this.currentValue + DataCacheUtils.separateChunksForCache(this.currentPeriod, startTime, currentTime).length) * 100 / this.val$totalValue;
/*     */         else {
/*  91 */           value = (this.currentValue + DataCacheUtils.separateChunksForCache(this.currentPeriod, DataCacheUtils.getCandleStartFast(this.currentPeriod, startTime), DataCacheUtils.getPreviousCandleStartFast(this.currentPeriod, DataCacheUtils.getCandleStartFast(this.currentPeriod, currentTime))).length) * 100 / this.val$totalValue;
/*     */         }
/*     */ 
/*  96 */         TesterDataLoader.this.loadingProgressListener.dataLoaded(0L, 100L, value, information);
/*     */       }
/*     */ 
/*     */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex) {
/*     */         try {
/* 101 */           if (allDataLoaded) {
/* 102 */             if (this.currentPeriod == Period.TICK)
/* 103 */               this.currentValue += DataCacheUtils.separateChunksForCache(this.currentPeriod, startTime, currentTime).length;
/*     */             else {
/* 105 */               this.currentValue += DataCacheUtils.separateChunksForCache(this.currentPeriod, DataCacheUtils.getCandleStartFast(this.currentPeriod, startTime), DataCacheUtils.getPreviousCandleStartFast(this.currentPeriod, DataCacheUtils.getCandleStartFast(this.currentPeriod, currentTime))).length;
/*     */             }
/*     */ 
/* 110 */             if ((this.currentPeriod == Period.TICK) || (this.currentSide == OfferSide.BID))
/*     */             {
/* 112 */               this.currentSide = OfferSide.ASK;
/* 113 */               Period newPeriod = null;
/* 114 */               int i = this.currentPeriod.ordinal() + 1; for (int j = Period.values().length; i < j; i++) {
/* 115 */                 Period period = Period.values()[i];
/* 116 */                 if (DataCacheUtils.isCandleBasicFast(period)) {
/* 117 */                   long firstCandle = DataCacheUtils.getCandleStartFast(period, TesterDataLoader.this.from);
/* 118 */                   long lastCandle = DataCacheUtils.getPreviousCandleStartFast(period, DataCacheUtils.getCandleStartFast(period, TesterDataLoader.this.to));
/* 119 */                   if (firstCandle <= lastCandle) {
/* 120 */                     newPeriod = period;
/* 121 */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/* 125 */               if (newPeriod == null)
/*     */               {
/* 127 */                 this.currentPeriod = (TesterDataLoader.this.period == Period.TICK ? Period.TICK : Period.TEN_SECS);
/*     */               }
/* 129 */               else this.currentPeriod = newPeriod;
/*     */             }
/*     */             else
/*     */             {
/* 133 */               this.currentSide = OfferSide.BID;
/*     */             }
/* 135 */             if (((this.currentPeriod == Period.TICK) || ((TesterDataLoader.this.period != Period.TICK) && (this.currentPeriod == Period.TEN_SECS))) && (this.currentSide == OfferSide.ASK)) {
/* 136 */               if (this.val$selectedInstrumentsIterator.hasNext())
/* 137 */                 this.currentInstrument = ((Instrument)this.val$selectedInstrumentsIterator.next());
/*     */               else {
/* 139 */                 this.currentInstrument = null;
/*     */               }
/*     */             }
/* 142 */             if (this.currentInstrument != null) {
/* 143 */               IFeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/* 144 */               if (this.currentPeriod == Period.TICK) {
/* 145 */                 if (TesterDataLoader.LOGGER.isDebugEnabled()) {
/* 146 */                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 147 */                   dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 148 */                   TesterDataLoader.LOGGER.debug("Loading ticks for instrument [" + this.currentInstrument + "], from [" + dateFormat.format(new Date(TesterDataLoader.this.from)) + "], to [" + dateFormat.format(new Date(TesterDataLoader.this.to)) + "]");
/*     */                 }
/* 150 */                 feedDataProvider.loadTicksDataInCacheSynched(this.currentInstrument, TesterDataLoader.this.from, TesterDataLoader.this.to, this);
/*     */               } else {
/* 152 */                 long firstCandle = DataCacheUtils.getCandleStartFast(this.currentPeriod, TesterDataLoader.this.from);
/* 153 */                 long lastCandle = DataCacheUtils.getPreviousCandleStartFast(this.currentPeriod, DataCacheUtils.getCandleStartFast(this.currentPeriod, TesterDataLoader.this.to));
/* 154 */                 if (TesterDataLoader.LOGGER.isDebugEnabled()) {
/* 155 */                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 156 */                   dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 157 */                   TesterDataLoader.LOGGER.debug("Loading candles for instrument [" + this.currentInstrument + "], period [" + this.currentPeriod + "], side [" + this.currentSide + "], from [" + dateFormat.format(new Date(firstCandle)) + "], to [" + dateFormat.format(new Date(lastCandle)) + "]");
/*     */                 }
/*     */ 
/* 160 */                 feedDataProvider.loadCandlesDataInCacheSynched(this.currentInstrument, this.currentPeriod, this.currentSide, firstCandle, lastCandle, this);
/*     */               }
/*     */ 
/* 163 */               return;
/*     */             }
/* 165 */           } else if (ex != null) {
/* 166 */             TesterDataLoader.LOGGER.error(ex.getMessage(), ex);
/*     */           }
/* 168 */           long value = this.currentValue * 100 / this.val$totalValue;
/* 169 */           TesterDataLoader.this.loadingProgressListener.loadingFinished(allDataLoaded, 0L, 100L, value, ex);
/*     */         } catch (Exception e) {
/* 171 */           TesterDataLoader.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public boolean stopJob() {
/* 176 */         return TesterDataLoader.this.loadingProgressListener.stopJob();
/*     */       }
/*     */     };
/*     */     try {
/* 181 */       if (this.period == Period.TICK) {
/* 182 */         if (LOGGER.isDebugEnabled()) {
/* 183 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 184 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 185 */           LOGGER.debug("Loading ticks for instrument [" + instrument + "], from [" + dateFormat.format(new Date(this.from)) + "], to [" + dateFormat.format(new Date(this.to)) + "]");
/*     */         }
/* 187 */         feedDataProvider.loadTicksDataInCacheSynched(instrument, this.from, this.to, loadingProgressWrapper);
/*     */       } else {
/* 189 */         if (LOGGER.isDebugEnabled()) {
/* 190 */           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 191 */           dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 192 */           LOGGER.debug("Loading ticks for instrument [" + instrument + "], period [" + this.period + "], from [" + dateFormat.format(new Date(this.from)) + "], to [" + dateFormat.format(new Date(this.to)) + "]");
/*     */         }
/* 194 */         feedDataProvider.loadCandlesDataInCacheSynched(instrument, Period.TEN_SECS, OfferSide.ASK, DataCacheUtils.getCandleStartFast(Period.TEN_SECS, this.from), DataCacheUtils.getCandleStartFast(Period.TEN_SECS, this.to), loadingProgressWrapper);
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 199 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterDataLoader
 * JD-Core Version:    0.6.0
 */