/*      */ package com.dukascopy.charts.data.datacache.intraperiod;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.ReversalAmount;
/*      */ import com.dukascopy.api.TickBarSize;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.charts.data.datacache.pnf.FlowPointAndFigureFromTicksCreator;
/*      */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureCreator;
/*      */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*      */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureLiveFeedAdapter;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationCreator;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.FlowPriceRangeCreator;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeCreator;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*      */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeLiveFeedAdapter;
/*      */ import com.dukascopy.charts.data.datacache.renko.FlowRenkoCreator;
/*      */ import com.dukascopy.charts.data.datacache.renko.IRenkoCreator;
/*      */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.FlowTickBarCreator;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarCreator;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*      */ import com.dukascopy.charts.data.datacache.tickbar.TickBarLiveFeedAdapter;
/*      */ import com.dukascopy.charts.utils.map.ISynchronizedFourKeyMap;
/*      */ import com.dukascopy.charts.utils.map.ISynchronizedThreeKeyMap;
/*      */ import com.dukascopy.charts.utils.map.SynchronizedFourKeyMap;
/*      */ import com.dukascopy.charts.utils.map.SynchronizedThreeKeyMap;
/*      */ import com.dukascopy.charts.utils.map.entry.IFourKeyEntry;
/*      */ import com.dukascopy.charts.utils.map.entry.IThreeKeyEntry;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class IntraperiodBarsGenerator
/*      */   implements IIntraperiodBarsGenerator
/*      */ {
/*   53 */   private static final Logger LOGGER = LoggerFactory.getLogger(IntraperiodBarsGenerator.class);
/*      */ 
/*   55 */   private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
/*      */ 
/*   61 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, IPriceRangeCreator> inProgressPriceRangeCreatorsMap = new SynchronizedThreeKeyMap();
/*   62 */   private final ISynchronizedFourKeyMap<Instrument, OfferSide, PriceRange, ReversalAmount, IPointAndFigureCreator> inProgressPointAndFigureCreatorsMap = new SynchronizedFourKeyMap();
/*   63 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, TickBarSize, ITickBarCreator> inProgressTickBarCreatorsMap = new SynchronizedThreeKeyMap();
/*   64 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, IRenkoCreator> inProgressRenkoCreatorsMap = new SynchronizedThreeKeyMap();
/*      */ 
/*   67 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IPriceRangeLiveFeedListener>> inProgressPriceRangeLiveFeedListenersMap = new SynchronizedThreeKeyMap();
/*   68 */   private final ISynchronizedFourKeyMap<Instrument, OfferSide, PriceRange, ReversalAmount, List<IPointAndFigureLiveFeedListener>> inProgressPointAndFigureListenersMap = new SynchronizedFourKeyMap();
/*   69 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, TickBarSize, List<ITickBarLiveFeedListener>> inProgressTickBarLiveFeedListenersMap = new SynchronizedThreeKeyMap();
/*   70 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IRenkoLiveFeedListener>> inProgressRenkoLiveFeedListenersMap = new SynchronizedThreeKeyMap();
/*      */ 
/*   73 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IPriceRangeLiveFeedListener>> priceRangeNotificationListenersMap = new SynchronizedThreeKeyMap();
/*   74 */   private final ISynchronizedFourKeyMap<Instrument, OfferSide, PriceRange, ReversalAmount, List<IPointAndFigureLiveFeedListener>> pointAndFigureNotificationListenersMap = new SynchronizedFourKeyMap();
/*   75 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, TickBarSize, List<ITickBarLiveFeedListener>> tickBarNotificationListenersMap = new SynchronizedThreeKeyMap();
/*   76 */   private final ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IRenkoLiveFeedListener>> renkoNotificationListenersMap = new SynchronizedThreeKeyMap();
/*      */   private final IPriceAggregationDataProvider priceAggregationDataProvider;
/*      */ 
/*      */   public IntraperiodBarsGenerator(IPriceAggregationDataProvider priceAggregationDataProvider)
/*      */   {
/*   85 */     this.priceAggregationDataProvider = priceAggregationDataProvider;
/*      */   }
/*      */ 
/*      */   private void processTickForTickBars(Instrument instrument, TickData tickData)
/*      */   {
/*   92 */     synchronized (this.inProgressTickBarCreatorsMap) {
/*   93 */       Set entrySet = this.inProgressTickBarCreatorsMap.entrySet(instrument);
/*   94 */       for (IThreeKeyEntry entry : entrySet) {
/*   95 */         ITickBarCreator creator = (ITickBarCreator)entry.getValue();
/*      */ 
/*   97 */         if ((creator == null) || (creator.getLastData() == null))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  104 */         boolean currentBarFormingFinished = creator.analyse(tickData);
/*  105 */         if (!currentBarFormingFinished)
/*  106 */           fireInProgressTickBarUpdated((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (TickBarSize)entry.getKey3(), (TickBarData)clone(creator.getLastData()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processTickForPointAndFigures(Instrument instrument, TickData tickData)
/*      */   {
/*  116 */     synchronized (this.inProgressPointAndFigureCreatorsMap) {
/*  117 */       Set entrySet = this.inProgressPointAndFigureCreatorsMap.entrySet(instrument);
/*  118 */       for (IFourKeyEntry entry : entrySet) {
/*  119 */         IPointAndFigureCreator creator = (IPointAndFigureCreator)entry.getValue();
/*  120 */         if ((creator == null) || (creator.getLastData() == null))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  126 */         boolean currentBarFormingFinished = creator.analyse(tickData);
/*  127 */         if (!currentBarFormingFinished)
/*  128 */           fireInProgressPointAndFigureUpdated((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (PriceRange)entry.getKey3(), (ReversalAmount)entry.getKey4(), (PointAndFigureData)clone(creator.getLastData()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processTickForPriceRanges(Instrument instrument, TickData tickData)
/*      */   {
/*  138 */     synchronized (this.inProgressPriceRangeCreatorsMap) {
/*  139 */       Set entrySet = this.inProgressPriceRangeCreatorsMap.entrySet(instrument);
/*  140 */       for (IThreeKeyEntry entry : entrySet) {
/*  141 */         IPriceRangeCreator creator = (IPriceRangeCreator)entry.getValue();
/*  142 */         if ((creator == null) || (creator.getLastData() == null))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  148 */         boolean currentBarFormingFinished = creator.analyse(tickData);
/*  149 */         if (!currentBarFormingFinished)
/*  150 */           fireInProgressPriceRangeUpdated((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (PriceRange)entry.getKey3(), (PriceRangeData)clone(creator.getLastData()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void processTickForRenkos(Instrument instrument, TickData tickData)
/*      */   {
/*  161 */     synchronized (this.inProgressRenkoCreatorsMap)
/*      */     {
/*  163 */       Set entrySet = this.inProgressRenkoCreatorsMap.entrySet(instrument);
/*  164 */       for (IThreeKeyEntry entry : entrySet) {
/*  165 */         IRenkoCreator creator = (IRenkoCreator)entry.getValue();
/*  166 */         if ((creator == null) || (creator.getLastData() == null))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  172 */         boolean currentBarFormingFinished = creator.analyse(tickData);
/*  173 */         if (!currentBarFormingFinished)
/*  174 */           fireInProgressRenkoUpdated((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (PriceRange)entry.getKey3(), (RenkoData)clone(creator.getLastData()));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void processTick(Instrument instrument, TickData tickData)
/*      */   {
/*      */     try
/*      */     {
/*  183 */       processTickForPriceRanges(instrument, tickData);
/*      */     } catch (Throwable t) {
/*  185 */       LOGGER.error(t.getMessage(), t);
/*      */     }
/*      */     try
/*      */     {
/*  189 */       processTickForPointAndFigures(instrument, tickData);
/*      */     } catch (Throwable t) {
/*  191 */       LOGGER.error(t.getMessage(), t);
/*      */     }
/*      */     try
/*      */     {
/*  195 */       processTickForTickBars(instrument, tickData);
/*      */     } catch (Throwable t) {
/*  197 */       LOGGER.error(t.getMessage(), t);
/*      */     }
/*      */     try
/*      */     {
/*  201 */       processTickForRenkos(instrument, tickData);
/*      */     } catch (Throwable t) {
/*  203 */       LOGGER.error(t.getMessage(), t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startToFillInProgressPriceRange(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  214 */     synchronized (this.inProgressPriceRangeCreatorsMap) {
/*  215 */       IPriceRangeCreator creator = (IPriceRangeCreator)this.inProgressPriceRangeCreatorsMap.get(instrument, offerSide, priceRange);
/*  216 */       if (creator == null)
/*      */       {
/*  220 */         creator = new FlowPriceRangeCreator(instrument, offerSide, priceRange);
/*      */ 
/*  222 */         creator.addListener(new PriceRangeLiveFeedAdapter(instrument, offerSide, priceRange)
/*      */         {
/*      */           public void newPriceData(PriceRangeData bar) {
/*  225 */             IntraperiodBarsGenerator.this.fireInProgressPriceRangeFormed(this.val$instrument, this.val$offerSide, this.val$priceRange, (PriceRangeData)IntraperiodBarsGenerator.access$000(bar));
/*      */           }
/*      */         });
/*  229 */         this.inProgressPriceRangeCreatorsMap.put(instrument, offerSide, priceRange, creator);
/*      */ 
/*  231 */         asynchLoadLastPriceRange(instrument, offerSide, priceRange, creator);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void asynchLoadLastPriceRange(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IPriceRangeCreator creator)
/*      */   {
/*  242 */     Runnable runnable = new Object(instrument, offerSide, priceRange, creator)
/*      */     {
/*      */       public void run() {
/*      */         try {
/*  246 */           boolean ticksExist = IntraperiodBarsGenerator.this.waitUntilTickArrives(this.val$instrument);
/*  247 */           if (!ticksExist) {
/*  248 */             throw new RuntimeException("There is no ticks for insrtument <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/*  251 */           PriceRangeData lastBar = IntraperiodBarsGenerator.this.loadLastPriceRange(this.val$instrument, this.val$offerSide, this.val$priceRange);
/*      */ 
/*  253 */           if (lastBar == null) {
/*  254 */             throw new RuntimeException("Unable to load last range bar for <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/*  257 */           synchronized (IntraperiodBarsGenerator.this.inProgressPriceRangeCreatorsMap) {
/*  258 */             this.val$creator.setupLastData(lastBar);
/*      */           }
/*      */         } catch (Throwable t) {
/*  261 */           IntraperiodBarsGenerator.LOGGER.error("Failed to load in progress bar " + t.getLocalizedMessage(), t);
/*  262 */           IntraperiodBarsGenerator.this.stopToFillInProgressPriceRange(this.val$instrument, this.val$offerSide, this.val$priceRange);
/*      */         }
/*      */       }
/*      */     };
/*  266 */     invoke(runnable);
/*      */   }
/*      */ 
/*      */   private PriceRangeData loadLastPriceRange(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  274 */     PriceRangeData lastBar = this.priceAggregationDataProvider.loadLastPriceRangeData(instrument, offerSide, priceRange);
/*  275 */     return lastBar;
/*      */   }
/*      */ 
/*      */   private void stopToFillInProgressPriceRange(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  283 */     synchronized (this.inProgressPriceRangeCreatorsMap) {
/*  284 */       this.inProgressPriceRangeCreatorsMap.remove(instrument, offerSide, priceRange);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startToFillInProgressPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  295 */     synchronized (this.inProgressPointAndFigureCreatorsMap) {
/*  296 */       IPointAndFigureCreator creator = (IPointAndFigureCreator)this.inProgressPointAndFigureCreatorsMap.get(instrument, offerSide, priceRange, reversalAmount);
/*      */ 
/*  298 */       if (creator == null)
/*      */       {
/*  302 */         creator = new FlowPointAndFigureFromTicksCreator(instrument, priceRange, reversalAmount, offerSide);
/*      */ 
/*  304 */         creator.addListener(new PointAndFigureLiveFeedAdapter(instrument, offerSide, priceRange, reversalAmount)
/*      */         {
/*      */           public void newPriceData(PointAndFigureData bar) {
/*  307 */             IntraperiodBarsGenerator.this.fireInProgressPointAndFigureFormed(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount, (PointAndFigureData)IntraperiodBarsGenerator.access$000(bar));
/*      */           }
/*      */         });
/*  311 */         this.inProgressPointAndFigureCreatorsMap.put(instrument, offerSide, priceRange, reversalAmount, creator);
/*      */ 
/*  313 */         asynchLoadLastPointAndFigure(instrument, offerSide, priceRange, reversalAmount, creator);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void asynchLoadLastPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, IPointAndFigureCreator creator)
/*      */   {
/*  326 */     Runnable runnable = new Object(instrument, offerSide, priceRange, reversalAmount, creator)
/*      */     {
/*      */       public void run() {
/*      */         try {
/*  330 */           boolean ticksExist = IntraperiodBarsGenerator.this.waitUntilTickArrives(this.val$instrument);
/*  331 */           if (!ticksExist) {
/*  332 */             throw new RuntimeException("There is no ticks for insrtument <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/*  335 */           PointAndFigureData lastBar = IntraperiodBarsGenerator.this.loadLastPointAndFigure(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount);
/*      */ 
/*  337 */           if (lastBar == null) {
/*  338 */             throw new RuntimeException("Unable to load last p&f for <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/*  341 */           synchronized (IntraperiodBarsGenerator.this.inProgressPointAndFigureCreatorsMap) {
/*  342 */             this.val$creator.setupLastData(lastBar);
/*      */           }
/*      */         } catch (Throwable t) {
/*  345 */           IntraperiodBarsGenerator.LOGGER.error("Failed to load in progress bar " + t.getLocalizedMessage(), t);
/*  346 */           IntraperiodBarsGenerator.this.stopToFillInProgressPointAndFigure(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount);
/*      */         }
/*      */       }
/*      */     };
/*  350 */     invoke(runnable);
/*      */   }
/*      */ 
/*      */   private PointAndFigureData loadLastPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  359 */     PointAndFigureData lastBar = this.priceAggregationDataProvider.loadLastPointAndFigureData(instrument, offerSide, priceRange, reversalAmount);
/*  360 */     return lastBar;
/*      */   }
/*      */ 
/*      */   private void stopToFillInProgressPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  369 */     synchronized (this.inProgressPointAndFigureCreatorsMap) {
/*  370 */       this.inProgressPointAndFigureCreatorsMap.remove(instrument, offerSide, priceRange, reversalAmount);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startToFillInProgressTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  380 */     synchronized (this.inProgressTickBarCreatorsMap) {
/*  381 */       ITickBarCreator creator = (ITickBarCreator)this.inProgressTickBarCreatorsMap.get(instrument, offerSide, tickBarSize);
/*  382 */       if (creator == null)
/*      */       {
/*  386 */         creator = new FlowTickBarCreator(instrument, tickBarSize, offerSide);
/*      */ 
/*  388 */         creator.addListener(new TickBarLiveFeedAdapter(instrument, offerSide, tickBarSize)
/*      */         {
/*      */           public void newPriceData(TickBarData bar) {
/*  391 */             IntraperiodBarsGenerator.this.fireInProgressTickBarFormed(this.val$instrument, this.val$offerSide, this.val$tickBarSize, (TickBarData)IntraperiodBarsGenerator.access$000(bar));
/*      */           }
/*      */         });
/*  395 */         this.inProgressTickBarCreatorsMap.put(instrument, offerSide, tickBarSize, creator);
/*      */ 
/*  397 */         asynchLoadLastTickBar(instrument, offerSide, tickBarSize, creator);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void asynchLoadLastTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarCreator creator)
/*      */   {
/*  408 */     Runnable runnable = new Object(instrument, offerSide, tickBarSize, creator)
/*      */     {
/*      */       public void run() {
/*      */         try {
/*  412 */           IntraperiodBarsGenerator.this.waitUntilTickArrives(this.val$instrument);
/*  413 */           boolean ticksExist = IntraperiodBarsGenerator.this.waitUntilTickArrives(this.val$instrument);
/*  414 */           if (!ticksExist) {
/*  415 */             throw new RuntimeException("There is no ticks for insrtument <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/*  418 */           TickBarData lastBar = IntraperiodBarsGenerator.this.loadLastTickBar(this.val$instrument, this.val$offerSide, this.val$tickBarSize);
/*      */ 
/*  420 */           if (lastBar == null) {
/*  421 */             throw new RuntimeException("Unable to load last tick bar for <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/*  424 */           synchronized (IntraperiodBarsGenerator.this.inProgressTickBarCreatorsMap) {
/*  425 */             this.val$creator.setupLastData(lastBar);
/*      */           }
/*      */         } catch (Throwable t) {
/*  428 */           IntraperiodBarsGenerator.LOGGER.error("Failed to load in progress bar " + t.getLocalizedMessage(), t);
/*  429 */           IntraperiodBarsGenerator.this.stopToFillInProgressTickBar(this.val$instrument, this.val$offerSide, this.val$tickBarSize);
/*      */         }
/*      */       }
/*      */     };
/*  433 */     invoke(runnable);
/*      */   }
/*      */ 
/*      */   private TickBarData loadLastTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  441 */     TickBarData lastBar = this.priceAggregationDataProvider.loadLastTickBarData(instrument, offerSide, tickBarSize);
/*  442 */     return lastBar;
/*      */   }
/*      */ 
/*      */   private void stopToFillInProgressTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  450 */     synchronized (this.inProgressTickBarCreatorsMap) {
/*  451 */       this.inProgressTickBarCreatorsMap.remove(instrument, offerSide, tickBarSize);
/*      */     }
/*      */   }
/*      */ 
/*      */   public PointAndFigureData getInProgressPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  462 */     IPointAndFigureCreator creator = getInProgressPointAndFigureCreator(instrument, offerSide, priceRange, reversalAmount);
/*  463 */     if (creator == null) {
/*  464 */       return null;
/*      */     }
/*  466 */     return (PointAndFigureData)clone(creator.getLastData());
/*      */   }
/*      */ 
/*      */   private IPointAndFigureCreator getInProgressPointAndFigureCreator(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  475 */     IPointAndFigureCreator creator = (IPointAndFigureCreator)this.inProgressPointAndFigureCreatorsMap.get(instrument, offerSide, priceRange, reversalAmount);
/*  476 */     return creator;
/*      */   }
/*      */ 
/*      */   public PriceRangeData getInProgressPriceRange(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  486 */     PriceRangeData result = null;
/*  487 */     IPriceRangeCreator creator = getInProgressPriceRangeCreator(instrument, offerSide, priceRange);
/*  488 */     if (creator != null) {
/*  489 */       result = (PriceRangeData)clone(creator.getLastData());
/*      */     }
/*  491 */     return result;
/*      */   }
/*      */ 
/*      */   private IPriceRangeCreator getInProgressPriceRangeCreator(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  499 */     return (IPriceRangeCreator)this.inProgressPriceRangeCreatorsMap.get(instrument, offerSide, priceRange);
/*      */   }
/*      */ 
/*      */   public TickBarData getInProgressTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  509 */     ITickBarCreator creator = getInProgressTickBarCreator(instrument, offerSide, tickBarSize);
/*  510 */     if (creator == null) {
/*  511 */       return null;
/*      */     }
/*  513 */     return (TickBarData)clone(creator.getLastData());
/*      */   }
/*      */ 
/*      */   private ITickBarCreator getInProgressTickBarCreator(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  521 */     ITickBarCreator creator = (ITickBarCreator)this.inProgressTickBarCreatorsMap.get(instrument, offerSide, tickBarSize);
/*  522 */     return creator;
/*      */   }
/*      */ 
/*      */   private static <T extends AbstractPriceAggregationData> T clone(T bar) {
/*  526 */     return bar == null ? null : bar.clone();
/*      */   }
/*      */ 
/*      */   private void invoke(Runnable runnable) {
/*  530 */     new Thread(runnable).start();
/*      */   }
/*      */ 
/*      */   public boolean isInProgressPriceRangeLoadingNow(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  539 */     IPriceRangeCreator creator = getInProgressPriceRangeCreator(instrument, offerSide, priceRange);
/*  540 */     return isInProgressBarLoadingNow(creator);
/*      */   }
/*      */ 
/*      */   public boolean isInProgressTickBarLoadingNow(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  549 */     ITickBarCreator creator = getInProgressTickBarCreator(instrument, offerSide, tickBarSize);
/*  550 */     return isInProgressBarLoadingNow(creator);
/*      */   }
/*      */ 
/*      */   public boolean isInProgressPointAndFigureLoadingNow(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  560 */     IPointAndFigureCreator creator = getInProgressPointAndFigureCreator(instrument, offerSide, priceRange, reversalAmount);
/*  561 */     return isInProgressBarLoadingNow(creator);
/*      */   }
/*      */ 
/*      */   private boolean isInProgressBarLoadingNow(IPriceAggregationCreator<?, ?, ?> creator)
/*      */   {
/*  569 */     return (creator != null) && (creator.getLastData() == null);
/*      */   }
/*      */ 
/*      */   private void fireInProgressTickBarFormed(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, TickBarData bar)
/*      */   {
/*  583 */     List listeners = (List)this.tickBarNotificationListenersMap.get(instrument, offerSide, tickBarSize);
/*  584 */     if (listeners != null)
/*  585 */       for (ITickBarLiveFeedListener listener : listeners)
/*  586 */         listener.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   private void fireInProgressTickBarUpdated(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, TickBarData bar)
/*      */   {
/*  597 */     List listeners = (List)this.inProgressTickBarLiveFeedListenersMap.get(instrument, offerSide, tickBarSize);
/*  598 */     if (listeners != null)
/*  599 */       for (ITickBarLiveFeedListener listener : listeners)
/*  600 */         listener.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   private void fireInProgressPointAndFigureFormed(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, PointAndFigureData bar)
/*      */   {
/*  612 */     List listeners = (List)this.pointAndFigureNotificationListenersMap.get(instrument, offerSide, priceRange, reversalAmount);
/*  613 */     if (listeners != null)
/*  614 */       for (IPointAndFigureLiveFeedListener listener : listeners)
/*  615 */         listener.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   private void fireInProgressPointAndFigureUpdated(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, PointAndFigureData bar)
/*      */   {
/*  627 */     List listeners = (List)this.inProgressPointAndFigureListenersMap.get(instrument, offerSide, priceRange, reversalAmount);
/*  628 */     if (listeners != null)
/*  629 */       for (IPointAndFigureLiveFeedListener listener : listeners)
/*  630 */         listener.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   private void fireInProgressPriceRangeUpdated(Instrument instrument, OfferSide offerSide, PriceRange priceRange, PriceRangeData bar)
/*      */   {
/*  641 */     List listeners = (List)this.inProgressPriceRangeLiveFeedListenersMap.get(instrument, offerSide, priceRange);
/*  642 */     if (listeners != null)
/*  643 */       for (IPriceRangeLiveFeedListener listener : listeners)
/*  644 */         listener.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   private void fireInProgressPriceRangeFormed(Instrument instrument, OfferSide offerSide, PriceRange priceRange, PriceRangeData bar)
/*      */   {
/*  655 */     List listeners = (List)this.priceRangeNotificationListenersMap.get(instrument, offerSide, priceRange);
/*  656 */     if (listeners != null)
/*  657 */       for (IPriceRangeLiveFeedListener listener : listeners)
/*  658 */         listener.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   public void addInProgressPriceRangeListener(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IPriceRangeLiveFeedListener listener)
/*      */   {
/*  671 */     List listeners = (List)this.inProgressPriceRangeLiveFeedListenersMap.get(instrument, offerSide, priceRange);
/*  672 */     if (listeners == null) {
/*  673 */       listeners = new ArrayList();
/*  674 */       this.inProgressPriceRangeLiveFeedListenersMap.put(instrument, offerSide, priceRange, listeners);
/*      */     }
/*  676 */     listeners.add(listener);
/*  677 */     startToFillInProgressPriceRange(instrument, offerSide, priceRange);
/*      */   }
/*      */ 
/*      */   public void removeInProgressPriceRangeListener(IPriceRangeLiveFeedListener listener)
/*      */   {
/*  682 */     removePriceRangeLiveFeedListener(this.inProgressPriceRangeLiveFeedListenersMap, listener);
/*      */   }
/*      */ 
/*      */   public void addPriceRangeNotificationListener(Instrument instrument, OfferSide offerSide, PriceRange priceRange, IPriceRangeLiveFeedListener listener)
/*      */   {
/*  692 */     List listeners = (List)this.priceRangeNotificationListenersMap.get(instrument, offerSide, priceRange);
/*  693 */     if (listeners == null) {
/*  694 */       listeners = new ArrayList();
/*  695 */       this.priceRangeNotificationListenersMap.put(instrument, offerSide, priceRange, listeners);
/*      */     }
/*  697 */     listeners.add(listener);
/*  698 */     startToFillInProgressPriceRange(instrument, offerSide, priceRange);
/*      */   }
/*      */ 
/*      */   public void removePriceRangeNotificationListener(IPriceRangeLiveFeedListener listener)
/*      */   {
/*  703 */     removePriceRangeLiveFeedListener(this.priceRangeNotificationListenersMap, listener);
/*      */   }
/*      */ 
/*      */   public void addInProgressPointAndFigureListener(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, IPointAndFigureLiveFeedListener listener)
/*      */   {
/*  715 */     List listeners = (List)this.inProgressPointAndFigureListenersMap.get(instrument, offerSide, priceRange, reversalAmount);
/*  716 */     if (listeners == null) {
/*  717 */       listeners = new ArrayList();
/*  718 */       this.inProgressPointAndFigureListenersMap.put(instrument, offerSide, priceRange, reversalAmount, listeners);
/*      */     }
/*  720 */     listeners.add(listener);
/*  721 */     startToFillInProgressPointAndFigure(instrument, offerSide, priceRange, reversalAmount);
/*      */   }
/*      */ 
/*      */   public void removeInProgressPointAndFigureListener(IPointAndFigureLiveFeedListener listener)
/*      */   {
/*  726 */     removePointAndFigureLiveFeedListener(this.inProgressPointAndFigureListenersMap, listener);
/*      */   }
/*      */ 
/*      */   public void addPointAndFigureNotificationListener(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, IPointAndFigureLiveFeedListener listener)
/*      */   {
/*  737 */     List listeners = (List)this.pointAndFigureNotificationListenersMap.get(instrument, offerSide, priceRange, reversalAmount);
/*  738 */     if (listeners == null) {
/*  739 */       listeners = new ArrayList();
/*  740 */       this.pointAndFigureNotificationListenersMap.put(instrument, offerSide, priceRange, reversalAmount, listeners);
/*      */     }
/*  742 */     listeners.add(listener);
/*  743 */     startToFillInProgressPointAndFigure(instrument, offerSide, priceRange, reversalAmount);
/*      */   }
/*      */ 
/*      */   public void removePointAndFigureNotificationListener(IPointAndFigureLiveFeedListener listener)
/*      */   {
/*  748 */     removePointAndFigureLiveFeedListener(this.pointAndFigureNotificationListenersMap, listener);
/*      */   }
/*      */ 
/*      */   public void addInProgressTickBarListener(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarLiveFeedListener listener)
/*      */   {
/*  759 */     List listeners = (List)this.inProgressTickBarLiveFeedListenersMap.get(instrument, offerSide, tickBarSize);
/*  760 */     if (listeners == null) {
/*  761 */       listeners = new ArrayList();
/*  762 */       this.inProgressTickBarLiveFeedListenersMap.put(instrument, offerSide, tickBarSize, listeners);
/*      */     }
/*  764 */     listeners.add(listener);
/*  765 */     startToFillInProgressTickBar(instrument, offerSide, tickBarSize);
/*      */   }
/*      */ 
/*      */   public void removeInProgressTickBarListener(ITickBarLiveFeedListener listener)
/*      */   {
/*  770 */     removeTickBarLiveFeedListener(this.inProgressTickBarLiveFeedListenersMap, listener);
/*      */   }
/*      */ 
/*      */   public void addTickBarNotificationListener(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBarLiveFeedListener listener)
/*      */   {
/*  780 */     List listeners = (List)this.tickBarNotificationListenersMap.get(instrument, offerSide, tickBarSize);
/*  781 */     if (listeners == null) {
/*  782 */       listeners = new ArrayList();
/*  783 */       this.tickBarNotificationListenersMap.put(instrument, offerSide, tickBarSize, listeners);
/*      */     }
/*  785 */     listeners.add(listener);
/*  786 */     startToFillInProgressTickBar(instrument, offerSide, tickBarSize);
/*      */   }
/*      */ 
/*      */   public void removeTickBarNotificationListener(ITickBarLiveFeedListener listener)
/*      */   {
/*  791 */     removeTickBarLiveFeedListener(this.tickBarNotificationListenersMap, listener);
/*      */   }
/*      */ 
/*      */   private void removePriceRangeLiveFeedListener(ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IPriceRangeLiveFeedListener>> map, IPriceRangeLiveFeedListener listener)
/*      */   {
/*  799 */     if (listener == null) {
/*  800 */       return;
/*      */     }
/*      */ 
/*  803 */     Set entrySet = map.entrySet();
/*  804 */     for (IThreeKeyEntry entry : entrySet) {
/*  805 */       List list = (List)entry.getValue();
/*  806 */       if (list == null) {
/*      */         continue;
/*      */       }
/*  809 */       if (list.contains(listener)) {
/*  810 */         list.remove(listener);
/*      */ 
/*  813 */         if (list.isEmpty())
/*      */         {
/*  817 */           stopToFillInProgressPriceRange((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (PriceRange)entry.getKey3());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void removePointAndFigureLiveFeedListener(ISynchronizedFourKeyMap<Instrument, OfferSide, PriceRange, ReversalAmount, List<IPointAndFigureLiveFeedListener>> map, IPointAndFigureLiveFeedListener listener)
/*      */   {
/*  827 */     if (listener == null) {
/*  828 */       return;
/*      */     }
/*      */ 
/*  831 */     Set entrySet = map.entrySet();
/*  832 */     for (IFourKeyEntry entry : entrySet) {
/*  833 */       List list = (List)entry.getValue();
/*      */ 
/*  835 */       if (list == null) {
/*      */         continue;
/*      */       }
/*  838 */       if (list.contains(listener)) {
/*  839 */         list.remove(listener);
/*      */ 
/*  842 */         if (list.isEmpty())
/*      */         {
/*  846 */           stopToFillInProgressPointAndFigure((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (PriceRange)entry.getKey3(), (ReversalAmount)entry.getKey4());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void removeTickBarLiveFeedListener(ISynchronizedThreeKeyMap<Instrument, OfferSide, TickBarSize, List<ITickBarLiveFeedListener>> map, ITickBarLiveFeedListener listener)
/*      */   {
/*  857 */     if (listener == null) {
/*  858 */       return;
/*      */     }
/*      */ 
/*  861 */     Set entrySet = map.entrySet();
/*  862 */     for (IThreeKeyEntry entry : entrySet) {
/*  863 */       List list = (List)entry.getValue();
/*  864 */       if (list == null) {
/*      */         continue;
/*      */       }
/*  867 */       if (list.contains(listener)) {
/*  868 */         list.remove(listener);
/*      */ 
/*  871 */         if (list.isEmpty())
/*      */         {
/*  875 */           stopToFillInProgressTickBar((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (TickBarSize)entry.getKey3());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public PointAndFigureData getOrLoadInProgressPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/*  884 */     PointAndFigureData bar = getInProgressPointAndFigure(instrument, offerSide, priceRange, reversalAmount);
/*  885 */     if (bar == null) {
/*  886 */       bar = loadLastPointAndFigure(instrument, offerSide, priceRange, reversalAmount);
/*      */     }
/*  888 */     return bar;
/*      */   }
/*      */ 
/*      */   public PriceRangeData getOrLoadInProgressPriceRange(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*      */   {
/*  893 */     PriceRangeData bar = getInProgressPriceRange(instrument, offerSide, priceRange);
/*  894 */     if (bar == null) {
/*  895 */       bar = loadLastPriceRange(instrument, offerSide, priceRange);
/*      */     }
/*  897 */     return bar;
/*      */   }
/*      */ 
/*      */   public TickBarData getOrLoadInProgressTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*      */   {
/*  902 */     TickBarData bar = getInProgressTickBar(instrument, offerSide, tickBarSize);
/*  903 */     if (bar == null) {
/*  904 */       bar = loadLastTickBar(instrument, offerSide, tickBarSize);
/*      */     }
/*  906 */     return bar;
/*      */   }
/*      */ 
/*      */   public void addInProgressRenkoListener(Instrument instrument, OfferSide offerSide, PriceRange brickSize, IRenkoLiveFeedListener listener)
/*      */   {
/*  916 */     List listeners = (List)this.inProgressRenkoLiveFeedListenersMap.get(instrument, offerSide, brickSize);
/*  917 */     if (listeners == null) {
/*  918 */       listeners = new ArrayList();
/*  919 */       this.inProgressRenkoLiveFeedListenersMap.put(instrument, offerSide, brickSize, listeners);
/*      */     }
/*  921 */     listeners.add(listener);
/*  922 */     startToFillInProgressRenko(instrument, offerSide, brickSize);
/*      */   }
/*      */ 
/*      */   public void addRenkoNotificationListener(Instrument instrument, OfferSide offerSide, PriceRange brickSize, IRenkoLiveFeedListener listener)
/*      */   {
/*  932 */     List listeners = (List)this.renkoNotificationListenersMap.get(instrument, offerSide, brickSize);
/*  933 */     if (listeners == null) {
/*  934 */       listeners = new ArrayList();
/*  935 */       this.renkoNotificationListenersMap.put(instrument, offerSide, brickSize, listeners);
/*      */     }
/*  937 */     listeners.add(listener);
/*  938 */     startToFillInProgressRenko(instrument, offerSide, brickSize);
/*      */   }
/*      */ 
/*      */   public RenkoData getOrLoadInProgressRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/*  947 */     RenkoData bar = getInProgressRenko(instrument, offerSide, brickSize);
/*  948 */     if (bar == null) {
/*  949 */       bar = loadLastRenko(instrument, offerSide, brickSize);
/*      */     }
/*  951 */     return bar;
/*      */   }
/*      */ 
/*      */   private RenkoData loadLastRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/*  959 */     RenkoData result = this.priceAggregationDataProvider.loadLastRenko(instrument, offerSide, brickSize);
/*  960 */     return result;
/*      */   }
/*      */ 
/*      */   public void removeInProgressRenkoListener(IRenkoLiveFeedListener listener)
/*      */   {
/*  965 */     removeRenkoLiveFeedListener(this.inProgressRenkoLiveFeedListenersMap, listener);
/*      */   }
/*      */ 
/*      */   public void removeRenkoNotificationListener(IRenkoLiveFeedListener listener)
/*      */   {
/*  970 */     removeRenkoLiveFeedListener(this.renkoNotificationListenersMap, listener);
/*      */   }
/*      */ 
/*      */   private void removeRenkoLiveFeedListener(ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IRenkoLiveFeedListener>> map, IRenkoLiveFeedListener listener)
/*      */   {
/*  978 */     if (listener == null) {
/*  979 */       return;
/*      */     }
/*      */ 
/*  982 */     Set entrySet = map.entrySet();
/*  983 */     for (IThreeKeyEntry entry : entrySet) {
/*  984 */       List list = (List)entry.getValue();
/*  985 */       if (list == null) {
/*      */         continue;
/*      */       }
/*  988 */       if (list.contains(listener)) {
/*  989 */         list.remove(listener);
/*      */ 
/*  992 */         if (list.isEmpty())
/*      */         {
/*  996 */           stopToFillInProgressRenko((Instrument)entry.getKey1(), (OfferSide)entry.getKey2(), (PriceRange)entry.getKey3());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public RenkoData getInProgressRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/* 1008 */     RenkoData result = null;
/* 1009 */     IRenkoCreator creator = getInProgressRenkoCreator(instrument, offerSide, brickSize);
/* 1010 */     if (creator != null) {
/* 1011 */       result = (RenkoData)clone(creator.getLastData());
/*      */     }
/* 1013 */     return result;
/*      */   }
/*      */ 
/*      */   private IRenkoCreator getInProgressRenkoCreator(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/* 1021 */     return (IRenkoCreator)this.inProgressRenkoCreatorsMap.get(instrument, offerSide, brickSize);
/*      */   }
/*      */ 
/*      */   public boolean isInProgressRenkoLoadingNow(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/* 1030 */     IRenkoCreator creator = getInProgressRenkoCreator(instrument, offerSide, brickSize);
/* 1031 */     return isInProgressBarLoadingNow(creator);
/*      */   }
/*      */ 
/*      */   public void startToFillInProgressRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/* 1040 */     synchronized (this.inProgressRenkoCreatorsMap) {
/* 1041 */       IRenkoCreator creator = (IRenkoCreator)this.inProgressRenkoCreatorsMap.get(instrument, offerSide, brickSize);
/* 1042 */       if (creator == null)
/*      */       {
/* 1046 */         creator = new FlowRenkoCreator(instrument, offerSide, brickSize);
/*      */ 
/* 1048 */         creator.addListener(new IRenkoLiveFeedListener(instrument, offerSide, brickSize)
/*      */         {
/*      */           public void newPriceData(RenkoData bar) {
/* 1051 */             IntraperiodBarsGenerator.this.fireInProgressRenkoFormed(this.val$instrument, this.val$offerSide, this.val$brickSize, (RenkoData)IntraperiodBarsGenerator.access$000(bar));
/*      */           }
/*      */         });
/* 1055 */         this.inProgressRenkoCreatorsMap.put(instrument, offerSide, brickSize, creator);
/*      */ 
/* 1057 */         asynchLoadLastRenko(instrument, offerSide, brickSize, creator);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void asynchLoadLastRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize, IRenkoCreator creator)
/*      */   {
/* 1068 */     Runnable runnable = new Object(instrument, offerSide, brickSize, creator)
/*      */     {
/*      */       public void run() {
/*      */         try {
/* 1072 */           IntraperiodBarsGenerator.this.waitUntilTickArrives(this.val$instrument);
/* 1073 */           boolean ticksExist = IntraperiodBarsGenerator.this.waitUntilTickArrives(this.val$instrument);
/* 1074 */           if (!ticksExist) {
/* 1075 */             throw new RuntimeException("There is no ticks for insrtument <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/* 1078 */           RenkoData lastBar = IntraperiodBarsGenerator.this.loadLastRenko(this.val$instrument, this.val$offerSide, this.val$brickSize);
/*      */ 
/* 1080 */           if (lastBar == null) {
/* 1081 */             throw new RuntimeException("Unable to load last renko for <" + this.val$instrument + ">");
/*      */           }
/*      */ 
/* 1084 */           synchronized (IntraperiodBarsGenerator.this.inProgressRenkoCreatorsMap) {
/* 1085 */             this.val$creator.setupLastData(lastBar);
/*      */           }
/*      */         } catch (Throwable t) {
/* 1088 */           IntraperiodBarsGenerator.LOGGER.error("Failed to load in progress bar " + t.getLocalizedMessage(), t);
/* 1089 */           IntraperiodBarsGenerator.this.stopToFillInProgressRenko(this.val$instrument, this.val$offerSide, this.val$brickSize);
/*      */         }
/*      */       }
/*      */     };
/* 1093 */     invoke(runnable);
/*      */   }
/*      */ 
/*      */   private void stopToFillInProgressRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*      */   {
/* 1101 */     synchronized (this.inProgressRenkoCreatorsMap) {
/* 1102 */       this.inProgressRenkoCreatorsMap.remove(instrument, offerSide, brickSize);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fireInProgressRenkoUpdated(Instrument instrument, OfferSide offerSide, PriceRange brickSize, RenkoData bar)
/*      */   {
/* 1112 */     fireRenko(instrument, offerSide, brickSize, bar, this.inProgressRenkoLiveFeedListenersMap);
/*      */   }
/*      */ 
/*      */   private void fireInProgressRenkoFormed(Instrument instrument, OfferSide offerSide, PriceRange brickSize, RenkoData bar)
/*      */   {
/* 1121 */     fireRenko(instrument, offerSide, brickSize, bar, this.renkoNotificationListenersMap);
/*      */   }
/*      */ 
/*      */   private void fireRenko(Instrument instrument, OfferSide offerSide, PriceRange brickSize, RenkoData bar, ISynchronizedThreeKeyMap<Instrument, OfferSide, PriceRange, List<IRenkoLiveFeedListener>> map)
/*      */   {
/* 1131 */     List listeners = (List)map.get(instrument, offerSide, brickSize);
/* 1132 */     if (listeners != null)
/* 1133 */       for (IRenkoLiveFeedListener l : listeners)
/* 1134 */         l.newPriceData(bar);
/*      */   }
/*      */ 
/*      */   private boolean waitUntilTickArrives(Instrument instrument)
/*      */   {
/* 1144 */     int count = 1000;
/* 1145 */     while ((this.priceAggregationDataProvider.getFeedDataProvider().getLastTickTime(instrument) <= 0L) && (count > 0)) {
/*      */       try {
/* 1147 */         Thread.sleep(100L);
/*      */       } catch (InterruptedException e) {
/* 1149 */         LOGGER.error(e.getLocalizedMessage(), e);
/*      */       }
/* 1151 */       count--;
/*      */     }
/* 1153 */     return this.priceAggregationDataProvider.getFeedDataProvider().getLastTickTime(instrument) > 0L;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   57 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.intraperiod.IntraperiodBarsGenerator
 * JD-Core Version:    0.6.0
 */