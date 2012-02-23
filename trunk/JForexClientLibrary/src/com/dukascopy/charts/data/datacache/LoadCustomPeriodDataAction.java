/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodCandleLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodCandleLoadingProgressListener;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodFromCandlesCreator;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.tick.LoadCandlesFromTicksAction;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class LoadCustomPeriodDataAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*  22 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
/*     */   private final Instrument instrument;
/*     */   private final OfferSide offerSide;
/*     */   private final Period desiredPeriod;
/*     */   private final long dataSequenceStartTime;
/*     */   private final long dataSequenceEndTime;
/*     */   private final IFeedDataProvider feedDataProvider;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private final boolean loadFromChunkStart;
/*     */   private final LiveFeedListener originalLiveFeedListener;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final boolean blocking;
/*     */   private final LoadingProgressListener originalLoadingProgress;
/*     */   private final Period basicPeriod;
/*     */   private final Runnable loadDataAction;
/*     */ 
/*     */   public LoadCustomPeriodDataAction(IFeedDataProvider feedDataProvider, Instrument instrument, Period period, OfferSide offerSide, long from, long to, LiveFeedListener originalLiveFeedListener, LoadingProgressListener originalLoadingProgress, StackTraceElement[] stackTrace, boolean blocking, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart)
/*     */     throws DataCacheException
/*     */   {
/*  59 */     this.instrument = instrument;
/*  60 */     this.desiredPeriod = period;
/*  61 */     this.offerSide = offerSide;
/*  62 */     this.dataSequenceStartTime = from;
/*  63 */     this.dataSequenceEndTime = to;
/*  64 */     this.originalLiveFeedListener = originalLiveFeedListener;
/*  65 */     this.stackTrace = stackTrace;
/*  66 */     this.blocking = blocking;
/*  67 */     this.feedDataProvider = feedDataProvider;
/*  68 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*  69 */     this.loadFromChunkStart = loadFromChunkStart;
/*  70 */     this.originalLoadingProgress = originalLoadingProgress;
/*     */ 
/*  72 */     if (Period.isPeriodBasic(this.desiredPeriod) != null) {
/*  73 */       throw new IllegalArgumentException("Passed period '" + this.desiredPeriod + "' is basic period, " + getClass().getSimpleName() + " can work only with NOT basic periods");
/*     */     }
/*     */ 
/*  76 */     if (!Period.isPeriodCompliant(this.desiredPeriod))
/*     */     {
/*  80 */       throw new IllegalArgumentException("Passed period '" + this.desiredPeriod + "' is not compliant");
/*     */     }
/*     */ 
/*  83 */     if (this.dataSequenceStartTime != DataCacheUtils.getCandleStartFast(this.desiredPeriod, this.dataSequenceStartTime)) {
/*  84 */       throw new IllegalArgumentException("Data sequence start time is not candle start time");
/*     */     }
/*  86 */     if (this.dataSequenceEndTime != DataCacheUtils.getCandleStartFast(this.desiredPeriod, this.dataSequenceEndTime)) {
/*  87 */       throw new IllegalArgumentException("Data sequence end time is not candle start time");
/*     */     }
/*     */ 
/*  90 */     this.basicPeriod = Period.getBasicPeriodForCustom(this.desiredPeriod);
/*     */ 
/*  92 */     this.loadDataAction = createLoadDataActionForBasicPeriod();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  98 */     getLoadDataAction().run();
/*     */   }
/*     */ 
/*     */   private Runnable createLoadDataActionForBasicPeriod()
/*     */     throws DataCacheException
/*     */   {
/* 106 */     Runnable loadDataAction = null;
/*     */ 
/* 111 */     if (Period.TICK == this.basicPeriod) {
/* 112 */       LoadCandlesFromTicksAction loadCandlesFromTicksAction = new LoadCandlesFromTicksAction(getFeedDataProvider(), getInstrument(), getOfferSide(), Filter.NO_FILTER, getDesiredPeriod(), getIntraperiodExistsPolicy(), getOriginalLoadingProgress(), getOriginalLiveFeedListener(), getDataSequenceStartTime(), getDataSequenceEndTime());
/*     */ 
/* 125 */       return loadCandlesFromTicksAction;
/*     */     }
/*     */ 
/* 128 */     CustomPeriodFromCandlesCreator customPeriodFromCandlesCreator = new CustomPeriodFromCandlesCreator(getInstrument(), getDesiredPeriod(), getBasicPeriod(), getOfferSide());
/*     */ 
/* 135 */     customPeriodFromCandlesCreator.addListener(getOriginalLiveFeedListener());
/*     */ 
/* 137 */     CustomPeriodCandleLiveFeedListener customPeriodCandleLiveFeedListener = new CustomPeriodCandleLiveFeedListener(getInstrument(), getOfferSide(), customPeriodFromCandlesCreator, getOriginalLiveFeedListener(), getFeedDataProvider(), getDataSequenceStartTime(), getDataSequenceEndTime(), -1L);
/*     */ 
/* 148 */     CustomPeriodCandleLoadingProgressListener customPeriodLoadingProgressListener = new CustomPeriodCandleLoadingProgressListener(getOriginalLoadingProgress(), customPeriodCandleLiveFeedListener);
/*     */ 
/* 153 */     loadDataAction = new LoadDataAction(getFeedDataProvider(), getInstrument(), getBasicPeriod(), getOfferSide(), getDataSequenceStartTime(), getDataSequenceEndTimeForBasicPeriod(), customPeriodCandleLiveFeedListener, customPeriodLoadingProgressListener, getStackTrace(), getBlocking(), getIntraperiodExistsPolicy(), getLoadFromChunkStart());
/*     */ 
/* 168 */     return loadDataAction;
/*     */   }
/*     */ 
/*     */   private long getDataSequenceEndTimeForBasicPeriod()
/*     */   {
/* 176 */     long nextDesiredCandleTime = DataCacheUtils.getNextCandleStartFast(getDesiredPeriod(), getDataSequenceEndTime());
/* 177 */     long basicCandleTimeForNextDesiredCandleTime = DataCacheUtils.getCandleStartFast(getBasicPeriod(), nextDesiredCandleTime);
/*     */ 
/* 179 */     if (basicCandleTimeForNextDesiredCandleTime >= nextDesiredCandleTime) {
/* 180 */       basicCandleTimeForNextDesiredCandleTime = DataCacheUtils.getPreviousCandleStartFast(getBasicPeriod(), basicCandleTimeForNextDesiredCandleTime);
/*     */     }
/*     */ 
/* 183 */     return basicCandleTimeForNextDesiredCandleTime;
/*     */   }
/*     */ 
/*     */   private Instrument getInstrument()
/*     */   {
/* 190 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   private OfferSide getOfferSide() {
/* 194 */     return this.offerSide;
/*     */   }
/*     */ 
/*     */   private Period getDesiredPeriod() {
/* 198 */     return this.desiredPeriod;
/*     */   }
/*     */ 
/*     */   private long getDataSequenceStartTime() {
/* 202 */     return this.dataSequenceStartTime;
/*     */   }
/*     */ 
/*     */   private long getDataSequenceEndTime() {
/* 206 */     return this.dataSequenceEndTime;
/*     */   }
/*     */ 
/*     */   private IFeedDataProvider getFeedDataProvider() {
/* 210 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   private CurvesDataLoader.IntraperiodExistsPolicy getIntraperiodExistsPolicy() {
/* 214 */     return this.intraperiodExistsPolicy;
/*     */   }
/*     */ 
/*     */   private boolean getLoadFromChunkStart() {
/* 218 */     return this.loadFromChunkStart;
/*     */   }
/*     */ 
/*     */   private LiveFeedListener getOriginalLiveFeedListener() {
/* 222 */     return this.originalLiveFeedListener;
/*     */   }
/*     */ 
/*     */   private StackTraceElement[] getStackTrace() {
/* 226 */     return this.stackTrace;
/*     */   }
/*     */ 
/*     */   private boolean getBlocking() {
/* 230 */     return this.blocking;
/*     */   }
/*     */ 
/*     */   private Period getBasicPeriod() {
/* 234 */     return this.basicPeriod;
/*     */   }
/*     */ 
/*     */   private Runnable getLoadDataAction() {
/* 238 */     return this.loadDataAction;
/*     */   }
/*     */ 
/*     */   protected LoadingProgressListener getOriginalLoadingProgress() {
/* 242 */     return this.originalLoadingProgress;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  24 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadCustomPeriodDataAction
 * JD-Core Version:    0.6.0
 */