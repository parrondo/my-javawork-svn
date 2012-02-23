/*      */ package com.dukascopy.charts.data.datacache.priceaggregation.dataprovider;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.ReversalAmount;
/*      */ import com.dukascopy.api.TickBarSize;
/*      */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider.FeedExecutor;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider.FeedThreadFactory;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.LoadProgressingAction;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressAdapter;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.listener.LastPointAndFigureLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.listener.LastPriceRangeLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.listener.LastRenkoLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.listener.LastTickBarLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.pnf.AbstractPointAndFigureWithInProgressBarCheckLoader;
/*      */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.pnf.LoadLatestPointAndFigureAction;
/*      */ import com.dukascopy.charts.data.datacache.pnf.LoadNumberOfPointAndFigureAction;
/*      */ import com.dukascopy.charts.data.datacache.pnf.LoadPointAndFigureTimeIntervalAction;
/*      */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.AbstractPriceRangeWithInProgressBarCheckLoader;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.LoadLatestPriceRangeAction;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.LoadNumberOfPriceRangeAction;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.LoadPriceRangeTimeIntervalAction;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*      */ import com.dukascopy.charts.data.datacache.renko.AbstractRenkoWithInProgressBarCheckLoader;
/*      */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.renko.LoadLatestRenkoAction;
/*      */ import com.dukascopy.charts.data.datacache.renko.LoadNumberOfRenkoAction;
/*      */ import com.dukascopy.charts.data.datacache.renko.LoadRenkoTimeIntervalAction;
/*      */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.AbstractTickBarWithInProgressBarCheckLoader;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.LoadLatestTickBarAction;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.LoadNumberOfTickBarAction;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.LoadTickBarTimeIntervalAction;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.concurrent.ArrayBlockingQueue;
/*      */ import java.util.concurrent.ExecutorService;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ 
/*      */ public class PriceAggregationDataProvider
/*      */   implements IPriceAggregationDataProvider
/*      */ {
/*      */   private final IFeedDataProvider feedDataProvider;
/*   63 */   private final List<Runnable> currentlyRunningTasks = new ArrayList();
/*      */   private final ExecutorService actionsExecutorService;
/*      */   private final CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy;
/*      */ 
/*      */   public PriceAggregationDataProvider(IFeedDataProvider feedDataProvider)
/*      */   {
/*   70 */     this.feedDataProvider = feedDataProvider;
/*      */ 
/*   72 */     ThreadFactory threadFactory = new FeedDataProvider.FeedThreadFactory();
/*      */ 
/*   74 */     this.actionsExecutorService = new FeedDataProvider.FeedExecutor(5, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(15 + Instrument.values().length * 3, false), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy(), this.currentlyRunningTasks);
/*      */ 
/*   80 */     this.intraperiodExistsPolicy = feedDataProvider.getIntraperiodExistsPolicy();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/*   85 */     synchronized (this.currentlyRunningTasks) {
/*   86 */       for (Runnable currentlyRunningTask : this.currentlyRunningTasks)
/*   87 */         if ((currentlyRunningTask instanceof LoadProgressingAction))
/*   88 */           ((LoadProgressingAction)currentlyRunningTask).cancel();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeData(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter, IPriceRangeLiveFeedListener priceRangeLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  105 */     LoadNumberOfPriceRangeAction loadPriceRangesAction = createLoadPriceRangeAction(instrument, offerSide, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter, priceRange, priceRangeLiveFeedListener, loadingProgressListener);
/*      */ 
/*  115 */     this.actionsExecutorService.submit(loadPriceRangesAction);
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeData(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter, IPriceRangeLiveFeedListener priceRangeLiveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  130 */     AbstractPriceRangeWithInProgressBarCheckLoader loader = new AbstractPriceRangeWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, loadingProgressListener, instrument, offerSide, priceRange, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter, priceRangeLiveFeedListener)
/*      */     {
/*      */       public Void load() {
/*  133 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/*  134 */         PriceAggregationDataProvider.this.loadPriceRangeData(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$numberOfPriceRangesBefore, this.val$time, this.val$numberOfPriceRangesAfter, this.val$priceRangeLiveFeedListener, proxyListener);
/*  135 */         return null;
/*      */       }
/*      */     };
/*  139 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  140 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/*  143 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeDataSynched(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter, IPriceRangeLiveFeedListener priceRangeLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  160 */     LoadNumberOfPriceRangeAction loadPriceRangesAction = createLoadPriceRangeAction(instrument, offerSide, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter, priceRange, priceRangeLiveFeedListener, loadingProgressListener);
/*      */ 
/*  170 */     loadPriceRangesAction.run();
/*      */   }
/*      */ 
/*      */   public List<PriceRangeData> loadPriceRangeData(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter)
/*      */   {
/*  182 */     List result = new ArrayList();
/*  183 */     loadPriceRangeDataSynched(instrument, offerSide, priceRange, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter, new IPriceRangeLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(PriceRangeData priceRange)
/*      */       {
/*  193 */         this.val$result.add(priceRange);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/*  198 */     return result;
/*      */   }
/*      */ 
/*      */   public PriceRangeData loadLastPriceRangeData(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  209 */     LastPriceRangeLiveFeedListener liveFeedListener = new LastPriceRangeLiveFeedListener();
/*  210 */     LoadingProgressListener loadingProgressListener = new LoadingProgressAdapter()
/*      */     {
/*      */     };
/*  212 */     LoadLatestPriceRangeAction action = new LoadLatestPriceRangeAction(this.feedDataProvider, instrument, offerSide, priceRange, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  221 */     action.run();
/*  222 */     return (PriceRangeData)liveFeedListener.getLastData();
/*      */   }
/*      */ 
/*      */   private LoadNumberOfPriceRangeAction createLoadPriceRangeAction(Instrument instrument, OfferSide offerSide, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter, PriceRange priceRange, IPriceRangeLiveFeedListener priceRangeLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  237 */     LoadNumberOfPriceRangeAction loadPriceRangesAction = new LoadNumberOfPriceRangeAction(this.feedDataProvider, instrument, offerSide, priceRange, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter, priceRangeLiveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  249 */     return loadPriceRangesAction;
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureData(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  265 */     Runnable action = createLoadPointAndFigureAction(this.feedDataProvider, instrument, offerSide, boxSize, reversalAmount, beforeTimeCandlesCount, time, afterTimeCandlesCount, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  279 */     this.actionsExecutorService.submit(action);
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureData(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  295 */     AbstractPointAndFigureWithInProgressBarCheckLoader loader = new AbstractPointAndFigureWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, boxSize, reversalAmount, loadingProgressListener, instrument, offerSide, boxSize, reversalAmount, beforeTimeCandlesCount, time, afterTimeCandlesCount, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/*  298 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/*  299 */         PriceAggregationDataProvider.this.loadPointAndFigureData(this.val$instrument, this.val$offerSide, this.val$boxSize, this.val$reversalAmount, this.val$beforeTimeCandlesCount, this.val$time, this.val$afterTimeCandlesCount, this.val$liveFeedListener, proxyListener);
/*  300 */         return null;
/*      */       }
/*      */     };
/*  304 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  305 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/*  308 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureDataSynched(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  327 */     Runnable action = createLoadPointAndFigureAction(this.feedDataProvider, instrument, offerSide, boxSize, reversalAmount, beforeTimeCandlesCount, time, afterTimeCandlesCount, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  341 */     action.run();
/*      */   }
/*      */ 
/*      */   public List<PointAndFigureData> loadPointAndFigureData(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount)
/*      */   {
/*  354 */     List result = new ArrayList();
/*      */ 
/*  356 */     loadPointAndFigureDataSynched(instrument, offerSide, boxSize, reversalAmount, beforeTimeCandlesCount, time, afterTimeCandlesCount, new IPointAndFigureLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(PointAndFigureData pointAndFigure)
/*      */       {
/*  367 */         this.val$result.add(pointAndFigure);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/*  373 */     return result;
/*      */   }
/*      */ 
/*      */   public List<PointAndFigureData> loadPointAndFigureData(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  387 */     AbstractPointAndFigureWithInProgressBarCheckLoader loader = new AbstractPointAndFigureWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, boxSize, reversalAmount, instrument, offerSide, boxSize, reversalAmount, beforeTimeCandlesCount, time, afterTimeCandlesCount)
/*      */     {
/*      */       public List<PointAndFigureData> load() {
/*  390 */         return PriceAggregationDataProvider.this.loadPointAndFigureData(this.val$instrument, this.val$offerSide, this.val$boxSize, this.val$reversalAmount, this.val$beforeTimeCandlesCount, this.val$time, this.val$afterTimeCandlesCount);
/*      */       }
/*      */     };
/*  394 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  395 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/*  398 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   private Runnable createLoadPointAndFigureAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int beforeTimeCandlesCount, long time, int afterTimeCandlesCount, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*      */   {
/*  417 */     Runnable action = new LoadNumberOfPointAndFigureAction(feedDataProvider, instrument, offerSide, boxSize, reversalAmount, beforeTimeCandlesCount, time, afterTimeCandlesCount, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*      */ 
/*  431 */     return action;
/*      */   }
/*      */ 
/*      */   public PointAndFigureData loadLastPointAndFigureData(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount)
/*      */   {
/*  441 */     LastPointAndFigureLiveFeedListener liveFeedListener = new LastPointAndFigureLiveFeedListener();
/*  442 */     LoadingProgressListener loadingProgressListener = new LoadingProgressAdapter()
/*      */     {
/*      */     };
/*  444 */     LoadLatestPointAndFigureAction action = new LoadLatestPointAndFigureAction(this.feedDataProvider, instrument, offerSide, boxSize, reversalAmount, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  455 */     action.run();
/*  456 */     return (PointAndFigureData)liveFeedListener.getLastData();
/*      */   }
/*      */ 
/*      */   public void loadTickBarData(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int beforeTimeBarsCount, long time, int afterTimeBarsCount, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  471 */     Runnable action = createLoadTickBarAction(this.feedDataProvider, instrument, offerSide, beforeTimeBarsCount, time, afterTimeBarsCount, tickBarSize, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  483 */     this.actionsExecutorService.submit(action);
/*      */   }
/*      */ 
/*      */   public void loadTickBarData(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int beforeTimeBarsCount, long time, int afterTimeBarsCount, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  498 */     AbstractTickBarWithInProgressBarCheckLoader loader = new AbstractTickBarWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, tickBarSize, loadingProgressListener, instrument, offerSide, tickBarSize, beforeTimeBarsCount, time, afterTimeBarsCount, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/*  501 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/*  502 */         PriceAggregationDataProvider.this.loadTickBarData(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$beforeTimeBarsCount, this.val$time, this.val$afterTimeBarsCount, this.val$liveFeedListener, proxyListener);
/*  503 */         return null;
/*      */       }
/*      */     };
/*  507 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  508 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/*  511 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadTickBarDataSynched(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int beforeTimeBarsCount, long time, int afterTimeBarsCount, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  527 */     Runnable action = createLoadTickBarAction(this.feedDataProvider, instrument, offerSide, beforeTimeBarsCount, time, afterTimeBarsCount, tickBarSize, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  539 */     action.run();
/*      */   }
/*      */ 
/*      */   public List<TickBarData> loadTickBarData(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int beforeTimeBarsCount, long time, int afterTimeBarsCount)
/*      */   {
/*  551 */     List result = new ArrayList();
/*  552 */     loadTickBarDataSynched(instrument, offerSide, tickBarSize, beforeTimeBarsCount, time, afterTimeBarsCount, new ITickBarLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(TickBarData tickBar)
/*      */       {
/*  562 */         this.val$result.add(tickBar);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/*  567 */     return result;
/*      */   }
/*      */ 
/*      */   public List<TickBarData> loadTickBarData(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int beforeTimeBarsCount, long time, int afterTimeBarsCount, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  581 */     AbstractTickBarWithInProgressBarCheckLoader loader = new AbstractTickBarWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, tickBarSize, instrument, offerSide, tickBarSize, beforeTimeBarsCount, time, afterTimeBarsCount)
/*      */     {
/*      */       public List<TickBarData> load() {
/*  584 */         return PriceAggregationDataProvider.this.loadTickBarData(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$beforeTimeBarsCount, this.val$time, this.val$afterTimeBarsCount);
/*      */       }
/*      */     };
/*  588 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  589 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/*  592 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   public TickBarData loadLastTickBarData(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  603 */     LastTickBarLiveFeedListener liveFeedListener = new LastTickBarLiveFeedListener();
/*  604 */     LoadingProgressListener loadingProgressListener = new LoadingProgressAdapter()
/*      */     {
/*      */     };
/*  606 */     LoadLatestTickBarAction action = new LoadLatestTickBarAction(this.feedDataProvider, instrument, offerSide, tickBarSize, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  615 */     action.run();
/*  616 */     return (TickBarData)liveFeedListener.getLastData();
/*      */   }
/*      */ 
/*      */   private Runnable createLoadTickBarAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, int beforeTimeBarsCount, long time, int afterTimeBarsCount, TickBarSize tickBarSize, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*      */   {
/*  633 */     return new LoadNumberOfTickBarAction(feedDataProvider, instrument, offerSide, tickBarSize, beforeTimeBarsCount, time, afterTimeBarsCount, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeTimeIntervalSynched(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime, IPriceRangeLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  657 */     Runnable action = new LoadPriceRangeTimeIntervalAction(this.feedDataProvider, instrument, offerSide, priceRange, fromTime, toTime, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  668 */     action.run();
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeTimeIntervalSynched(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime, IPriceRangeLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  682 */     AbstractPriceRangeWithInProgressBarCheckLoader loader = new AbstractPriceRangeWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, instrument, offerSide, priceRange, fromTime, toTime, liveFeedListener, loadingProgressListener)
/*      */     {
/*      */       public Void load() {
/*  685 */         PriceAggregationDataProvider.this.loadPriceRangeTimeIntervalSynched(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$fromTime, this.val$toTime, this.val$liveFeedListener, this.val$loadingProgressListener);
/*  686 */         return null;
/*      */       }
/*      */     };
/*  690 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  691 */       loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */     else
/*  694 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime, IPriceRangeLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  709 */     Runnable action = new LoadPriceRangeTimeIntervalAction(this.feedDataProvider, instrument, offerSide, priceRange, fromTime, toTime, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  720 */     this.actionsExecutorService.submit(action);
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureTimeIntervalSynched(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long fromTime, long toTime, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  735 */     Runnable action = new LoadPointAndFigureTimeIntervalAction(this.feedDataProvider, instrument, offerSide, priceRange, reversalAmount, fromTime, toTime, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  747 */     action.run();
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureTimeIntervalSynched(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, long fromTime, long toTime, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  762 */     AbstractPointAndFigureWithInProgressBarCheckLoader loader = new AbstractPointAndFigureWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, boxSize, reversalAmount, instrument, offerSide, boxSize, reversalAmount, fromTime, toTime, liveFeedListener, loadingProgressListener)
/*      */     {
/*      */       public Void load() {
/*  765 */         PriceAggregationDataProvider.this.loadPointAndFigureTimeIntervalSynched(this.val$instrument, this.val$offerSide, this.val$boxSize, this.val$reversalAmount, this.val$fromTime, this.val$toTime, this.val$liveFeedListener, this.val$loadingProgressListener);
/*  766 */         return null;
/*      */       }
/*      */     };
/*  770 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  771 */       loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */     else
/*  774 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long fromTime, long toTime, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  791 */     Runnable action = new LoadPointAndFigureTimeIntervalAction(this.feedDataProvider, instrument, offerSide, priceRange, reversalAmount, fromTime, toTime, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  804 */     this.actionsExecutorService.submit(action);
/*      */   }
/*      */ 
/*      */   public void loadTickBarTimeIntervalSynched(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  818 */     Runnable action = new LoadTickBarTimeIntervalAction(this.feedDataProvider, instrument, offerSide, tickBarSize, fromTime, toTime, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  829 */     action.run();
/*      */   }
/*      */ 
/*      */   public void loadTickBarTimeIntervalSynched(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/*  843 */     AbstractTickBarWithInProgressBarCheckLoader loader = new AbstractTickBarWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, tickBarSize, instrument, offerSide, tickBarSize, fromTime, toTime, liveFeedListener, loadingProgressListener)
/*      */     {
/*      */       public Void load() {
/*  846 */         PriceAggregationDataProvider.this.loadTickBarTimeIntervalSynched(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$fromTime, this.val$toTime, this.val$liveFeedListener, this.val$loadingProgressListener);
/*  847 */         return null;
/*      */       }
/*      */     };
/*  851 */     if (checkOrLoadInProgressBarIfNeeded) {
/*  852 */       loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */     else
/*  855 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadTickBarTimeInterval(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/*  869 */     Runnable action = new LoadTickBarTimeIntervalAction(this.feedDataProvider, instrument, offerSide, tickBarSize, fromTime, toTime, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  880 */     this.actionsExecutorService.submit(action);
/*      */   }
/*      */ 
/*      */   public List<PointAndFigureData> loadPointAndFigureTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long fromTime, long toTime)
/*      */   {
/*  892 */     List result = new ArrayList();
/*      */ 
/*  894 */     loadPointAndFigureTimeIntervalSynched(instrument, offerSide, priceRange, reversalAmount, fromTime, toTime, new IPointAndFigureLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(PointAndFigureData pointAndFigure)
/*      */       {
/*  904 */         this.val$result.add(pointAndFigure);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/*  910 */     return result;
/*      */   }
/*      */ 
/*      */   public List<PriceRangeData> loadPriceRangeTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime)
/*      */   {
/*  921 */     List result = new ArrayList();
/*      */ 
/*  923 */     loadPriceRangeTimeIntervalSynched(instrument, offerSide, priceRange, fromTime, toTime, new IPriceRangeLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(PriceRangeData priceRange)
/*      */       {
/*  932 */         this.val$result.add(priceRange);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/*  938 */     return result;
/*      */   }
/*      */ 
/*      */   public List<TickBarData> loadTickBarTimeInterval(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime)
/*      */   {
/*  949 */     List result = new ArrayList();
/*      */ 
/*  951 */     loadTickBarTimeIntervalSynched(instrument, offerSide, tickBarSize, fromTime, toTime, new ITickBarLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(TickBarData tickBar)
/*      */       {
/*  960 */         this.val$result.add(tickBar);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/*  966 */     return result;
/*      */   }
/*      */ 
/*      */   public RenkoData loadLastRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/*  975 */     LastRenkoLiveFeedListener liveFeedListener = new LastRenkoLiveFeedListener();
/*  976 */     LoadingProgressListener loadingProgressListener = new LoadingProgressAdapter()
/*      */     {
/*      */     };
/*  978 */     LoadLatestRenkoAction action = new LoadLatestRenkoAction(this.feedDataProvider, instrument, offerSide, brickSize, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/*  987 */     action.run();
/*  988 */     return (RenkoData)liveFeedListener.getLastData();
/*      */   }
/*      */ 
/*      */   public void loadRenkoData(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numOfBarsBefore, long time, int numOfBarsAfter, IRenkoLiveFeedListener renkoLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/* 1002 */     LoadNumberOfRenkoAction loadRenkoAction = createLoadRenkoAction(instrument, offerSide, brickSize, numOfBarsBefore, time, numOfBarsAfter, renkoLiveFeedListener, loadingProgressListener);
/*      */ 
/* 1012 */     this.actionsExecutorService.submit(loadRenkoAction);
/*      */   }
/*      */ 
/*      */   public void loadRenkoData(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numOfBarsBefore, long time, int numOfBarsAfter, IRenkoLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1027 */     AbstractRenkoWithInProgressBarCheckLoader loader = new AbstractRenkoWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, brickSize, loadingProgressListener, instrument, offerSide, brickSize, numOfBarsBefore, time, numOfBarsAfter, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/* 1030 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/* 1031 */         PriceAggregationDataProvider.this.loadRenkoData(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$numOfBarsBefore, this.val$time, this.val$numOfBarsAfter, this.val$liveFeedListener, proxyListener);
/* 1032 */         return null;
/*      */       }
/*      */     };
/* 1036 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1037 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/* 1040 */       loader.load();
/*      */   }
/*      */ 
/*      */   private LoadNumberOfRenkoAction createLoadRenkoAction(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRenkoLiveFeedListener renkoLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/* 1055 */     LoadNumberOfRenkoAction loadPriceRangesAction = new LoadNumberOfRenkoAction(this.feedDataProvider, instrument, offerSide, brickSize, numberOfBarsBefore, time, numberOfBarsAfter, renkoLiveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/* 1067 */     return loadPriceRangesAction;
/*      */   }
/*      */ 
/*      */   public void loadRenkoDataSynched(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numOfBarsBefore, long time, int numOfBarsAfter, IRenkoLiveFeedListener renkoLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/* 1081 */     LoadNumberOfRenkoAction loadRenkoAction = createLoadRenkoAction(instrument, offerSide, brickSize, numOfBarsBefore, time, numOfBarsAfter, renkoLiveFeedListener, loadingProgressListener);
/*      */ 
/* 1091 */     loadRenkoAction.run();
/*      */   }
/*      */ 
/*      */   public List<RenkoData> loadRenkoTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to)
/*      */   {
/* 1102 */     List result = new ArrayList();
/*      */ 
/* 1104 */     loadRenkoTimeIntervalSynched(instrument, offerSide, brickSize, from, to, new IRenkoLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(RenkoData renko)
/*      */       {
/* 1113 */         this.val$result.add(renko);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/* 1119 */     return result;
/*      */   }
/*      */ 
/*      */   public void loadRenkoTimeIntervalSynched(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoLiveFeedListener renkoLiveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/* 1132 */     Runnable action = new LoadRenkoTimeIntervalAction(this.feedDataProvider, instrument, offerSide, brickSize, from, to, renkoLiveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/* 1143 */     action.run();
/*      */   }
/*      */ 
/*      */   public void loadRenkoTimeIntervalSynched(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoLiveFeedListener renkoLiveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1157 */     AbstractRenkoWithInProgressBarCheckLoader loader = new AbstractRenkoWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, brickSize, instrument, offerSide, brickSize, from, to, renkoLiveFeedListener, loadingProgressListener)
/*      */     {
/*      */       public Void load() {
/* 1160 */         PriceAggregationDataProvider.this.loadRenkoTimeIntervalSynched(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$from, this.val$to, this.val$renkoLiveFeedListener, this.val$loadingProgressListener);
/* 1161 */         return null;
/*      */       }
/*      */     };
/* 1165 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1166 */       loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */     else
/* 1169 */       loader.load();
/*      */   }
/*      */ 
/*      */   public List<RenkoData> loadRenkoData(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter)
/*      */   {
/* 1183 */     List result = new ArrayList();
/* 1184 */     loadRenkoDataSynched(instrument, offerSide, brickSize, numberOfBarsBefore, time, numberOfBarsAfter, new IRenkoLiveFeedListener(result)
/*      */     {
/*      */       public void newPriceData(RenkoData renko)
/*      */       {
/* 1194 */         this.val$result.add(renko);
/*      */       }
/*      */     }
/*      */     , new LoadingProgressAdapter()
/*      */     {
/*      */     });
/* 1199 */     return result;
/*      */   }
/*      */ 
/*      */   public void loadRenkoTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener)
/*      */   {
/* 1212 */     Runnable action = new LoadRenkoTimeIntervalAction(this.feedDataProvider, instrument, offerSide, brickSize, from, to, liveFeedListener, loadingProgressListener, this.intraperiodExistsPolicy);
/*      */ 
/* 1223 */     this.actionsExecutorService.submit(action);
/*      */   }
/*      */ 
/*      */   public IFeedDataProvider getFeedDataProvider()
/*      */   {
/* 1228 */     return this.feedDataProvider;
/*      */   }
/*      */ 
/*      */   public void loadPriceRangeTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime, IPriceRangeLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1242 */     AbstractPriceRangeWithInProgressBarCheckLoader loader = new AbstractPriceRangeWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, loadingProgressListener, instrument, offerSide, priceRange, fromTime, toTime, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/* 1245 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/* 1246 */         PriceAggregationDataProvider.this.loadPriceRangeTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$fromTime, this.val$toTime, this.val$liveFeedListener, proxyListener);
/* 1247 */         return null;
/*      */       }
/*      */     };
/* 1251 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1252 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/* 1255 */       loader.load();
/*      */   }
/*      */ 
/*      */   public List<PriceRangeData> loadPriceRangeData(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfPriceRangesBefore, long time, int numberOfPriceRangesAfter, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1269 */     AbstractPriceRangeWithInProgressBarCheckLoader loader = new AbstractPriceRangeWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, instrument, offerSide, priceRange, numberOfPriceRangesBefore, time, numberOfPriceRangesAfter)
/*      */     {
/*      */       public List<PriceRangeData> load() {
/* 1272 */         return PriceAggregationDataProvider.this.loadPriceRangeData(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$numberOfPriceRangesBefore, this.val$time, this.val$numberOfPriceRangesAfter);
/*      */       }
/*      */     };
/* 1276 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1277 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/* 1280 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   private <R> R loadBarsWithInProgressBarCheck(IBarsWithInProgressBarCheckLoader<R> loader, boolean removeListener)
/*      */   {
/* 1289 */     boolean inProgressBarExists = loader.doesInProgressBarExistWaitIfCurrentlyLoading();
/* 1290 */     if (inProgressBarExists) {
/* 1291 */       return loader.load();
/*      */     }
/*      */ 
/* 1294 */     loader.addInProgressListener();
/*      */     try {
/* 1296 */       inProgressBarExists = loader.doesInProgressBarExistWaitIfCurrentlyLoading();
/* 1297 */       if (!inProgressBarExists) {
/* 1298 */         throw new IllegalArgumentException("In progress bar is not loaded, could not load history further");
/*      */       }
/*      */ 
/* 1301 */       Object localObject1 = loader.load();
/*      */       return localObject1;
/*      */     }
/*      */     finally
/*      */     {
/* 1304 */       if (removeListener)
/* 1305 */         loader.removeInProgressListener(); 
/* 1305 */     }throw localObject2;
/*      */   }
/*      */ 
/*      */   public void loadPointAndFigureTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long fromTime, long toTime, IPointAndFigureLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1323 */     AbstractPointAndFigureWithInProgressBarCheckLoader loader = new AbstractPointAndFigureWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, reversalAmount, loadingProgressListener, instrument, offerSide, priceRange, reversalAmount, fromTime, toTime, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/* 1326 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/* 1327 */         PriceAggregationDataProvider.this.loadPointAndFigureTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, this.val$fromTime, this.val$toTime, this.val$liveFeedListener, proxyListener);
/* 1328 */         return null;
/*      */       }
/*      */     };
/* 1332 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1333 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/* 1336 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadTickBarTimeInterval(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime, ITickBarLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1351 */     AbstractTickBarWithInProgressBarCheckLoader loader = new AbstractTickBarWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, tickBarSize, loadingProgressListener, instrument, offerSide, tickBarSize, fromTime, toTime, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/* 1354 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/* 1355 */         PriceAggregationDataProvider.this.loadTickBarTimeInterval(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$fromTime, this.val$toTime, this.val$liveFeedListener, proxyListener);
/* 1356 */         return null;
/*      */       }
/*      */     };
/* 1360 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1361 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/* 1364 */       loader.load();
/*      */   }
/*      */ 
/*      */   public void loadRenkoTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoLiveFeedListener liveFeedListener, LoadingProgressListener loadingProgressListener, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1379 */     AbstractRenkoWithInProgressBarCheckLoader loader = new AbstractRenkoWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, brickSize, loadingProgressListener, instrument, offerSide, brickSize, from, to, liveFeedListener)
/*      */     {
/*      */       public Void load() {
/* 1382 */         BarsLoadingProgressListener proxyListener = new BarsLoadingProgressListener(this.val$loadingProgressListener, this);
/* 1383 */         PriceAggregationDataProvider.this.loadRenkoTimeInterval(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$from, this.val$to, this.val$liveFeedListener, proxyListener);
/* 1384 */         return null;
/*      */       }
/*      */     };
/* 1388 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1389 */       loadBarsWithInProgressBarCheck(loader, false);
/*      */     }
/*      */     else
/* 1392 */       loader.load();
/*      */   }
/*      */ 
/*      */   public List<RenkoData> loadRenkoData(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1405 */     AbstractRenkoWithInProgressBarCheckLoader loader = new AbstractRenkoWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, brickSize, instrument, offerSide, brickSize, numberOfBarsBefore, time, numberOfBarsAfter)
/*      */     {
/*      */       public List<RenkoData> load() {
/* 1408 */         return PriceAggregationDataProvider.this.loadRenkoData(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$numberOfBarsBefore, this.val$time, this.val$numberOfBarsAfter);
/*      */       }
/*      */     };
/* 1412 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1413 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/* 1416 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   public List<RenkoData> loadRenkoTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1430 */     AbstractRenkoWithInProgressBarCheckLoader loader = new AbstractRenkoWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, brickSize, instrument, offerSide, brickSize, from, to)
/*      */     {
/*      */       public List<RenkoData> load() {
/* 1433 */         return PriceAggregationDataProvider.this.loadRenkoTimeInterval(this.val$instrument, this.val$offerSide, this.val$brickSize, this.val$from, this.val$to);
/*      */       }
/*      */     };
/* 1437 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1438 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/* 1441 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   public List<PriceRangeData> loadPriceRangeTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long fromTime, long toTime, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1454 */     AbstractPriceRangeWithInProgressBarCheckLoader loader = new AbstractPriceRangeWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, instrument, offerSide, priceRange, fromTime, toTime)
/*      */     {
/*      */       public List<PriceRangeData> load() {
/* 1457 */         return PriceAggregationDataProvider.this.loadPriceRangeTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$fromTime, this.val$toTime);
/*      */       }
/*      */     };
/* 1461 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1462 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/* 1465 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   public List<TickBarData> loadTickBarTimeInterval(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long fromTime, long toTime, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1478 */     AbstractTickBarWithInProgressBarCheckLoader loader = new AbstractTickBarWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, tickBarSize, instrument, offerSide, tickBarSize, fromTime, toTime)
/*      */     {
/*      */       public List<TickBarData> load() {
/* 1481 */         return PriceAggregationDataProvider.this.loadTickBarTimeInterval(this.val$instrument, this.val$offerSide, this.val$tickBarSize, this.val$fromTime, this.val$toTime);
/*      */       }
/*      */     };
/* 1485 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1486 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/* 1489 */     return (List)loader.load();
/*      */   }
/*      */ 
/*      */   public List<PointAndFigureData> loadPointAndFigureTimeInterval(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, long fromTime, long toTime, boolean checkOrLoadInProgressBarIfNeeded)
/*      */   {
/* 1503 */     AbstractPointAndFigureWithInProgressBarCheckLoader loader = new AbstractPointAndFigureWithInProgressBarCheckLoader(this.feedDataProvider, instrument, offerSide, priceRange, reversalAmount, instrument, offerSide, priceRange, reversalAmount, fromTime, toTime)
/*      */     {
/*      */       public List<PointAndFigureData> load() {
/* 1506 */         return PriceAggregationDataProvider.this.loadPointAndFigureTimeInterval(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, this.val$fromTime, this.val$toTime);
/*      */       }
/*      */     };
/* 1510 */     if (checkOrLoadInProgressBarIfNeeded) {
/* 1511 */       return (List)loadBarsWithInProgressBarCheck(loader, true);
/*      */     }
/*      */ 
/* 1514 */     return (List)loader.load();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationDataProvider
 * JD-Core Version:    0.6.0
 */