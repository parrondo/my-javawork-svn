/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodCandleLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.candle.CustomPeriodFromCandlesCreator;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.tick.CustomPeriodFromTicksCreator;
/*     */ import com.dukascopy.charts.data.datacache.customperiod.tick.CustomPeriodTickLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*     */ 
/*     */ public class LoadCustomPeriodNumberOfLastAvailableDataAction extends LoadProgressingAction
/*     */   implements Runnable
/*     */ {
/*     */   private final Instrument instrument;
/*     */   private final Period desiredPeriod;
/*     */   private final Period basicPeriod;
/*     */   private final OfferSide side;
/*     */   private final int numberOfCandles;
/*     */   private final long to;
/*     */   private final StackTraceElement[] stackTrace;
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private final Filter filter;
/*     */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*     */   private final Runnable loadNumberOfLastAvailableDataAction;
/*     */   private final LoadingProgressListener originalLoadingProgress;
/*     */   private final LiveFeedListener originalLiveFeedListener;
/*     */   private CustomPeriodTickLiveFeedListener tickLiveFeedListener;
/*     */   private CustomPeriodCandleLiveFeedListener candleLiveFeedListener;
/*     */ 
/*     */   public LoadCustomPeriodNumberOfLastAvailableDataAction(FeedDataProvider feedDataProvider, Instrument instrument, Period desiredPeriod, OfferSide side, int numberOfCandles, long to, Filter filter, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, StackTraceElement[] stackTrace)
/*     */     throws DataCacheException
/*     */   {
/*  54 */     this.instrument = instrument;
/*  55 */     this.desiredPeriod = desiredPeriod;
/*  56 */     this.side = side;
/*  57 */     this.numberOfCandles = numberOfCandles;
/*  58 */     this.to = to;
/*  59 */     this.originalLiveFeedListener = candleListener;
/*  60 */     this.stackTrace = stackTrace;
/*  61 */     this.feedDataProvider = feedDataProvider;
/*  62 */     this.filter = filter;
/*  63 */     this.originalLoadingProgress = loadingProgress;
/*  64 */     this.intraperiodExistsPolicy = intraperiodExistsPolicy;
/*     */ 
/*  67 */     if (Period.isPeriodBasic(desiredPeriod) != null) {
/*  68 */       throw new IllegalArgumentException("Passed period '" + desiredPeriod + "' is basic period, " + getClass().getSimpleName() + " can work only with NOT basic periods");
/*     */     }
/*     */ 
/*  71 */     if (!Period.isPeriodCompliant(desiredPeriod))
/*     */     {
/*  75 */       throw new IllegalArgumentException("Passed period '" + desiredPeriod + "' is not compliant");
/*     */     }
/*     */ 
/*  78 */     this.basicPeriod = Period.getBasicPeriodForCustom(desiredPeriod);
/*     */ 
/*  80 */     this.loadNumberOfLastAvailableDataAction = createLoadNumberOfLastAvailableDataAction();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  86 */     if (getOriginalLoadingProgress().stopJob()) {
/*  87 */       getOriginalLoadingProgress().loadingFinished(false, 0L, 0L, 0L, null);
/*  88 */       return;
/*     */     }
/*     */ 
/*  91 */     getLoadNumberOfLastAvailableDataAction().run();
/*     */ 
/*  93 */     if (Period.TICK.equals(this.basicPeriod))
/*     */     {
/*  97 */       getTickLiveFeedListener().reverseCollectedDatas();
/*     */ 
/* 101 */       getTickLiveFeedListener().analyseTickDataPortion();
/*     */ 
/* 103 */       CandleData[] result = getTickLiveFeedListener().getCustomPeriodFromTicksCreator().getResult();
/* 104 */       CandleData[] reversedResult = new CandleData[result.length];
/* 105 */       TimeDataUtils.reverseArray(result, reversedResult);
/*     */ 
/* 107 */       for (CandleData data : reversedResult) {
/* 108 */         if (data == null)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 117 */         getOriginalLiveFeedListener().newCandle(getInstrument(), getDesiredPeriod(), getSide(), data.getTime(), data.getOpen(), data.getClose(), data.getLow(), data.getHigh(), data.getVolume());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Runnable createLoadNumberOfLastAvailableDataAction()
/*     */     throws NoDataForPeriodException, DataCacheException
/*     */   {
/* 133 */     LoadNumberOfLastAvailableDataAction loadLastAvailableDataAction = null;
/*     */ 
/* 137 */     if (Period.TICK.equals(this.basicPeriod)) {
/* 138 */       CustomPeriodFromTicksCreator customPeriodFromTicksCreator = new CustomPeriodFromTicksCreator(getInstrument(), getSide(), getNumberOfCandles(), getDesiredPeriod(), getFilter(), false, null, null, this.feedDataProvider.getFilterManager());
/*     */ 
/* 150 */       this.tickLiveFeedListener = new CustomPeriodTickLiveFeedListener(customPeriodFromTicksCreator);
/*     */ 
/* 154 */       loadLastAvailableDataAction = new LoadNumberOfLastAvailableDataAction(getFeedDataProvider(), getInstrument(), getNumberOfCandles(), getTo(), getFilter(), this.intraperiodExistsPolicy, this.tickLiveFeedListener, getOriginalLoadingProgress(), getStackTrace());
/*     */     }
/*     */     else
/*     */     {
/* 167 */       CustomPeriodFromCandlesCreator customPeriodFromCandlesCreator = new CustomPeriodFromCandlesCreator(getInstrument(), getDesiredPeriod(), getBasicPeriod(), getSide());
/*     */ 
/* 174 */       this.candleLiveFeedListener = new CustomPeriodCandleLiveFeedListener(getInstrument(), getSide(), customPeriodFromCandlesCreator, getOriginalLiveFeedListener(), getFeedDataProvider(), getNumberOfCandles(), 0, getTo());
/*     */ 
/* 185 */       loadLastAvailableDataAction = new LoadNumberOfLastAvailableDataAction(getFeedDataProvider(), getInstrument(), getBasicPeriod(), getSide(), getNumberOfCandles(), getTo(), getFilter(), this.intraperiodExistsPolicy, this.candleLiveFeedListener, getOriginalLoadingProgress(), getStackTrace());
/*     */     }
/*     */ 
/* 200 */     return loadLastAvailableDataAction;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 208 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public Period getDesiredPeriod() {
/* 212 */     return this.desiredPeriod;
/*     */   }
/*     */ 
/*     */   public OfferSide getSide() {
/* 216 */     return this.side;
/*     */   }
/*     */ 
/*     */   public int getNumberOfCandles() {
/* 220 */     return this.numberOfCandles;
/*     */   }
/*     */ 
/*     */   public long getTo() {
/* 224 */     return this.to;
/*     */   }
/*     */ 
/*     */   public LiveFeedListener getOriginalLiveFeedListener() {
/* 228 */     return this.originalLiveFeedListener;
/*     */   }
/*     */ 
/*     */   public StackTraceElement[] getStackTrace() {
/* 232 */     return this.stackTrace;
/*     */   }
/*     */ 
/*     */   public FeedDataProvider getFeedDataProvider() {
/* 236 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   public Filter getFilter() {
/* 240 */     return this.filter;
/*     */   }
/*     */ 
/*     */   public Period getBasicPeriod() {
/* 244 */     return this.basicPeriod;
/*     */   }
/*     */ 
/*     */   public Runnable getLoadNumberOfLastAvailableDataAction() {
/* 248 */     return this.loadNumberOfLastAvailableDataAction;
/*     */   }
/*     */ 
/*     */   public LoadingProgressListener getOriginalLoadingProgress() {
/* 252 */     return this.originalLoadingProgress;
/*     */   }
/*     */ 
/*     */   public CustomPeriodTickLiveFeedListener getTickLiveFeedListener() {
/* 256 */     return this.tickLiveFeedListener;
/*     */   }
/*     */ 
/*     */   public CustomPeriodCandleLiveFeedListener getCandleLiveFeedListener() {
/* 260 */     return this.candleLiveFeedListener;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LoadCustomPeriodNumberOfLastAvailableDataAction
 * JD-Core Version:    0.6.0
 */