/*      */ package com.dukascopy.charts.data.datacache;
/*      */ 
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.ReversalAmount;
/*      */ import com.dukascopy.api.TickBarSize;
/*      */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*      */ import com.dukascopy.charts.data.datacache.dhl.DailyHighLowManager;
/*      */ import com.dukascopy.charts.data.datacache.dhl.IDailyHighLowManager;
/*      */ import com.dukascopy.charts.data.datacache.feed.FeedCommissionManager;
/*      */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*      */ import com.dukascopy.charts.data.datacache.filtering.FilterManager;
/*      */ import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
/*      */ import com.dukascopy.charts.data.datacache.firsttimes.FeedDataHistoryFirstTimesManager;
/*      */ import com.dukascopy.charts.data.datacache.firsttimes.IFeedDataHistoryFirstTimesManager;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*      */ import com.dukascopy.charts.data.datacache.listener.DataFeedServerConnectionListener;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.thread.BackgroundFeedLoadingThread;
/*      */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*      */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.util.IOrderUtils;
/*      */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import com.dukascopy.transport.util.Hex;
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileLock;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.ArrayBlockingQueue;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.ExecutorService;
/*      */ import java.util.concurrent.RejectedExecutionHandler;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import javax.crypto.KeyGenerator;
/*      */ import javax.crypto.SecretKey;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class FeedDataProvider
/*      */   implements IFeedDataProvider, TickListener, OrdersListener
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   public static final int WEEKENDS_CACHE_SIZE = 40;
/*      */   private static FeedDataProvider feedDataProvider;
/*      */   protected static final SimpleDateFormat DATE_FORMAT;
/*      */   private final ExecutorService actionsExecutorService;
/*   86 */   private final List<Runnable> currentlyRunningTasks = new ArrayList();
/*   87 */   private final List<LiveFeedListener>[] tickListeners = new List[Instrument.values().length];
/*   88 */   private final Map<Instrument, Map<Period, Map<com.dukascopy.api.OfferSide, List<LiveFeedListener>>>> periodListenersMap = new HashMap();
/*   89 */   private final List<LiveFeedListener>[][] allPeriodListeners = new List[Instrument.values().length][2];
/*   90 */   private final List<LiveCandleListener> allCandlePeriodListener = Collections.synchronizedList(new ArrayList());
/*   91 */   private final Map<Instrument, Map<Period, Map<com.dukascopy.api.OfferSide, List<LiveFeedListener>>>> inProgressCandleListenersMap = new HashMap();
/*   92 */   private final List<CacheDataUpdatedListener>[] cacheDataChangeListeners = new List[Instrument.values().length];
/*   93 */   protected final double[] lastAsks = new double[Instrument.values().length];
/*   94 */   protected final double[] lastBids = new double[Instrument.values().length];
/*   95 */   protected final TickData[] lastTicks = new TickData[Instrument.values().length];
/*   96 */   protected final long[] currentTimes = new long[Instrument.values().length];
/*   97 */   protected volatile long currentTime = -9223372036854775808L;
/*   98 */   protected volatile long firstTickLocalTime = -9223372036854775808L;
/*      */   private final CurvesDataLoader curvesDataLoader;
/*      */   protected LocalCacheManager localCacheManager;
/*      */   private static ICurvesProtocolHandler curvesProtocolHandler;
/*      */   private static String accountId;
/*      */   protected IntraperiodCandlesGenerator intraperiodCandlesGenerator;
/*      */   private final IOrdersProvider ordersProvider;
/*      */   private final IFeedCommissionManager feedCommissionsManager;
/*  108 */   private final List<Instrument> subscribedInstruments = new ArrayList();
/*  109 */   private final List<InstrumentSubscriptionListener> instrumentSubscriptionListeners = Collections.synchronizedList(new ArrayList());
/*  110 */   protected CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy = CurvesDataLoader.IntraperiodExistsPolicy.DOWNLOAD_CHUNK_IN_BACKGROUND;
/*      */ 
/*  112 */   private final int[] networkLatency = new int[15];
/*  113 */   private int latencyIndex = 0;
/*      */   private static String platformTicket;
/*      */   private static String encryptionKey;
/*  118 */   private volatile boolean connected = true;
/*      */   private volatile long disconnectTime;
/*      */   private volatile boolean stopped;
/*      */   private BackgroundFeedLoadingThread backgroundFeedLoadingThread;
/*      */   private IFilterManager filterManager;
/*      */   private IPriceAggregationDataProvider priceAggregationDataProvider;
/*      */   private static IFeedDataHistoryFirstTimesManager feedDataHistoryFirstTimesManager;
/*      */   private IDailyHighLowManager dailyHighLowManager;
/*      */ 
/*      */   protected FeedDataProvider(String cacheName, boolean disableFlatsGenerationByTimeout, IOrdersProvider ordersProvider, List<String[]> feedCommissions)
/*      */     throws DataCacheException
/*      */   {
/*  136 */     ThreadFactory threadFactory = new FeedThreadFactory();
/*      */ 
/*  139 */     this.actionsExecutorService = new FeedExecutor(5, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(15 + Instrument.values().length * 3, false), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy(), this.currentlyRunningTasks);
/*      */ 
/*  143 */     for (int i = 0; i < this.tickListeners.length; i++) {
/*  144 */       this.tickListeners[i] = Collections.synchronizedList(new ArrayList());
/*      */     }
/*  146 */     for (int i = 0; i < this.cacheDataChangeListeners.length; i++) {
/*  147 */       this.cacheDataChangeListeners[i] = Collections.synchronizedList(new ArrayList());
/*      */     }
/*  149 */     for (int i = 0; i < this.currentTimes.length; i++) {
/*  150 */       this.currentTimes[i] = -9223372036854775808L;
/*      */     }
/*  152 */     for (int i = 0; i < this.lastAsks.length; i++) {
/*  153 */       this.lastAsks[i] = (0.0D / 0.0D);
/*  154 */       this.lastBids[i] = (0.0D / 0.0D);
/*      */     }
/*      */ 
/*  157 */     this.curvesDataLoader = new CurvesDataLoader(this);
/*  158 */     this.localCacheManager = new LocalCacheManager(cacheName, cacheName.equals("LIVE"));
/*  159 */     this.instrumentSubscriptionListeners.add(this.localCacheManager);
/*  160 */     this.ordersProvider = ordersProvider;
/*  161 */     this.feedCommissionsManager = new FeedCommissionManager(feedCommissions);
/*      */ 
/*  163 */     this.priceAggregationDataProvider = new PriceAggregationDataProvider(this);
/*  164 */     this.intraperiodCandlesGenerator = new IntraperiodCandlesGenerator(disableFlatsGenerationByTimeout, this);
/*  165 */     this.dailyHighLowManager = new DailyHighLowManager(this);
/*      */   }
/*      */ 
/*      */   public static FeedDataProvider getDefaultInstance()
/*      */   {
/*  175 */     return feedDataProvider;
/*      */   }
/*      */ 
/*      */   public static void createFeedDataProvider(String cacheName)
/*      */     throws DataCacheException
/*      */   {
/*  185 */     createFeedDataProvider(cacheName, null, null);
/*      */   }
/*      */ 
/*      */   public static void createFeedDataProvider(String cacheName, List<String[]> feedCommissions, Set<String> supportedInstruments)
/*      */     throws DataCacheException
/*      */   {
/*  193 */     createFeedDataProvider(cacheName, null, feedCommissions, supportedInstruments);
/*      */   }
/*      */ 
/*      */   private static void createFeedDataProvider(String cacheName, ICurvesProtocolHandler curvesProtocolHandler, List<String[]> feedCommissions, Set<String> supportedInstruments)
/*      */     throws DataCacheException
/*      */   {
/*  202 */     if (feedDataProvider == null) {
/*  203 */       OrdersProvider ordersProvider = OrdersProvider.getInstance();
/*  204 */       feedDataProvider = new FeedDataProvider(cacheName, false, ordersProvider, feedCommissions);
/*  205 */       if (curvesProtocolHandler == null)
/*  206 */         curvesProtocolHandler = new CurvesJsonProtocolHandler();
/*      */       else {
/*  208 */         curvesProtocolHandler = curvesProtocolHandler;
/*      */       }
/*      */ 
/*  211 */       feedDataHistoryFirstTimesManager = new FeedDataHistoryFirstTimesManager(curvesProtocolHandler, supportedInstruments);
/*      */ 
/*  217 */       for (Instrument instrument : Instrument.values())
/*  218 */         ordersProvider.addOrdersListener(instrument, feedDataProvider);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void connectToHistoryServer(Collection<String> authServerUrls, String userName, String instanceId, String historyServerUrl, String encryptionKey, String version)
/*      */   {
/*  231 */     connectToHistoryServer(new FeedAuthenticator(authServerUrls, version, userName, instanceId, null), userName, instanceId, historyServerUrl, encryptionKey);
/*      */   }
/*      */ 
/*      */   public void connectToHistoryServer(IAuthenticator authenticator, String userName, String instanceId, String historyServerUrl, String encryptionKey)
/*      */   {
/*  241 */     if (!ObjectUtils.isEqual(accountId, userName)) {
/*  242 */       accountId = userName;
/*  243 */       if (this.ordersProvider != null) {
/*  244 */         this.ordersProvider.clear();
/*      */       }
/*      */     }
/*  247 */     if (encryptionKey == null) {
/*  248 */       encryptionKey = encryptionKey;
/*      */     }
/*  250 */     curvesProtocolHandler.connect(authenticator, userName, instanceId, historyServerUrl);
/*      */   }
/*      */ 
/*      */   public IFeedCommissionManager getFeedCommissionManager()
/*      */   {
/*  255 */     return this.feedCommissionsManager;
/*      */   }
/*      */ 
/*      */   public static void setPlatformTicket(String platformTicket) {
/*  259 */     platformTicket = platformTicket;
/*      */   }
/*      */ 
/*      */   public void subscribeToLiveFeed(Instrument instrument, LiveFeedListener listener)
/*      */   {
/*  264 */     LOGGER.trace(new StringBuilder().append("Subscribing one more listener to instrument [").append(instrument).append("]").toString());
/*  265 */     this.tickListeners[instrument.ordinal()].add(listener);
/*      */   }
/*      */ 
/*      */   public void unsubscribeFromLiveFeed(Instrument instrument, LiveFeedListener listener)
/*      */   {
/*  271 */     LOGGER.trace(new StringBuilder().append("Unsubscribing listener from instrument [").append(instrument).append("]").toString());
/*  272 */     this.tickListeners[instrument.ordinal()].remove(listener);
/*      */   }
/*      */ 
/*      */   private Map<com.dukascopy.api.OfferSide, List<LiveFeedListener>> getOfferSideMap(Map<Instrument, Map<Period, Map<com.dukascopy.api.OfferSide, List<LiveFeedListener>>>> map, Instrument instrument, Period period)
/*      */   {
/*  280 */     if (!map.containsKey(instrument)) {
/*  281 */       map.put(instrument, new HashMap());
/*      */     }
/*  283 */     Map periodMap = (Map)map.get(instrument);
/*  284 */     if (!periodMap.containsKey(period)) {
/*  285 */       periodMap.put(period, new HashMap());
/*      */     }
/*  287 */     return (Map)periodMap.get(period);
/*      */   }
/*      */ 
/*      */   private List<LiveFeedListener> getPeriodListeners(Map<Instrument, Map<Period, Map<com.dukascopy.api.OfferSide, List<LiveFeedListener>>>> map, Instrument instrument, Period period, com.dukascopy.api.OfferSide offerSide)
/*      */   {
/*  296 */     Map offerSideMap = getOfferSideMap(map, instrument, period);
/*  297 */     return (List)offerSideMap.get(offerSide);
/*      */   }
/*      */ 
/*      */   private void putPeriodListeners(Map<Instrument, Map<Period, Map<com.dukascopy.api.OfferSide, List<LiveFeedListener>>>> map, Instrument instrument, Period period, com.dukascopy.api.OfferSide offerSide, List<LiveFeedListener> listeners)
/*      */   {
/*  307 */     Map offerSideMap = getOfferSideMap(map, instrument, period);
/*  308 */     offerSideMap.put(offerSide, listeners);
/*      */   }
/*      */ 
/*      */   public void subscribeToPeriodNotifications(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, LiveFeedListener listener)
/*      */   {
/*  313 */     LOGGER.trace(new StringBuilder().append("Subscribing one more listener to instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("]").toString());
/*      */ 
/*  317 */     List listenersList = getPeriodListeners(this.periodListenersMap, instrument, period, side);
/*  318 */     if (listenersList == null) {
/*  319 */       listenersList = Collections.synchronizedList(new ArrayList());
/*  320 */       putPeriodListeners(this.periodListenersMap, instrument, period, side, listenersList);
/*      */     }
/*  322 */     listenersList.add(listener);
/*      */   }
/*      */ 
/*      */   public void unsubscribeFromPeriodNotifications(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, LiveFeedListener listener)
/*      */   {
/*  327 */     LOGGER.trace(new StringBuilder().append("Unsubscribing listener from instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("]").toString());
/*  328 */     List listenersList = getPeriodListeners(this.periodListenersMap, instrument, period, side);
/*  329 */     if (listenersList != null)
/*  330 */       listenersList.remove(listener);
/*      */   }
/*      */ 
/*      */   public void subscribeToAllPeriodNotifications(Instrument instrument, com.dukascopy.api.OfferSide side, LiveFeedListener listener)
/*      */   {
/*  336 */     LOGGER.trace(new StringBuilder().append("Subscribing one more listener to instrument [").append(instrument).append("], all periods, side [").append(side).append("]").toString());
/*  337 */     List listenersList = this.allPeriodListeners[instrument.ordinal()][1];
/*  338 */     if (listenersList == null) {
/*  339 */       listenersList = Collections.synchronizedList(new ArrayList());
/*  340 */       this.allPeriodListeners[instrument.ordinal()][(side == com.dukascopy.api.OfferSide.ASK ? 0 : 1)] = listenersList;
/*      */     }
/*  342 */     listenersList.add(listener);
/*      */   }
/*      */ 
/*      */   public void unsubscribeFromAllPeriodNotifications(Instrument instrument, com.dukascopy.api.OfferSide side, LiveFeedListener listener)
/*      */   {
/*  347 */     LOGGER.trace(new StringBuilder().append("Unsubscribing listener from instrument [").append(instrument).append("], all periods, side [").append(side).append("]").toString());
/*  348 */     List listenersList = this.allPeriodListeners[instrument.ordinal()][1];
/*  349 */     if (listenersList != null)
/*  350 */       listenersList.remove(listener);
/*      */   }
/*      */ 
/*      */   public void subscribeToAllCandlePeriods(LiveCandleListener listener)
/*      */   {
/*  356 */     this.allCandlePeriodListener.add(listener);
/*      */   }
/*      */ 
/*      */   public void unsubscribeFromAllCandlePeriods(LiveCandleListener listener)
/*      */   {
/*  361 */     this.allCandlePeriodListener.remove(listener);
/*      */   }
/*      */ 
/*      */   public void addInProgressCandleListener(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, LiveFeedListener listener)
/*      */   {
/*  366 */     LOGGER.trace(new StringBuilder().append("Subscribing one more inProgressCandle listener to instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("]").toString());
/*  367 */     if (listener == null) {
/*  368 */       throw new NullPointerException("Listener for in-progress candle is null");
/*      */     }
/*  370 */     List listenersList = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, side);
/*  371 */     if (listenersList == null) {
/*  372 */       listenersList = Collections.synchronizedList(new ArrayList());
/*  373 */       putPeriodListeners(this.inProgressCandleListenersMap, instrument, period, side, listenersList);
/*      */     }
/*  375 */     listenersList.add(listener);
/*      */ 
/*  377 */     this.intraperiodCandlesGenerator.inProgressCandleListenerAdded(instrument, period, side, listener);
/*      */   }
/*      */ 
/*      */   public void removeInProgressCandleListener(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, LiveFeedListener listener)
/*      */   {
/*  382 */     LOGGER.trace(new StringBuilder().append("Unsubscribing inProgressCandle listener from instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("]").toString());
/*  383 */     List listenersList = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, side);
/*  384 */     if (listenersList != null) {
/*  385 */       listenersList.remove(listener);
/*      */     }
/*  387 */     this.intraperiodCandlesGenerator.inProgressCandleListenerRemoved(instrument, period, side, listener);
/*      */   }
/*      */ 
/*      */   public void setInstrumentNamesSubscribed(Set<String> instrumentNames) {
/*  391 */     Set instruments = new HashSet();
/*  392 */     for (String instrumentStr : instrumentNames) {
/*  393 */       instruments.add(Instrument.fromString(instrumentStr));
/*      */     }
/*  395 */     setInstrumentsSubscribed(instruments);
/*      */   }
/*      */ 
/*      */   public void addInstrumentNamesSubscribed(Set<String> instrumentNames) {
/*  399 */     synchronized (this.subscribedInstruments) {
/*  400 */       Set instruments = new HashSet(this.subscribedInstruments);
/*  401 */       for (String instrumentStr : instrumentNames) {
/*  402 */         Instrument instrument = Instrument.fromString(instrumentStr);
/*  403 */         if (!instruments.contains(instrument)) {
/*  404 */           instruments.add(instrument);
/*      */         }
/*      */       }
/*  407 */       setInstrumentsSubscribed(instruments);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInstrumentNamesSubscribed(List<String> instrumentNames) {
/*  412 */     Set instruments = new HashSet();
/*  413 */     for (String instrumentStr : instrumentNames) {
/*  414 */       instruments.add(Instrument.fromString(instrumentStr));
/*      */     }
/*  416 */     setInstrumentsSubscribed(instruments);
/*      */   }
/*      */ 
/*      */   public void setInstrumentsSubscribed(Set<Instrument> instruments) {
/*  420 */     synchronized (this.subscribedInstruments) {
/*  421 */       List instrumentsAdded = new ArrayList(instruments);
/*  422 */       List instrumentsRemoved = new ArrayList(this.subscribedInstruments);
/*  423 */       instrumentsAdded.removeAll(this.subscribedInstruments);
/*  424 */       instrumentsRemoved.removeAll(instruments);
/*  425 */       this.subscribedInstruments.clear();
/*  426 */       this.subscribedInstruments.addAll(instruments);
/*      */       try {
/*  428 */         for (Instrument instrument : instrumentsAdded) {
/*  429 */           fireInstrumentSubscribed(instrument);
/*  430 */           this.intraperiodCandlesGenerator.addInstrument(instrument);
/*      */         }
/*  432 */         for (Instrument instrument : instrumentsRemoved) {
/*  433 */           this.lastTicks[instrument.ordinal()] = null;
/*  434 */           this.currentTimes[instrument.ordinal()] = -9223372036854775808L;
/*  435 */           fireInstrumentUnsubscribed(instrument);
/*  436 */           unsubscribeListenersFromInstrument(instrument);
/*      */         }
/*      */       } catch (Exception e) {
/*  439 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void unsubscribeListenersFromInstrument(Instrument instrument) {
/*  445 */     LOGGER.trace(new StringBuilder().append("Unsubscribing all listeners from instrument [").append(instrument).append("]").toString());
/*  446 */     this.localCacheManager.clearTicksIntraPeriod(instrument);
/*  447 */     this.tickListeners[instrument.ordinal()].clear();
/*  448 */     LOGGER.trace(new StringBuilder().append("Unsubscribing all listeners from instrument [").append(instrument).append("] for all periods and both sides").toString());
/*  449 */     this.intraperiodCandlesGenerator.removeInstrument(instrument);
/*  450 */     for (Period period : Period.values()) {
/*  451 */       putPeriodListeners(this.periodListenersMap, instrument, period, com.dukascopy.api.OfferSide.ASK, null);
/*  452 */       putPeriodListeners(this.periodListenersMap, instrument, period, com.dukascopy.api.OfferSide.BID, null);
/*      */     }
/*  454 */     this.allPeriodListeners[instrument.ordinal()][0] = null;
/*  455 */     this.allPeriodListeners[instrument.ordinal()][1] = null;
/*      */   }
/*      */ 
/*      */   public void fireInstrumentSubscribed(Instrument instrument) {
/*  459 */     InstrumentSubscriptionListener[] listeners = (InstrumentSubscriptionListener[])this.instrumentSubscriptionListeners.toArray(new InstrumentSubscriptionListener[this.instrumentSubscriptionListeners.size()]);
/*      */ 
/*  461 */     for (InstrumentSubscriptionListener instrumentSubscriptionListener : listeners)
/*  462 */       instrumentSubscriptionListener.subscribedToInstrument(instrument);
/*      */   }
/*      */ 
/*      */   public void fireInstrumentUnsubscribed(Instrument instrument)
/*      */   {
/*  467 */     InstrumentSubscriptionListener[] listeners = (InstrumentSubscriptionListener[])this.instrumentSubscriptionListeners.toArray(new InstrumentSubscriptionListener[this.instrumentSubscriptionListeners.size()]);
/*      */ 
/*  469 */     for (InstrumentSubscriptionListener instrumentSubscriptionListener : listeners)
/*  470 */       instrumentSubscriptionListener.unsubscribedFromInstrument(instrument);
/*      */   }
/*      */ 
/*      */   public void addInstrumentSubscriptionListener(InstrumentSubscriptionListener listener)
/*      */   {
/*  476 */     if (listener == null) {
/*  477 */       throw new NullPointerException("listener is null");
/*      */     }
/*  479 */     if (!this.instrumentSubscriptionListeners.contains(listener))
/*  480 */       this.instrumentSubscriptionListeners.add(listener);
/*      */   }
/*      */ 
/*      */   public List<Instrument> getInstrumentsCurrentlySubscribed()
/*      */   {
/*  486 */     synchronized (this.subscribedInstruments) {
/*  487 */       return new ArrayList(this.subscribedInstruments);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isSubscribedToInstrument(Instrument instrument)
/*      */   {
/*  493 */     synchronized (this.subscribedInstruments) {
/*  494 */       return this.subscribedInstruments.contains(instrument);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeInstrumentSubscriptionListener(InstrumentSubscriptionListener listener)
/*      */   {
/*  500 */     this.instrumentSubscriptionListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public boolean isDataCached(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to) throws DataCacheException
/*      */   {
/*  505 */     if (LOGGER.isTraceEnabled()) {
/*  506 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  507 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  508 */       LOGGER.trace(new StringBuilder().append("Checking isDataCached for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */     }
/*  510 */     IsDataCachedAction isDataCachedAction = new IsDataCachedAction(this, instrument, period, side, from, to);
/*  511 */     return isDataCachedAction.call().booleanValue();
/*      */   }
/*      */ 
/*      */   public void loadCandlesData(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  517 */     LoadDataAction loadDataAction = getLoadCandlesDataAction(instrument, period, side, from, to, candleListener, loadingProgress, false, false);
/*  518 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataBefore(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandles, long to, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  524 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, period, side, numberOfCandles, 0, to, filter, candleListener, loadingProgress);
/*      */ 
/*  526 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataAfter(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandles, long from, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  532 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, period, side, 0, numberOfCandles, from, filter, candleListener, loadingProgress);
/*      */ 
/*  534 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataBeforeAfter(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandlesBefore, int numberOfCandlesAfter, long time, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  541 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, period, side, numberOfCandlesBefore, numberOfCandlesAfter, time, filter, candleListener, loadingProgress);
/*      */ 
/*  543 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataBeforeAfterSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandlesBefore, int numberOfCandlesAfter, long time, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  550 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, period, side, numberOfCandlesBefore, numberOfCandlesAfter, time, filter, candleListener, loadingProgress);
/*      */ 
/*  552 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataBeforeSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandles, long to, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  558 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, period, side, numberOfCandles, 0, to, filter, candleListener, loadingProgress);
/*      */ 
/*  560 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataAfterSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandles, long from, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  566 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, period, side, 0, numberOfCandles, from, filter, candleListener, loadingProgress);
/*      */ 
/*  568 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadLastAvailableCandlesDataSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  574 */     LoadLastAvailableDataAction loadDataAction = getLoadLastAvailableCandlesDataAction(instrument, period, side, from, to, candleListener, loadingProgress);
/*  575 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadLastAvailableNumberOfCandlesDataSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandles, long to, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  582 */     LoadNumberOfLastAvailableDataAction loadDataAction = getLoadNumberOfLastAvailableCandlesDataAction(instrument, period, side, numberOfCandles, to, filter, candleListener, loadingProgress);
/*  583 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  589 */     LoadDataAction loadDataAction = getLoadCandlesDataAction(instrument, period, side, from, to, candleListener, loadingProgress, false, false);
/*  590 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, boolean loadFromChunkStart) throws DataCacheException
/*      */   {
/*  595 */     LoadDataAction loadDataAction = getLoadCandlesDataAction(instrument, period, side, from, to, candleListener, loadingProgress, false, loadFromChunkStart);
/*  596 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataBlockingSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  602 */     LoadDataAction loadDataAction = getLoadCandlesDataAction(instrument, period, side, from, to, candleListener, loadingProgress, true, false);
/*  603 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadInProgressCandleData(Instrument instrument, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  610 */     LoadInProgressCandleDataAction loadDataAction = getLoadInProgressCandleDataAction(instrument, to, candleListener, loadingProgress);
/*  611 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadInProgressCandleDataSynched(Instrument instrument, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  618 */     LoadInProgressCandleDataAction loadDataAction = getLoadInProgressCandleDataAction(instrument, to, candleListener, loadingProgress);
/*  619 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   private LoadNumberOfCandlesAction getLoadNumberOfCandlesAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandlesBefore, int numberOfCandlesAfter, long time, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  626 */     if (LOGGER.isTraceEnabled()) {
/*  627 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  628 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  629 */       LOGGER.trace(new StringBuilder().append("Loading [").append(numberOfCandlesBefore).append("] candles before and [").append(numberOfCandlesAfter).append("] after for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("] time [").append(dateFormat.format(new Date(time))).append("]").append(filter == Filter.ALL_FLATS ? " filtering all flats" : filter == Filter.WEEKENDS ? " filtering weekends" : "").toString());
/*      */ 
/*  632 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  633 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*  638 */           this.lastCurrentTime = currentTime;
/*  639 */           FeedDataProvider.LOGGER.trace(information);
/*  640 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  645 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  646 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob()
/*      */         {
/*  651 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  655 */     LoadNumberOfCandlesAction loadDataAction = new LoadNumberOfCandlesAction(this, instrument, period, side, numberOfCandlesBefore, numberOfCandlesAfter, time, filter, candleListener, loadingProgress, this.intraperiodExistsPolicy, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/*  658 */     if (this.stopped) {
/*  659 */       loadDataAction.cancel();
/*      */     }
/*  661 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadNumberOfCandlesAction getLoadNumberOfCandlesAction(Instrument instrument, int numberOfSecondsBefore, int numberOfSecondsAfter, long time, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  667 */     if (LOGGER.isTraceEnabled()) {
/*  668 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  669 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  670 */       LOGGER.trace(new StringBuilder().append("Loading [").append(numberOfSecondsBefore).append("] seconds before and [").append(numberOfSecondsAfter).append("] after of ticks for instrument [").append(instrument).append("] time [").append(dateFormat.format(new Date(time))).append("]").append(filter == Filter.ALL_FLATS ? " filtering all flats" : filter == Filter.WEEKENDS ? " filtering weekends" : "").toString());
/*      */ 
/*  673 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  674 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*  679 */           this.lastCurrentTime = currentTime;
/*  680 */           FeedDataProvider.LOGGER.trace(information);
/*  681 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  686 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  687 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob()
/*      */         {
/*  692 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  696 */     LoadNumberOfCandlesAction loadDataAction = new LoadNumberOfCandlesAction(this, instrument, numberOfSecondsBefore, numberOfSecondsAfter, time, filter, candleListener, loadingProgress, this.intraperiodExistsPolicy, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/*  698 */     if (this.stopped) {
/*  699 */       loadDataAction.cancel();
/*      */     }
/*  701 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadDataAction getLoadCandlesDataAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress, boolean blocking, boolean loadFromChunkStart) throws DataCacheException
/*      */   {
/*  706 */     if (LOGGER.isTraceEnabled()) {
/*  707 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  708 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  709 */       LOGGER.trace(new StringBuilder().append("Loading candles for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */ 
/*  711 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  712 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*  717 */           this.lastCurrentTime = currentTime;
/*  718 */           FeedDataProvider.LOGGER.trace(information);
/*  719 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  724 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  725 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob()
/*      */         {
/*  730 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  734 */     LoadDataAction loadDataAction = new LoadDataAction(this, instrument, period, side, from, to, candleListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null, blocking, this.intraperiodExistsPolicy, loadFromChunkStart);
/*      */ 
/*  737 */     if (this.stopped) {
/*  738 */       loadDataAction.cancel();
/*      */     }
/*  740 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadLastAvailableDataAction getLoadLastAvailableCandlesDataAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  745 */     if (LOGGER.isTraceEnabled()) {
/*  746 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  747 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  748 */       LOGGER.trace(new StringBuilder().append("Loading last available candles for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */ 
/*  750 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  751 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*      */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*  756 */           this.lastCurrentTime = currentTime;
/*  757 */           FeedDataProvider.LOGGER.trace(information);
/*  758 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */         }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  763 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  764 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  768 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  772 */     LoadLastAvailableDataAction loadDataAction = new LoadLastAvailableDataAction(this, instrument, period, side, from, to, this.intraperiodExistsPolicy, candleListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/*  774 */     if (this.stopped) {
/*  775 */       loadDataAction.cancel();
/*      */     }
/*  777 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadNumberOfLastAvailableDataAction getLoadNumberOfLastAvailableCandlesDataAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, int numberOfCandles, long to, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  782 */     if (LOGGER.isTraceEnabled()) {
/*  783 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  784 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  785 */       LOGGER.trace(new StringBuilder().append("Loading last available candles for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("] number of candles [").append(numberOfCandles).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */ 
/*  788 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  789 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*  793 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/*  794 */           FeedDataProvider.LOGGER.trace(information);
/*  795 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  799 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  800 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  804 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  808 */     LoadNumberOfLastAvailableDataAction loadDataAction = new LoadNumberOfLastAvailableDataAction(this, instrument, period, side, numberOfCandles, to, filter, this.intraperiodExistsPolicy, candleListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/*  811 */     if (this.stopped) {
/*  812 */       loadDataAction.cancel();
/*      */     }
/*  814 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadInProgressCandleDataAction getLoadInProgressCandleDataAction(Instrument instrument, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  819 */     if (LOGGER.isTraceEnabled()) {
/*  820 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  821 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  822 */       LOGGER.trace(new StringBuilder().append("Loading in-progress candle for instrument [").append(instrument).append("], to time [").append(dateFormat.format(new Date(to))).append("]").toString());
/*  823 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  824 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*  828 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/*  829 */           FeedDataProvider.LOGGER.trace(information);
/*  830 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  834 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  835 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  839 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  843 */     LoadInProgressCandleDataAction loadDataAction = new LoadInProgressCandleDataAction(this, instrument, to, candleListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/*  845 */     if (this.stopped) {
/*  846 */       loadDataAction.cancel();
/*      */     }
/*  848 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private boolean isAssertionsEnabled() {
/*  852 */     boolean b = false;
/*  853 */     assert ((b = 1) != 0);
/*  854 */     return b;
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataInCache(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  859 */     LoadDataAction loadDataAction = getLoadCandlesDataInCacheAction(instrument, period, side, from, to, loadingProgress, false);
/*  860 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadCandlesDataInCacheSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  865 */     LoadDataAction loadDataAction = getLoadCandlesDataInCacheAction(instrument, period, side, from, to, loadingProgress, false);
/*  866 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   private LoadDataAction getLoadCandlesDataInCacheAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LoadingProgressListener loadingProgress, boolean loadFromChunkStart) throws DataCacheException
/*      */   {
/*  871 */     if (LOGGER.isTraceEnabled()) {
/*  872 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  873 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  874 */       LOGGER.trace(new StringBuilder().append("Loading candles in cache for instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*  875 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  876 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*  880 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/*  881 */           FeedDataProvider.LOGGER.trace(information);
/*  882 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  886 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/*  887 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/*  891 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/*  895 */     LoadDataAction loadDataAction = new LoadDataAction(this, instrument, period, side, from, to, null, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null, false, CurvesDataLoader.IntraperiodExistsPolicy.FORCE_CHUNK_DOWNLOADING, loadFromChunkStart);
/*      */ 
/*  898 */     if (this.stopped) {
/*  899 */       loadDataAction.cancel();
/*      */     }
/*  901 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   public void loadTicksData(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  906 */     LoadDataAction loadDataAction = getLoadTicksDataAction(instrument, from, to, tickListener, loadingProgress, false, false);
/*  907 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadTicksDataBefore(Instrument instrument, int numberOfSeconds, long to, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  912 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, numberOfSeconds, 0, to, filter, candleListener, loadingProgress);
/*      */ 
/*  914 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadTicksDataAfter(Instrument instrument, int numberOfSeconds, long from, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  919 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, 0, numberOfSeconds, from, filter, candleListener, loadingProgress);
/*      */ 
/*  921 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadTicksDataBeforeAfter(Instrument instrument, int numberOfSecondsBefore, int numberOfSecondsAfter, long time, Filter filter, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  926 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, numberOfSecondsBefore, numberOfSecondsAfter, time, filter, tickListener, loadingProgress);
/*      */ 
/*  928 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadTicksDataBeforeAfterSynched(Instrument instrument, int numberOfSecondsBefore, int numberOfSecondsAfter, long time, Filter filter, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  933 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, numberOfSecondsBefore, numberOfSecondsAfter, time, filter, tickListener, loadingProgress);
/*      */ 
/*  935 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadTicksDataBeforeSynched(Instrument instrument, int numberOfSeconds, long to, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  940 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, numberOfSeconds, 0, to, filter, candleListener, loadingProgress);
/*      */ 
/*  942 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadTicksDataAfterSynched(Instrument instrument, int numberOfSeconds, long from, Filter filter, LiveFeedListener candleListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  947 */     LoadNumberOfCandlesAction loadDataAction = getLoadNumberOfCandlesAction(instrument, 0, numberOfSeconds, from, filter, candleListener, loadingProgress);
/*      */ 
/*  949 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadLastAvailableTicksDataSynched(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  954 */     LoadLastAvailableDataAction loadDataAction = getLoadLastAvailableTicksDataAction(instrument, from, to, tickListener, loadingProgress);
/*  955 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadLastAvailableNumberOfTicksDataSynched(Instrument instrument, int numberOfSeconds, long to, Filter filter, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  960 */     LoadNumberOfLastAvailableDataAction loadDataAction = getLoadNumberOfLastAvailableTicksData(instrument, numberOfSeconds, to, filter, tickListener, loadingProgress);
/*  961 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadTicksDataSynched(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  966 */     LoadDataAction loadDataAction = getLoadTicksDataAction(instrument, from, to, tickListener, loadingProgress, false, false);
/*  967 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadTicksDataSynched(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress, boolean loadFromChunkStart) throws DataCacheException
/*      */   {
/*  972 */     LoadDataAction loadDataAction = getLoadTicksDataAction(instrument, from, to, tickListener, loadingProgress, false, loadFromChunkStart);
/*  973 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadTicksDataBlockingSynched(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  978 */     LoadDataAction loadDataAction = getLoadTicksDataAction(instrument, from, to, tickListener, loadingProgress, true, false);
/*  979 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   private LoadDataAction getLoadTicksDataAction(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress, boolean blocking, boolean loadFromChunkStart) throws DataCacheException
/*      */   {
/*  984 */     if (LOGGER.isTraceEnabled()) {
/*  985 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  986 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  987 */       LOGGER.trace(new StringBuilder().append("Loading ticks for instrument [").append(instrument).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*  988 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/*  989 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/*  993 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/*  994 */           FeedDataProvider.LOGGER.trace(information);
/*  995 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/*  999 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/* 1000 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/* 1004 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/* 1008 */     LoadDataAction loadDataAction = new LoadDataAction(this, instrument, from, to, tickListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null, blocking, this.intraperiodExistsPolicy, loadFromChunkStart);
/*      */ 
/* 1011 */     if (this.stopped) {
/* 1012 */       loadDataAction.cancel();
/*      */     }
/* 1014 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadLastAvailableDataAction getLoadLastAvailableTicksDataAction(Instrument instrument, long from, long to, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1019 */     if (LOGGER.isTraceEnabled()) {
/* 1020 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1021 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1022 */       LOGGER.trace(new StringBuilder().append("Loading last available ticks for instrument [").append(instrument).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/* 1023 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/* 1024 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/* 1028 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/* 1029 */           FeedDataProvider.LOGGER.trace(information);
/* 1030 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/* 1034 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/* 1035 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/* 1039 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/* 1043 */     LoadLastAvailableDataAction loadDataAction = new LoadLastAvailableDataAction(this, instrument, from, to, this.intraperiodExistsPolicy, tickListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/* 1045 */     if (this.stopped) {
/* 1046 */       loadDataAction.cancel();
/*      */     }
/* 1048 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadNumberOfLastAvailableDataAction getLoadNumberOfLastAvailableTicksData(Instrument instrument, int numberOfSeconds, long to, Filter filter, LiveFeedListener tickListener, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1053 */     if (LOGGER.isTraceEnabled()) {
/* 1054 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1055 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1056 */       LOGGER.trace(new StringBuilder().append("Loading last available ticks for instrument [").append(instrument).append("] number of seconds [").append(numberOfSeconds).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/*      */ 
/* 1058 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/* 1059 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/* 1063 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/* 1064 */           FeedDataProvider.LOGGER.trace(information);
/* 1065 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/* 1069 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/* 1070 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/* 1074 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/* 1078 */     LoadNumberOfLastAvailableDataAction loadDataAction = new LoadNumberOfLastAvailableDataAction(this, instrument, numberOfSeconds, to, filter, this.intraperiodExistsPolicy, tickListener, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/* 1080 */     if (this.stopped) {
/* 1081 */       loadDataAction.cancel();
/*      */     }
/* 1083 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   public void loadTicksDataInCache(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1088 */     LoadDataAction loadDataAction = getLoadTicksDataInCacheAction(instrument, from, to, loadingProgress, false);
/* 1089 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadTicksDataInCacheSynched(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1094 */     LoadDataAction loadDataAction = getLoadTicksDataInCacheAction(instrument, from, to, loadingProgress, false);
/* 1095 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   private LoadDataAction getLoadTicksDataInCacheAction(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress, boolean loadFromChunkStart) throws DataCacheException
/*      */   {
/* 1100 */     if (LOGGER.isTraceEnabled()) {
/* 1101 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1102 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1103 */       LOGGER.trace(new StringBuilder().append("Loading ticks in cache for instrument [").append(instrument).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/* 1104 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/* 1105 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/* 1109 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/* 1110 */           FeedDataProvider.LOGGER.trace(information);
/* 1111 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/* 1115 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/* 1116 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/* 1120 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/* 1124 */     LoadDataAction loadDataAction = new LoadDataAction(this, instrument, from, to, null, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null, false, CurvesDataLoader.IntraperiodExistsPolicy.FORCE_CHUNK_DOWNLOADING, loadFromChunkStart);
/*      */ 
/* 1127 */     if (this.stopped) {
/* 1128 */       loadDataAction.cancel();
/*      */     }
/* 1130 */     return loadDataAction;
/*      */   }
/*      */ 
/*      */   private LoadInCacheAction getLoadInCacheAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LoadingProgressListener loadingProgress, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart, boolean loadFromDFSIfFailedFromHTTP, ChunkLoadingListener chunkLoadingListener)
/*      */   {
/* 1145 */     LoadInCacheAction loadInCacheAction = new LoadInCacheAction(this, instrument, period, side, from, to, loadingProgress, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null, intraperiodExistsPolicy, loadFromChunkStart, loadFromDFSIfFailedFromHTTP, chunkLoadingListener);
/*      */ 
/* 1148 */     if (this.stopped) {
/* 1149 */       loadInCacheAction.cancel();
/*      */     }
/* 1151 */     return loadInCacheAction;
/*      */   }
/*      */ 
/*      */   public Thread loadInCacheAsynch(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LoadingProgressListener loadingProgress, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, boolean loadFromChunkStart, ChunkLoadingListener chunkLoadingListener)
/*      */     throws DataCacheException
/*      */   {
/* 1158 */     LoadInCacheAction loadInCacheAction = getLoadInCacheAction(instrument, period, side, from, to, loadingProgress, intraperiodExistsPolicy, loadFromChunkStart, true, chunkLoadingListener);
/*      */ 
/* 1162 */     Thread thread = new Thread(loadInCacheAction, new StringBuilder().append("FeedDataProvider_LoadInCacheAsynch_").append(instrument).append("_").append(period).append("_").append(side).toString());
/* 1163 */     thread.setDaemon(true);
/* 1164 */     thread.setPriority(5);
/* 1165 */     thread.start();
/* 1166 */     return thread;
/*      */   }
/*      */ 
/*      */   public void loadHistoryDataInCacheFromCFGSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long from, long to, LoadingProgressListener loadingProgress)
/*      */   {
/* 1178 */     LoadInCacheAction loadInCacheAction = getLoadInCacheAction(instrument, period, side, from, to, loadingProgress, this.intraperiodExistsPolicy, true, false, new ChunkLoadingListener()
/*      */     {
/*      */       public void chunkLoaded(long[] chunk)
/*      */         throws DataCacheException
/*      */       {
/*      */       }
/*      */     });
/* 1194 */     loadInCacheAction.run();
/*      */   }
/*      */ 
/*      */   public void loadHistoryDataInCacheFromCFGSynched(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress)
/*      */   {
/* 1204 */     loadHistoryDataInCacheFromCFGSynched(instrument, Period.TICK, null, from, to, loadingProgress);
/*      */   }
/*      */ 
/*      */   public CandleData getInProgressCandle(Instrument instrument, Period period, com.dukascopy.api.OfferSide side) {
/* 1208 */     return this.intraperiodCandlesGenerator.getInProgressCandle(instrument, period, side);
/*      */   }
/*      */ 
/*      */   public CandleData getInProgressCandleBlocking(Instrument instrument, Period period, com.dukascopy.api.OfferSide side) throws DataCacheException {
/* 1212 */     return this.intraperiodCandlesGenerator.getInProgressCandleBlocking(instrument, period, side);
/*      */   }
/*      */ 
/*      */   public CurvesDataLoader getCurvesDataLoader()
/*      */   {
/* 1217 */     return this.curvesDataLoader;
/*      */   }
/*      */ 
/*      */   public LocalCacheManager getLocalCacheManager()
/*      */   {
/* 1222 */     return this.localCacheManager;
/*      */   }
/*      */ 
/*      */   public static ICurvesProtocolHandler getCurvesProtocolHandler() {
/* 1226 */     return curvesProtocolHandler;
/*      */   }
/*      */ 
/*      */   protected void fireNewTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 1230 */     List listeners = this.tickListeners[instrument.ordinal()];
/* 1231 */     LiveFeedListener[] listenersArr = (LiveFeedListener[])listeners.toArray(new LiveFeedListener[listeners.size()]);
/* 1232 */     for (LiveFeedListener liveFeedListener : listenersArr)
/* 1233 */       liveFeedListener.newTick(instrument, time, ask, bid, askVol, bidVol);
/*      */   }
/*      */ 
/*      */   protected void fireInProgressCandleUpdated(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, CandleData candle)
/*      */   {
/* 1238 */     List listenersList = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, side);
/* 1239 */     if (listenersList != null) {
/* 1240 */       LiveFeedListener[] listeners = (LiveFeedListener[])listenersList.toArray(new LiveFeedListener[listenersList.size()]);
/* 1241 */       for (LiveFeedListener liveFeedListener : listeners)
/* 1242 */         liveFeedListener.newCandle(instrument, period, side, candle.time, candle.open, candle.close, candle.low, candle.high, candle.vol);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void fireCandlesFormed(Instrument instrument, Period period, IntraPeriodCandleData askCandleData, IntraPeriodCandleData bidCandleData)
/*      */   {
/* 1252 */     long nextCandleStart = DataCacheUtils.getNextCandleStartFast(period, askCandleData != null ? askCandleData.time : bidCandleData.time);
/* 1253 */     if (this.currentTimes[instrument.ordinal()] < nextCandleStart) {
/* 1254 */       this.currentTimes[instrument.ordinal()] = nextCandleStart;
/*      */     }
/* 1256 */     if (this.currentTime < nextCandleStart) {
/* 1257 */       this.currentTime = nextCandleStart;
/*      */     }
/*      */ 
/* 1260 */     if (askCandleData != null) {
/* 1261 */       List listenersList = getPeriodListeners(this.periodListenersMap, instrument, period, com.dukascopy.api.OfferSide.ASK);
/* 1262 */       if (listenersList != null) {
/* 1263 */         LiveFeedListener[] listeners = (LiveFeedListener[])listenersList.toArray(new LiveFeedListener[listenersList.size()]);
/* 1264 */         for (LiveFeedListener liveFeedListener : listeners) {
/* 1265 */           liveFeedListener.newCandle(instrument, period, com.dukascopy.api.OfferSide.ASK, askCandleData.time, askCandleData.open, askCandleData.close, askCandleData.low, askCandleData.high, askCandleData.vol);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1270 */     if (bidCandleData != null) {
/* 1271 */       List listenersList = getPeriodListeners(this.periodListenersMap, instrument, period, com.dukascopy.api.OfferSide.BID);
/* 1272 */       if (listenersList != null) {
/* 1273 */         LiveFeedListener[] listeners = (LiveFeedListener[])listenersList.toArray(new LiveFeedListener[listenersList.size()]);
/* 1274 */         for (LiveFeedListener liveFeedListener : listeners) {
/* 1275 */           liveFeedListener.newCandle(instrument, period, com.dukascopy.api.OfferSide.BID, bidCandleData.time, bidCandleData.open, bidCandleData.close, bidCandleData.low, bidCandleData.high, bidCandleData.vol);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1280 */     if (askCandleData != null) {
/* 1281 */       List listenersList = this.allPeriodListeners[instrument.ordinal()][0];
/* 1282 */       if (listenersList != null) {
/* 1283 */         LiveFeedListener[] listeners = (LiveFeedListener[])listenersList.toArray(new LiveFeedListener[listenersList.size()]);
/* 1284 */         for (LiveFeedListener liveFeedListener : listeners) {
/* 1285 */           liveFeedListener.newCandle(instrument, period, com.dukascopy.api.OfferSide.ASK, askCandleData.time, askCandleData.open, askCandleData.close, askCandleData.low, askCandleData.high, askCandleData.vol);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1290 */     if (bidCandleData != null) {
/* 1291 */       List listenersList = this.allPeriodListeners[instrument.ordinal()][1];
/* 1292 */       if (listenersList != null) {
/* 1293 */         LiveFeedListener[] listeners = (LiveFeedListener[])listenersList.toArray(new LiveFeedListener[listenersList.size()]);
/* 1294 */         for (LiveFeedListener liveFeedListener : listeners) {
/* 1295 */           liveFeedListener.newCandle(instrument, period, com.dukascopy.api.OfferSide.BID, bidCandleData.time, bidCandleData.open, bidCandleData.close, bidCandleData.low, bidCandleData.high, bidCandleData.vol);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1300 */     if ((askCandleData != null) && (bidCandleData != null)) {
/* 1301 */       LiveCandleListener[] listeners = (LiveCandleListener[])this.allCandlePeriodListener.toArray(new LiveCandleListener[this.allCandlePeriodListener.size()]);
/* 1302 */       for (LiveCandleListener liveFeedListener : listeners)
/* 1303 */         liveFeedListener.newCandle(instrument, period, askCandleData, bidCandleData);
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getFirstTickLocalTime()
/*      */   {
/* 1309 */     return this.firstTickLocalTime;
/*      */   }
/*      */ 
/*      */   public void tickReceived(CurrencyMarket tick)
/*      */   {
/* 1315 */     Instrument instrument = Instrument.fromString(tick.getInstrument());
/* 1316 */     if (instrument == null) {
/* 1317 */       LOGGER.warn(new StringBuilder().append("Tick for instrument ").append(tick.getInstrument()).append(" is skipped.").toString());
/* 1318 */       return;
/*      */     }
/*      */ 
/* 1321 */     long time = tick.getCreationTimestamp().longValue();
/*      */ 
/* 1324 */     double askVol = 0.0D;
/* 1325 */     double bidVol = 0.0D;
/*      */     double ask;
/*      */     double bid;
/* 1326 */     synchronized (this) {
/* 1327 */       if (!tick.isBackup()) {
/* 1328 */         addTickToLatency(time);
/*      */       }
/* 1330 */       if (this.firstTickLocalTime == -9223372036854775808L) {
/* 1331 */         this.firstTickLocalTime = System.currentTimeMillis();
/*      */       }
/* 1333 */       CurrencyOffer currencyOffer = tick.getBestOffer(com.dukascopy.transport.common.model.type.OfferSide.ASK);
/* 1334 */       ask = this.lastAsks[instrument.ordinal()];
/* 1335 */       if (currencyOffer != null) {
/* 1336 */         ask = currencyOffer.getPrice().getValue().doubleValue();
/* 1337 */         this.lastAsks[instrument.ordinal()] = ask;
/* 1338 */         askVol = StratUtils.round(currencyOffer.getAmount().getValue().doubleValue() / 1000000.0D, 6);
/*      */       }
/* 1340 */       currencyOffer = tick.getBestOffer(com.dukascopy.transport.common.model.type.OfferSide.BID);
/* 1341 */       bid = this.lastBids[instrument.ordinal()];
/* 1342 */       if (currencyOffer != null) {
/* 1343 */         bid = currencyOffer.getPrice().getValue().doubleValue();
/* 1344 */         this.lastBids[instrument.ordinal()] = bid;
/* 1345 */         bidVol = StratUtils.round(currencyOffer.getAmount().getValue().doubleValue() / 1000000.0D, 6);
/*      */       }
/* 1347 */       if ((Double.isNaN(ask)) || (Double.isNaN(bid)))
/*      */       {
/* 1349 */         return;
/*      */       }
/*      */ 
/* 1352 */       if ((this.lastTicks[instrument.ordinal()] != null) && (time < this.lastTicks[instrument.ordinal()].time)) {
/* 1353 */         LOGGER.warn("Receved tick has time older than previous tick, ignoring");
/* 1354 */         return;
/*      */       }
/* 1356 */       List askOffers = tick.getAsks();
/* 1357 */       double[] asks = new double[askOffers.size() > 0 ? askOffers.size() : 1];
/* 1358 */       double[] askVols = new double[asks.length];
/*      */       int i;
/* 1359 */       if (askOffers.size() > 0) {
/* 1360 */         i = 0;
/* 1361 */         for (CurrencyOffer askOffer : askOffers) {
/* 1362 */           asks[i] = askOffer.getPrice().getValue().doubleValue();
/* 1363 */           askVols[i] = StratUtils.round(askOffer.getAmount().getValue().doubleValue() / 1000000.0D, 6);
/* 1364 */           i++;
/*      */         }
/*      */       } else {
/* 1367 */         asks[0] = ask;
/* 1368 */         askVols[0] = askVol;
/*      */       }
/* 1370 */       List bidOffers = tick.getBids();
/* 1371 */       double[] bids = new double[bidOffers.size() > 0 ? bidOffers.size() : 1];
/* 1372 */       double[] bidVols = new double[bids.length];
/*      */       int i;
/* 1373 */       if (bidOffers.size() > 0) {
/* 1374 */         i = 0;
/* 1375 */         for (CurrencyOffer bidOffer : bidOffers) {
/* 1376 */           bids[i] = bidOffer.getPrice().getValue().doubleValue();
/* 1377 */           bidVols[i] = StratUtils.round(bidOffer.getAmount().getValue().doubleValue() / 1000000.0D, 6);
/* 1378 */           i++;
/*      */         }
/*      */       } else {
/* 1381 */         bids[0] = bid;
/* 1382 */         bidVols[0] = bidVol;
/*      */       }
/* 1384 */       TickData tickData = new TickData(time, ask, bid, askVol, bidVol, asks, bids, askVols, bidVols);
/* 1385 */       if ((LOGGER.isDebugEnabled()) && (this.lastTicks[instrument.ordinal()] == null)) {
/* 1386 */         LOGGER.debug(new StringBuilder().append("First tick received for [").append(instrument).append("] - [").append(tickData).append("]").toString());
/*      */       }
/* 1388 */       this.lastTicks[instrument.ordinal()] = tickData;
/* 1389 */       this.currentTimes[instrument.ordinal()] = time;
/* 1390 */       this.currentTime = time;
/*      */     }
/*      */ 
/* 1393 */     this.localCacheManager.newTick(instrument, time, ask, bid, askVol, bidVol, false);
/*      */ 
/* 1395 */     this.intraperiodCandlesGenerator.newTick(instrument, time, ask, bid, askVol, bidVol);
/*      */ 
/* 1397 */     fireNewTick(instrument, time, ask, bid, askVol, bidVol);
/*      */   }
/*      */ 
/*      */   public void newOrder(Instrument instrument, OrderHistoricalData orderData)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void orderChange(Instrument instrument, OrderHistoricalData orderData)
/*      */   {
/* 1407 */     if (((orderData.getMergedToGroupId() != null) || (orderData.isClosed())) && (orderData.isOpened()) && (accountId != null)) {
/* 1408 */       if (LOGGER.isDebugEnabled())
/* 1409 */         LOGGER.debug(new StringBuilder().append("Saving closed order [").append(orderData).append("]").toString());
/*      */       try
/*      */       {
/* 1412 */         this.localCacheManager.saveOrderData(accountId, instrument, orderData);
/*      */       } catch (DataCacheException e) {
/* 1414 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void orderMerge(Instrument instrument, OrderHistoricalData resultingOrderData, List<OrderHistoricalData> mergedOrdersData)
/*      */   {
/* 1421 */     if (((resultingOrderData.getMergedToGroupId() != null) || (resultingOrderData.isClosed())) && (resultingOrderData.isOpened()) && (accountId != null)) {
/* 1422 */       if (LOGGER.isDebugEnabled())
/* 1423 */         LOGGER.debug(new StringBuilder().append("Saving closed order [").append(resultingOrderData).append("]").toString());
/*      */       try
/*      */       {
/* 1426 */         this.localCacheManager.saveOrderData(accountId, instrument, resultingOrderData);
/*      */       } catch (DataCacheException e) {
/* 1428 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/* 1431 */     for (OrderHistoricalData mergedOrder : mergedOrdersData) {
/* 1432 */       if (LOGGER.isDebugEnabled())
/* 1433 */         LOGGER.debug(new StringBuilder().append("Saving merged order [").append(mergedOrder).append("]").toString());
/*      */       try
/*      */       {
/* 1436 */         this.localCacheManager.saveOrderData(accountId, instrument, mergedOrder);
/*      */       } catch (DataCacheException e) {
/* 1438 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ordersInvalidated(Instrument instrument)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized double getLastAsk(Instrument instrument)
/*      */   {
/* 1449 */     return this.lastAsks[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   public synchronized double getLastBid(Instrument instrument) {
/* 1453 */     return this.lastBids[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   public synchronized long getCurrentTime(Instrument instrument) {
/* 1457 */     TickData tickData = this.lastTicks[instrument.ordinal()];
/* 1458 */     if (tickData == null) {
/* 1459 */       return this.currentTimes[instrument.ordinal()];
/*      */     }
/* 1461 */     return Math.max(this.currentTimes[instrument.ordinal()], tickData.time);
/*      */   }
/*      */ 
/*      */   public synchronized long getLastTickTime(Instrument instrument)
/*      */   {
/* 1466 */     TickData tickData = this.lastTicks[instrument.ordinal()];
/* 1467 */     if (tickData == null) {
/* 1468 */       return -9223372036854775808L;
/*      */     }
/* 1470 */     return tickData.time;
/*      */   }
/*      */ 
/*      */   public synchronized long getLastTickTime()
/*      */   {
/* 1475 */     long ret = -9223372036854775808L;
/* 1476 */     for (TickData lastTick : this.lastTicks) {
/* 1477 */       if ((lastTick != null) && (lastTick.time > ret)) {
/* 1478 */         ret = lastTick.time;
/*      */       }
/*      */     }
/* 1481 */     return ret;
/*      */   }
/*      */ 
/*      */   public synchronized TickData getLastTick(Instrument instrument) {
/* 1485 */     return this.lastTicks[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   public void setCurrentTime(long currentTime)
/*      */   {
/* 1492 */     this.currentTime = currentTime;
/*      */   }
/*      */ 
/*      */   public long getCurrentTime() {
/* 1496 */     return this.currentTime;
/*      */   }
/*      */ 
/*      */   public long getTimeOfFirstCandle(Instrument instrument, Period period) {
/* 1500 */     long time = feedDataHistoryFirstTimesManager.getFirstFeedDataTime(instrument, period);
/* 1501 */     return time;
/*      */   }
/*      */ 
/*      */   public void runTask(Runnable task) {
/* 1505 */     this.actionsExecutorService.submit(task);
/*      */   }
/*      */ 
/*      */   protected void finalize() throws Throwable
/*      */   {
/* 1510 */     super.finalize();
/* 1511 */     if (!this.stopped)
/* 1512 */       close();
/*      */   }
/*      */ 
/*      */   public void close()
/*      */   {
/* 1517 */     this.stopped = true;
/* 1518 */     this.actionsExecutorService.shutdown();
/* 1519 */     synchronized (this.currentlyRunningTasks) {
/* 1520 */       for (Runnable currentlyRunningTask : this.currentlyRunningTasks) {
/* 1521 */         if ((currentlyRunningTask instanceof LoadProgressingAction))
/* 1522 */           ((LoadProgressingAction)currentlyRunningTask).cancel();
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1527 */       if (!this.actionsExecutorService.awaitTermination(15L, TimeUnit.SECONDS))
/* 1528 */         this.actionsExecutorService.shutdownNow();
/*      */     }
/*      */     catch (InterruptedException e) {
/* 1531 */       LOGGER.error(e.getMessage(), e);
/* 1532 */       this.actionsExecutorService.shutdownNow();
/*      */     }
/*      */ 
/* 1535 */     this.intraperiodCandlesGenerator.stop();
/*      */     try {
/* 1537 */       this.localCacheManager.closeHandles();
/* 1538 */       if ((this.localCacheManager.cacheLock != null) && (this.localCacheManager.cacheLock.isValid())) {
/* 1539 */         this.localCacheManager.cacheLock.release();
/* 1540 */         this.localCacheManager.cacheLock.channel().close();
/*      */       }
/* 1542 */       this.localCacheManager = null;
/*      */     } catch (IOException e) {
/* 1544 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/* 1546 */     this.instrumentSubscriptionListeners.clear();
/* 1547 */     for (Instrument instrument : Instrument.values()) {
/* 1548 */       for (Period period : Period.values()) {
/* 1549 */         List askInProgressCandleListeners = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, com.dukascopy.api.OfferSide.ASK);
/* 1550 */         List bidInProgressCandleListeners = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, com.dukascopy.api.OfferSide.BID);
/*      */ 
/* 1552 */         if (askInProgressCandleListeners != null) {
/* 1553 */           askInProgressCandleListeners.clear();
/*      */         }
/* 1555 */         if (bidInProgressCandleListeners != null) {
/* 1556 */           bidInProgressCandleListeners.clear();
/*      */         }
/*      */       }
/*      */     }
/* 1560 */     this.allCandlePeriodListener.clear();
/* 1561 */     for (int i = 0; i < Instrument.values().length; i++) {
/* 1562 */       for (int j = 0; j < Period.values().length; j++) {
/* 1563 */         Instrument instrument = Instrument.values()[i];
/* 1564 */         Period period = Period.values()[j];
/* 1565 */         List askPeriodListeners = getPeriodListeners(this.periodListenersMap, instrument, period, com.dukascopy.api.OfferSide.ASK);
/* 1566 */         List bidPeriodListeners = getPeriodListeners(this.periodListenersMap, instrument, period, com.dukascopy.api.OfferSide.BID);
/* 1567 */         if (askPeriodListeners != null) {
/* 1568 */           askPeriodListeners.clear();
/*      */         }
/* 1570 */         if (bidPeriodListeners != null) {
/* 1571 */           bidPeriodListeners.clear();
/*      */         }
/*      */       }
/* 1574 */       if (this.allPeriodListeners[i][0] != null) {
/* 1575 */         this.allPeriodListeners[i][0].clear();
/*      */       }
/* 1577 */       if (this.allPeriodListeners[i][1] != null) {
/* 1578 */         this.allPeriodListeners[i][1].clear();
/*      */       }
/*      */     }
/* 1581 */     for (List tickListener : this.tickListeners) {
/* 1582 */       tickListener.clear();
/*      */     }
/* 1584 */     for (List cacheDataChangeListener : this.cacheDataChangeListeners) {
/* 1585 */       cacheDataChangeListener.clear();
/*      */     }
/* 1587 */     this.ordersProvider.close();
/* 1588 */     if (this == getDefaultInstance()) {
/* 1589 */       feedDataProvider = null;
/* 1590 */       if (curvesProtocolHandler != null) {
/* 1591 */         curvesProtocolHandler.close();
/* 1592 */         curvesProtocolHandler = null;
/*      */       }
/*      */     }
/*      */ 
/* 1596 */     if (this.backgroundFeedLoadingThread != null) {
/* 1597 */       this.backgroundFeedLoadingThread.setFinished(true);
/*      */     }
/*      */ 
/* 1600 */     getPriceAggregationDataProvider().close();
/* 1601 */     this.priceAggregationDataProvider = null;
/*      */ 
/* 1603 */     this.dailyHighLowManager.removeAllListeners();
/*      */ 
/* 1605 */     if (curvesProtocolHandler != null)
/* 1606 */       curvesProtocolHandler.removeAllDFSConnectionListeners();
/*      */   }
/*      */ 
/*      */   protected LoadOrdersAction getLoadOrdersAction(Instrument instrument, long from, long to, OrdersListener ordersListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1612 */     if (LOGGER.isTraceEnabled()) {
/* 1613 */       DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1614 */       dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1615 */       LOGGER.trace(new StringBuilder().append("Loading orders instrument [").append(instrument).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("]").toString());
/* 1616 */       LoadingProgressListener originalLoadingProgressListener = loadingProgress;
/* 1617 */       loadingProgress = new LoadingProgressListener(originalLoadingProgressListener) {
/*      */         private long lastCurrentTime;
/*      */ 
/* 1621 */         public void dataLoaded(long startTime, long endTime, long currentTime, String information) { this.lastCurrentTime = currentTime;
/* 1622 */           FeedDataProvider.LOGGER.trace(information);
/* 1623 */           this.val$originalLoadingProgressListener.dataLoaded(startTime, endTime, currentTime, information); }
/*      */ 
/*      */         public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */         {
/* 1627 */           FeedDataProvider.LOGGER.trace(new StringBuilder().append("Loading fineshed with ").append(allDataLoaded ? "OK" : "ERROR").append(" status").toString());
/* 1628 */           this.val$originalLoadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, allDataLoaded ? currentTime : this.lastCurrentTime, e);
/*      */         }
/*      */ 
/*      */         public boolean stopJob() {
/* 1632 */           return this.val$originalLoadingProgressListener.stopJob();
/*      */         } } ;
/*      */     }
/* 1636 */     LoadOrdersAction loadOrdersAction = new LoadOrdersAction(this, accountId, instrument, from, to, ordersListener, loadingProgress, this.intraperiodExistsPolicy, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/*      */ 
/* 1638 */     if (this.stopped) {
/* 1639 */       loadOrdersAction.cancel();
/*      */     }
/* 1641 */     return loadOrdersAction;
/*      */   }
/*      */ 
/*      */   public void loadOrdersHistoricalData(Instrument instrument, long from, long to, OrdersListener ordersListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1647 */     LoadOrdersAction loadDataAction = getLoadOrdersAction(instrument, from, to, ordersListener, loadingProgress);
/* 1648 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadOrdersHistoricalDataSynched(Instrument instrument, long from, long to, OrdersListener ordersListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1654 */     LoadOrdersAction loadDataAction = getLoadOrdersAction(instrument, from, to, ordersListener, loadingProgress);
/* 1655 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   public void loadOrdersHistoricalDataInCache(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1660 */     LoadOrdersAction loadDataAction = getLoadOrdersAction(instrument, from, to, null, loadingProgress);
/* 1661 */     this.actionsExecutorService.submit(loadDataAction);
/*      */   }
/*      */ 
/*      */   public void loadOrdersHistoricalDataInCacheSynched(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1666 */     LoadOrdersAction loadDataAction = getLoadOrdersAction(instrument, from, to, null, loadingProgress);
/* 1667 */     loadDataAction.run();
/*      */   }
/*      */ 
/*      */   protected static String getEncryptionKey() {
/* 1671 */     if (encryptionKey == null) {
/* 1672 */       LOGGER.warn("Encryption key was not found, creation temporary key");
/*      */       try {
/* 1674 */         KeyGenerator kgen = KeyGenerator.getInstance("AES");
/* 1675 */         kgen.init(128);
/*      */ 
/* 1678 */         SecretKey skey = kgen.generateKey();
/* 1679 */         byte[] raw = skey.getEncoded();
/* 1680 */         encryptionKey = Hex.encodeHexString(raw);
/*      */       } catch (NoSuchAlgorithmException e) {
/* 1682 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/* 1685 */     return encryptionKey;
/*      */   }
/*      */ 
/*      */   public void subscribeToOrdersNotifications(Instrument instrument, OrdersListener listener)
/*      */   {
/* 1690 */     LOGGER.trace(new StringBuilder().append("Subscribing one more listener for orders notifications to instrument [").append(instrument).append("]").toString());
/* 1691 */     this.ordersProvider.addOrdersListener(instrument, listener);
/*      */   }
/*      */ 
/*      */   public void unsubscribeFromOrdersNotifications(Instrument instrument, OrdersListener listener)
/*      */   {
/* 1696 */     LOGGER.trace(new StringBuilder().append("Unsubscribing orders notifications listener from instrument [").append(instrument).append("]").toString());
/* 1697 */     this.ordersProvider.removeOrdersListener(listener);
/*      */   }
/*      */ 
/*      */   public IOrdersProvider getOrdersProvider() {
/* 1701 */     return this.ordersProvider;
/*      */   }
/*      */ 
/*      */   public IOrderUtils getOrderUtils()
/*      */   {
/* 1706 */     return this.ordersProvider.getOrderUtils();
/*      */   }
/*      */ 
/*      */   public synchronized void disconnected() {
/* 1710 */     if (this.connected) {
/* 1711 */       this.connected = false;
/* 1712 */       long lastTickTime = getLastTickTime();
/* 1713 */       if (lastTickTime == -9223372036854775808L) {
/* 1714 */         lastTickTime = System.currentTimeMillis();
/*      */       }
/* 1716 */       this.disconnectTime = (lastTickTime - 300000L);
/* 1717 */       for (int i = 0; i < this.lastAsks.length; i++) {
/* 1718 */         this.lastAsks[i] = (0.0D / 0.0D);
/* 1719 */         this.lastBids[i] = (0.0D / 0.0D);
/*      */       }
/* 1721 */       for (int i = 0; i < this.lastTicks.length; i++) {
/* 1722 */         this.lastTicks[i] = null;
/*      */       }
/* 1724 */       this.localCacheManager.resetLastOrderUpdateTimes();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void connected() {
/* 1729 */     if (!this.connected) {
/* 1730 */       UpdateCacheDataAction action = new UpdateCacheDataAction(this, this.disconnectTime, isAssertionsEnabled() ? Thread.currentThread().getStackTrace() : null);
/* 1731 */       this.actionsExecutorService.submit(action);
/* 1732 */       this.connected = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addCacheDataUpdatedListener(Instrument instrument, CacheDataUpdatedListener listener)
/*      */   {
/* 1738 */     this.cacheDataChangeListeners[instrument.ordinal()].add(listener);
/*      */   }
/*      */ 
/*      */   public void removeCacheDataUpdatedListener(Instrument instrument, CacheDataUpdatedListener listener)
/*      */   {
/* 1743 */     this.cacheDataChangeListeners[instrument.ordinal()].remove(listener);
/*      */   }
/*      */ 
/*      */   protected void fireCacheDataChanged(Instrument instrument, long disconnectedTime, long connectedTime) {
/* 1747 */     CacheDataUpdatedListener[] listeners = (CacheDataUpdatedListener[])this.cacheDataChangeListeners[instrument.ordinal()].toArray(new CacheDataUpdatedListener[this.cacheDataChangeListeners[instrument.ordinal()].size()]);
/* 1748 */     for (CacheDataUpdatedListener listener : listeners)
/* 1749 */       listener.cacheUpdated(instrument, disconnectedTime, connectedTime);
/*      */   }
/*      */ 
/*      */   public boolean isPeriodSubscribedInProgressCandle(Instrument instrument, Period period)
/*      */   {
/* 1754 */     List askListenersList = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, com.dukascopy.api.OfferSide.ASK);
/* 1755 */     List bidListenersList = getPeriodListeners(this.inProgressCandleListenersMap, instrument, period, com.dukascopy.api.OfferSide.BID);
/*      */ 
/* 1760 */     return ((askListenersList != null) && (!askListenersList.isEmpty())) || ((bidListenersList != null) && (!bidListenersList.isEmpty()));
/*      */   }
/*      */ 
/*      */   private void addTickToLatency(long time)
/*      */   {
/* 1767 */     int latency = (int)(System.currentTimeMillis() - time);
/* 1768 */     this.networkLatency[this.latencyIndex] = latency;
/* 1769 */     this.latencyIndex += 1;
/* 1770 */     if (this.latencyIndex >= this.networkLatency.length)
/* 1771 */       this.latencyIndex = 0;
/*      */   }
/*      */ 
/*      */   public synchronized int getLatency()
/*      */   {
/* 1776 */     int latencySum = 0;
/* 1777 */     for (int aNetworkLatency : this.networkLatency) {
/* 1778 */       latencySum += aNetworkLatency;
/*      */     }
/* 1780 */     return latencySum / this.networkLatency.length;
/*      */   }
/*      */ 
/*      */   public long getEstimatedServerTime() {
/* 1784 */     return System.currentTimeMillis() - getLatency();
/*      */   }
/*      */ 
/*      */   public long getLatestKnownTimeOrCurrentGMTTime(Instrument instrument)
/*      */   {
/* 1870 */     if (instrument == null) {
/* 1871 */       throw new NullPointerException("Params are not correct!");
/*      */     }
/*      */ 
/* 1874 */     long time = getLastTickTime(instrument);
/* 1875 */     if (-9223372036854775808L == time) {
/* 1876 */       time = Calendar.getInstance(TimeZone.getTimeZone("GMT 0")).getTimeInMillis();
/*      */     }
/* 1878 */     return time;
/*      */   }
/*      */ 
/*      */   public void startInBackgroundFeedPreloadingToLocalCache()
/*      */   {
/* 1884 */     if (this.backgroundFeedLoadingThread == null) {
/* 1885 */       this.backgroundFeedLoadingThread = new BackgroundFeedLoadingThread(this);
/* 1886 */       this.backgroundFeedLoadingThread.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<CandleData> loadCandlesFromToSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide offerSide, Filter filter, long from, long to)
/*      */     throws DataCacheException
/*      */   {
/* 1900 */     List candles = new ArrayList();
/*      */ 
/* 1902 */     LiveFeedListener candleListener = new LiveFeedListener(candles)
/*      */     {
/*      */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*      */       }
/*      */ 
/*      */       public void newCandle(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long time, double open, double close, double low, double high, double vol) {
/* 1908 */         CandleData candle = new CandleData(time, open, close, low, high, vol);
/* 1909 */         this.val$candles.add(candle);
/*      */       }
/*      */     };
/* 1912 */     List exception = new ArrayList();
/* 1913 */     LoadingProgressListener loadingProgress = new LoadingProgressListener(exception)
/*      */     {
/*      */       public void dataLoaded(long start, long end, long currentPosition, String information) {
/*      */       }
/*      */ 
/*      */       public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e) {
/* 1919 */         if (e != null)
/* 1920 */           this.val$exception.add(e);
/*      */       }
/*      */ 
/*      */       public boolean stopJob()
/*      */       {
/* 1925 */         return false;
/*      */       }
/*      */     };
/* 1930 */     loadCandlesFromToSynched(instrument, period, offerSide, filter, from, to, candleListener, loadingProgress);
/*      */ 
/* 1932 */     if (!exception.isEmpty()) {
/* 1933 */       throw new DataCacheException((Throwable)exception.get(0));
/*      */     }
/*      */ 
/* 1936 */     return candles;
/*      */   }
/*      */ 
/*      */   public void loadCandlesFromTo(Instrument instrument, Period period, com.dukascopy.api.OfferSide offerSide, Filter filter, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1950 */     if ((filter == null) || (Filter.NO_FILTER.equals(filter))) {
/* 1951 */       loadCandlesData(instrument, period, offerSide, from, to, candleListener, loadingProgress);
/*      */     }
/*      */     else {
/* 1954 */       LoadFilteredDataAction action = createLoadFilteredDataAction(instrument, period, offerSide, filter, from, to, candleListener, loadingProgress);
/* 1955 */       this.actionsExecutorService.submit(action);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void loadCandlesFromToSynched(Instrument instrument, Period period, com.dukascopy.api.OfferSide offerSide, Filter filter, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1970 */     if ((filter == null) || (Filter.NO_FILTER.equals(filter))) {
/* 1971 */       loadCandlesDataSynched(instrument, period, offerSide, from, to, candleListener, loadingProgress);
/*      */     }
/*      */     else {
/* 1974 */       LoadFilteredDataAction action = createLoadFilteredDataAction(instrument, period, offerSide, filter, from, to, candleListener, loadingProgress);
/* 1975 */       action.run();
/*      */     }
/*      */   }
/*      */ 
/*      */   private LoadFilteredDataAction createLoadFilteredDataAction(Instrument instrument, Period period, com.dukascopy.api.OfferSide offerSide, Filter filter, long from, long to, LiveFeedListener candleListener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1989 */     LoadFilteredDataAction action = new LoadFilteredDataAction(feedDataProvider, instrument, period, offerSide, filter, from, to, candleListener, loadingProgress, false, this.intraperiodExistsPolicy, false);
/* 1990 */     return action;
/*      */   }
/*      */ 
/*      */   public CurvesDataLoader.IntraperiodExistsPolicy getIntraperiodExistsPolicy()
/*      */   {
/* 1995 */     return this.intraperiodExistsPolicy;
/*      */   }
/*      */ 
/*      */   public IFilterManager getFilterManager()
/*      */   {
/* 2000 */     if (this.filterManager == null) {
/* 2001 */       this.filterManager = new FilterManager(this);
/*      */     }
/* 2003 */     return this.filterManager;
/*      */   }
/*      */ 
/*      */   public IIntraperiodBarsGenerator getIntraperiodBarsGenerator()
/*      */   {
/* 2008 */     return this.intraperiodCandlesGenerator.getIntraperiodBarsGenerator();
/*      */   }
/*      */ 
/*      */   public IPriceAggregationDataProvider getPriceAggregationDataProvider()
/*      */   {
/* 2013 */     return this.priceAggregationDataProvider;
/*      */   }
/*      */ 
/*      */   public List<Instrument> getInstrumentsSupportedByFileCacheGenerator()
/*      */   {
/* 2025 */     List result = feedDataHistoryFirstTimesManager.getSupportedInstruments();
/* 2026 */     return result;
/*      */   }
/*      */ 
/*      */   public IDailyHighLowManager getDailyHighLowManager()
/*      */   {
/* 2031 */     return this.dailyHighLowManager;
/*      */   }
/*      */ 
/*      */   public long getTimeOfFirstBar(Instrument instrument, PriceRange priceRange)
/*      */   {
/* 2036 */     Period period = TimeDataUtils.getSuitablePeriod(priceRange);
/* 2037 */     long result = getTimeOfFirstCandle(instrument, period);
/* 2038 */     return result;
/*      */   }
/*      */ 
/*      */   public long getTimeOfFirstBar(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount)
/*      */   {
/* 2043 */     Period period = TimeDataUtils.getSuitablePeriod(priceRange, reversalAmount);
/* 2044 */     long result = getTimeOfFirstCandle(instrument, period);
/* 2045 */     return result;
/*      */   }
/*      */ 
/*      */   public long getTimeOfFirstBar(Instrument instrument, TickBarSize tickBarSize)
/*      */   {
/* 2050 */     long result = getTimeOfFirstCandle(instrument, Period.TICK);
/* 2051 */     return result;
/*      */   }
/*      */ 
/*      */   public long getTimeOfFirstTick(Instrument instrument)
/*      */   {
/* 2056 */     long result = getTimeOfFirstCandle(instrument, Period.TICK);
/* 2057 */     return result;
/*      */   }
/*      */ 
/*      */   public void addDataFeedServerConnectionListener(DataFeedServerConnectionListener listener)
/*      */   {
/* 2062 */     curvesProtocolHandler.addDFSConnectionListener(listener);
/*      */   }
/*      */ 
/*      */   public List<DataFeedServerConnectionListener> getDataFeedServerConnectionListeners()
/*      */   {
/* 2067 */     return curvesProtocolHandler.getDFSConnectionListeners();
/*      */   }
/*      */ 
/*      */   public void removeDataFeedServerConnectionListener(DataFeedServerConnectionListener listener)
/*      */   {
/* 2072 */     curvesProtocolHandler.removeDFSConnectionListener(listener);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   76 */     LOGGER = LoggerFactory.getLogger(FeedDataProvider.class);
/*      */ 
/*   80 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/*      */ 
/*   82 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*      */   }
/*      */ 
/*      */   private static class FeedAuthenticator
/*      */     implements IAuthenticator
/*      */   {
/*      */     private final Collection<String> authServerUrls;
/*      */     private final String version;
/*      */     private final String userName;
/*      */     private final String instanceId;
/*      */ 
/*      */     private FeedAuthenticator(Collection<String> authServerUrls, String version, String userName, String instanceId)
/*      */     {
/* 1848 */       this.authServerUrls = authServerUrls;
/* 1849 */       this.version = version;
/* 1850 */       this.userName = userName;
/* 1851 */       this.instanceId = instanceId;
/*      */     }
/*      */ 
/*      */     public String authenticate()
/*      */     {
/* 1856 */       AuthorizationClient auClient = AuthorizationClient.getInstance(this.authServerUrls, this.version);
/*      */       try {
/* 1858 */         return auClient.getFeedUrlAndTicket(this.userName, FeedDataProvider.platformTicket, this.instanceId);
/*      */       } catch (IOException e) {
/* 1860 */         FeedDataProvider.LOGGER.error(e.getMessage(), e);
/*      */       } catch (NoSuchAlgorithmException e) {
/* 1862 */         FeedDataProvider.LOGGER.error(e.getMessage(), e);
/*      */       }
/* 1864 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class FeedExecutor extends ThreadPoolExecutor
/*      */   {
/*      */     private final List<Runnable> currentlyRunningTasks;
/*      */ 
/*      */     public FeedExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, List<Runnable> currentlyRunningTasks)
/*      */     {
/* 1815 */       super(maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
/* 1816 */       this.currentlyRunningTasks = currentlyRunningTasks;
/*      */     }
/*      */ 
/*      */     protected void beforeExecute(Thread t, Runnable r)
/*      */     {
/* 1821 */       synchronized (this.currentlyRunningTasks) {
/* 1822 */         this.currentlyRunningTasks.add(r);
/*      */       }
/* 1824 */       super.beforeExecute(t, r);
/*      */     }
/*      */ 
/*      */     protected void afterExecute(Runnable r, Throwable t)
/*      */     {
/* 1829 */       synchronized (this.currentlyRunningTasks) {
/* 1830 */         this.currentlyRunningTasks.remove(r);
/*      */       }
/* 1832 */       super.afterExecute(r, t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class FeedThreadFactory
/*      */     implements ThreadFactory
/*      */   {
/* 1788 */     final AtomicInteger threadNumber = new AtomicInteger(1);
/*      */ 
/*      */     public Thread newThread(Runnable r) {
/* 1791 */       Thread thread = new Thread(r, "FeedDataProvider_ActionsThread_" + this.threadNumber.getAndIncrement());
/* 1792 */       if (!thread.isDaemon()) {
/* 1793 */         thread.setDaemon(true);
/*      */       }
/* 1795 */       if (thread.getPriority() != 5) {
/* 1796 */         thread.setPriority(5);
/*      */       }
/* 1798 */       return thread;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.FeedDataProvider
 * JD-Core Version:    0.6.0
 */