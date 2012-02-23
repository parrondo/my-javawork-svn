/*      */ package com.dukascopy.charts.data.datacache;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*      */ import com.dukascopy.charts.data.datacache.intraperiod.IntraperiodBarsGenerator;
/*      */ import com.dukascopy.charts.data.datacache.wrapper.PeriodOfferSideCandle;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class IntraperiodCandlesGenerator
/*      */   implements CacheDataUpdatedListener
/*      */ {
/*      */   private static final Logger LOGGER;
/*   36 */   private final Map<Instrument, Map<Period, Map<OfferSide, IntraPeriodCandleData>>> generatedCandlesMap = new HashMap();
/*      */   private ThreadPoolExecutor executorService;
/*      */   private Thread flatsGeneratorByTimeout;
/*      */   private FeedDataProvider feedDataProvider;
/*      */   private boolean testerFeedDataProvider;
/*      */   private volatile boolean stop;
/*   42 */   private Object inProgressWaitNotify = new Object();
/*      */   private final IIntraperiodBarsGenerator intraperiodBarsGenerator;
/*      */ 
/*      */   public IntraperiodCandlesGenerator(boolean testerFeedDataProvider, FeedDataProvider feedDataProvider)
/*      */   {
/*   51 */     this.feedDataProvider = feedDataProvider;
/*   52 */     if (feedDataProvider != null) {
/*   53 */       this.intraperiodBarsGenerator = new IntraperiodBarsGenerator(feedDataProvider.getPriceAggregationDataProvider());
/*      */     }
/*      */     else {
/*   56 */       this.intraperiodBarsGenerator = null;
/*      */     }
/*      */ 
/*   59 */     this.testerFeedDataProvider = testerFeedDataProvider;
/*   60 */     this.executorService = new ThreadPoolExecutor(1, 1, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
/*   61 */     this.executorService.allowCoreThreadTimeOut(true);
/*   62 */     this.flatsGeneratorByTimeout = new Thread(new Object() {
/*      */       public void run() {
/*   64 */         List formedCandles = new ArrayList();
/*   65 */         while (!IntraperiodCandlesGenerator.this.stop)
/*      */         {
/*   68 */           long currentTimeMillis = System.currentTimeMillis();
/*   69 */           int latency = IntraperiodCandlesGenerator.this.feedDataProvider == null ? 0 : IntraperiodCandlesGenerator.this.feedDataProvider.getLatency();
/*      */           long timeNow;
/*   71 */           synchronized (this) {
/*   72 */             synchronized (IntraperiodCandlesGenerator.this.generatedCandlesMap) {
/*   73 */               timeNow = currentTimeMillis;
/*   74 */               timeNow -= latency;
/*      */ 
/*   76 */               timeNow -= 1000L;
/*   77 */               formedCandles.clear();
/*   78 */               IntraperiodCandlesGenerator.this.checkCandlesForFlats(timeNow, formedCandles);
/*      */             }
/*   80 */             if (!formedCandles.isEmpty()) {
/*   81 */               if (formedCandles.size() > 1)
/*   82 */                 Collections.sort(formedCandles, new Comparator()
/*      */                 {
/*      */                   public int compare(Object[] o1, Object[] o2) {
/*   85 */                     long time1 = ((CandleData)o1[2]).time;
/*   86 */                     long time2 = ((CandleData)o2[2]).time;
/*   87 */                     return time1 == time2 ? 0 : time1 < time2 ? -1 : 1;
/*      */                   }
/*      */                 });
/*   91 */               for (Object[] formedCandle : formedCandles) {
/*   92 */                 if (IntraperiodCandlesGenerator.LOGGER.isTraceEnabled()) {
/*   93 */                   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
/*   94 */                   format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*   95 */                   IntraperiodCandlesGenerator.LOGGER.trace("Candle for instrument [" + formedCandle[0] + "] period [" + formedCandle[1] + "] at time [" + format.format(new Date(((IntraPeriodCandleData)formedCandle[2]).time)) + "] closed by timeout. Last tick time [" + format.format(new Date(IntraperiodCandlesGenerator.this.feedDataProvider.getCurrentTime())) + "], current calculated time [" + format.format(new Date(timeNow)) + "]");
/*      */                 }
/*      */ 
/*  100 */                 IntraperiodCandlesGenerator.this.fireCandlesFormed((Instrument)formedCandle[0], (Period)formedCandle[1], (IntraPeriodCandleData)formedCandle[2], (IntraPeriodCandleData)formedCandle[3]);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*  105 */           if (timeNow != -9223372036854775808L) {
/*  106 */             Period basePeriod = Period.ONE_SEC;
/*      */ 
/*  108 */             long current10SecCandleStart = DataCacheUtils.getCandleStartFast(basePeriod, timeNow);
/*  109 */             current10SecCandleStart += latency;
/*  110 */             current10SecCandleStart += basePeriod.getInterval() + 1000L;
/*      */             try {
/*  112 */               long sleepTime = current10SecCandleStart - currentTimeMillis;
/*  113 */               if (sleepTime < 0L) {
/*  114 */                 sleepTime = 1L;
/*      */               }
/*  116 */               Thread.sleep(sleepTime);
/*      */             } catch (InterruptedException e) {
/*      */             }
/*      */             catch (IllegalArgumentException e) {
/*  120 */               IntraperiodCandlesGenerator.LOGGER.error("wrong time for sleep method [" + (current10SecCandleStart - timeNow) + "], current10SecCandleStart [" + current10SecCandleStart + "], timeNow [" + timeNow + "], latency [" + latency + "]", e);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*      */             try {
/*  126 */               Thread.sleep(1000L);
/*      */             } catch (InterruptedException e) {
/*  128 */               IntraperiodCandlesGenerator.LOGGER.debug(e.getMessage(), e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     , "Flats generator by timeout");
/*      */ 
/*  134 */     this.flatsGeneratorByTimeout.setDaemon(true);
/*  135 */     if (!testerFeedDataProvider) {
/*  136 */       this.flatsGeneratorByTimeout.start();
/*      */     }
/*  138 */     if (feedDataProvider != null)
/*  139 */       for (Instrument instrument : Instrument.values())
/*  140 */         feedDataProvider.addCacheDataUpdatedListener(instrument, this);
/*      */   }
/*      */ 
/*      */   public void processTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */   {
/*  148 */     synchronized (this) {
/*  149 */       List formedCandles = new ArrayList();
/*      */       try {
/*  151 */         if (this.intraperiodBarsGenerator != null)
/*      */         {
/*  155 */           this.intraperiodBarsGenerator.processTick(instrument, new TickData(time, ask, bid, askVol, bidVol));
/*      */         }
/*      */         Map periodMap;
/*  162 */         synchronized (this.generatedCandlesMap) {
/*  163 */           periodMap = getPeriodGeneratedCandlesMap(instrument);
/*      */         }
/*      */ 
/*  166 */         if (periodMap == null) {
/*  167 */           return;
/*      */         }
/*  169 */         synchronized (periodMap) {
/*  170 */           formedCandles.clear();
/*  171 */           addTickToCandles(time, ask, bid, askVol, bidVol, instrument, periodMap, formedCandles, this);
/*      */         }
/*      */ 
/*  174 */         for (Object[] formedCandle : formedCandles) {
/*  175 */           fireCandlesFormed(instrument, (Period)formedCandle[0], (IntraPeriodCandleData)formedCandle[1], (IntraPeriodCandleData)formedCandle[2]);
/*      */         }
/*      */ 
/*  181 */         List dataToFire = new ArrayList();
/*      */         Iterator i$;
/*      */         Period period;
/*      */         Map offerSideMap;
/*  190 */         synchronized (periodMap) {
/*  191 */           for (i$ = periodMap.keySet().iterator(); i$.hasNext(); ) { period = (Period)i$.next();
/*  192 */             offerSideMap = getOfferSideGeneratedCandlesMap(instrument, period);
/*  193 */             if (offerSideMap == null) {
/*      */               continue;
/*      */             }
/*  196 */             for (OfferSide offerSide : offerSideMap.keySet()) {
/*  197 */               if (offerSideMap.get(offerSide) != null) {
/*  198 */                 PeriodOfferSideCandle data = new PeriodOfferSideCandle(period, offerSide, (CandleData)offerSideMap.get(offerSide));
/*  199 */                 dataToFire.add(data);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  208 */         for (PeriodOfferSideCandle data : dataToFire)
/*  209 */           fireInProgressCandleUpdated(instrument, data.getPeriod(), data.getOfferSide(), data.getCandleData());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  213 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void processCandle(Instrument instrument, Period period, IntraPeriodCandleData askCandle, IntraPeriodCandleData bidCandle)
/*      */   {
/*  225 */     synchronized (this.generatedCandlesMap) {
/*  226 */       processCandle(instrument, period, OfferSide.ASK, askCandle);
/*  227 */       processCandle(instrument, period, OfferSide.BID, bidCandle);
/*      */     }
/*  229 */     fireCandlesFormed(instrument, period, askCandle, bidCandle);
/*      */   }
/*      */ 
/*      */   private void processCandle(Instrument instrument, Period period, OfferSide offerSide, IntraPeriodCandleData candle)
/*      */   {
/*  238 */     if (candle == null)
/*      */     {
/*  242 */       return;
/*      */     }
/*      */ 
/*  245 */     Map offerSideMap = getOfferSideGeneratedCandlesMap(instrument, period);
/*  246 */     if (offerSideMap == null) {
/*  247 */       return;
/*      */     }
/*      */ 
/*  250 */     IntraPeriodCandleData askCandleData = (IntraPeriodCandleData)offerSideMap.get(offerSide);
/*  251 */     if (askCandleData == null)
/*      */     {
/*  253 */       askCandleData = new IntraPeriodCandleData();
/*      */ 
/*  255 */       askCandleData.time = DataCacheUtils.getNextCandleStartFast(period, candle.time);
/*      */ 
/*  257 */       makeFlat(askCandleData, candle);
/*      */ 
/*  259 */       put(offerSideMap, offerSide, askCandleData);
/*      */     } else {
/*  261 */       long currentCandleTime = candle.time;
/*      */ 
/*  263 */       if (askCandleData.time > currentCandleTime) {
/*  264 */         DataCacheException notThrownException = new DataCacheException("Last candle time is bigger than new candle time, ignoring candle update");
/*  265 */         LOGGER.error(notThrownException.getMessage(), notThrownException);
/*  266 */         return;
/*      */       }
/*  268 */       long inProgressCandleTime = askCandleData.time;
/*  269 */       if (inProgressCandleTime == currentCandleTime)
/*      */       {
/*  271 */         askCandleData = new IntraPeriodCandleData();
/*      */ 
/*  273 */         askCandleData.time = DataCacheUtils.getNextCandleStartFast(period, inProgressCandleTime);
/*  274 */         makeFlat(askCandleData, candle);
/*      */ 
/*  276 */         put(offerSideMap, offerSide, askCandleData);
/*      */       } else {
/*  278 */         LOGGER.debug("WARNING! Trying to replace non current candle");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void addTickToCandles(long time, double ask, double bid, double askVol, double bidVol, Instrument instrument, Map<Period, Map<OfferSide, IntraPeriodCandleData>> periodMap, List<Object[]> formedCandles, IntraperiodCandlesGenerator generator)
/*      */   {
/*  295 */     if (periodMap == null) {
/*  296 */       return;
/*      */     }
/*      */ 
/*  299 */     Map intProgressPeriodMap = null;
/*  300 */     for (Period period : periodMap.keySet()) {
/*  301 */       Map offerSideMap = (Map)periodMap.get(period);
/*  302 */       if (offerSideMap == null) {
/*      */         continue;
/*      */       }
/*  305 */       IntraPeriodCandleData askCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*  306 */       IntraPeriodCandleData bidCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.BID);
/*  307 */       if ((askCandleData == null) || (bidCandleData == null))
/*      */       {
/*  309 */         askCandleData = new IntraPeriodCandleData();
/*  310 */         bidCandleData = new IntraPeriodCandleData();
/*      */ 
/*  313 */         askCandleData.empty = (bidCandleData.empty = 1);
/*  314 */         askCandleData.time = (bidCandleData.time = DataCacheUtils.getCandleStartFast(period, time));
/*      */ 
/*  316 */         addTickToCandle(ask, askVol, askCandleData);
/*  317 */         addTickToCandle(bid, bidVol, bidCandleData);
/*      */ 
/*  319 */         if (intProgressPeriodMap == null) {
/*  320 */           intProgressPeriodMap = new HashMap();
/*      */         }
/*  322 */         if (intProgressPeriodMap.get(period) == null) {
/*  323 */           intProgressPeriodMap.put(period, new HashMap());
/*      */         }
/*  325 */         Map inProgressOfferSideMap = (Map)intProgressPeriodMap.get(period);
/*  326 */         put(inProgressOfferSideMap, OfferSide.ASK, askCandleData);
/*  327 */         put(inProgressOfferSideMap, OfferSide.BID, bidCandleData);
/*      */       }
/*      */       else
/*      */       {
/*  332 */         long tickCandleTime = DataCacheUtils.getCandleStartFast(period, time);
/*  333 */         if ((askCandleData.time > tickCandleTime) || (askCandleData.time != bidCandleData.time))
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/*  339 */         if (askCandleData.time >= tickCandleTime)
/*      */         {
/*  341 */           addTickToCandle(ask, askVol, askCandleData);
/*  342 */           addTickToCandle(bid, bidVol, bidCandleData);
/*      */         }
/*      */         else {
/*      */           do {
/*  346 */             long lastTime = askCandleData.time;
/*      */ 
/*  349 */             askCandleData = closeCandle(askCandleData);
/*  350 */             bidCandleData = closeCandle(bidCandleData);
/*      */ 
/*  352 */             formedCandles.add(new Object[] { period, askCandleData, bidCandleData });
/*  353 */             IntraPeriodCandleData newAskCandleData = new IntraPeriodCandleData();
/*  354 */             IntraPeriodCandleData newBidCandleData = new IntraPeriodCandleData();
/*      */ 
/*  356 */             newAskCandleData.time = (newBidCandleData.time = DataCacheUtils.getNextCandleStartFast(period, lastTime));
/*      */ 
/*  358 */             makeFlat(newAskCandleData, askCandleData);
/*  359 */             makeFlat(newBidCandleData, bidCandleData);
/*      */ 
/*  361 */             askCandleData = newAskCandleData;
/*  362 */             bidCandleData = newBidCandleData;
/*  363 */           }while (askCandleData.time < tickCandleTime);
/*      */ 
/*  365 */           assert ((askCandleData.time == tickCandleTime) && (askCandleData.time == bidCandleData.time));
/*      */ 
/*  367 */           addTickToCandle(ask, askVol, askCandleData);
/*  368 */           addTickToCandle(bid, bidVol, bidCandleData);
/*      */         }
/*      */       }
/*      */ 
/*  372 */       put(offerSideMap, OfferSide.ASK, askCandleData);
/*  373 */       put(offerSideMap, OfferSide.BID, bidCandleData);
/*      */     }
/*      */ 
/*  376 */     if ((intProgressPeriodMap != null) && (generator != null))
/*  377 */       if (generator.testerFeedDataProvider)
/*  378 */         for (Period period : intProgressPeriodMap.keySet()) {
/*  379 */           Map offerSideMap = (Map)intProgressPeriodMap.get(period);
/*      */ 
/*  381 */           if (offerSideMap != null) {
/*  382 */             IntraPeriodCandleData askCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*  383 */             IntraPeriodCandleData bidCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.BID);
/*  384 */             generator.fillInProgressBars(instrument, period, askCandleData, bidCandleData, time, false);
/*      */           }
/*      */         }
/*      */       else
/*  388 */         generator.fillInProgressBars(instrument, intProgressPeriodMap, time, false);
/*      */   }
/*      */ 
/*      */   private static IntraPeriodCandleData closeCandle(IntraPeriodCandleData candle)
/*      */   {
/*  394 */     candle.open = StratUtils.round(candle.open, 5);
/*  395 */     candle.close = StratUtils.round(candle.close, 5);
/*  396 */     candle.high = StratUtils.round(candle.high, 5);
/*  397 */     candle.low = StratUtils.round(candle.low, 5);
/*  398 */     candle.vol = StratUtils.round(candle.vol, 6);
/*  399 */     return candle;
/*      */   }
/*      */ 
/*      */   private void checkCandlesForFlats(long time, List<Object[]> formedCandles)
/*      */   {
/*      */     Instrument instrument;
/*      */     Map periodMap;
/*  403 */     for (instrument : Instrument.values()) {
/*  404 */       periodMap = getPeriodGeneratedCandlesMap(instrument);
/*  405 */       if (periodMap == null)
/*      */       {
/*      */         continue;
/*      */       }
/*  409 */       for (Period period : periodMap.keySet()) {
/*  410 */         Map offerSideMap = (Map)periodMap.get(period);
/*      */ 
/*  412 */         if (offerSideMap == null)
/*      */         {
/*      */           continue;
/*      */         }
/*  416 */         IntraPeriodCandleData askCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*  417 */         IntraPeriodCandleData bidCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.BID);
/*      */ 
/*  419 */         if ((askCandleData != null) && (bidCandleData != null))
/*      */         {
/*  423 */           long lastTime = askCandleData.time;
/*  424 */           long currentCandleTime = DataCacheUtils.getCandleStartFast(period, time);
/*  425 */           if (lastTime < currentCandleTime)
/*      */           {
/*      */             do
/*      */             {
/*  429 */               lastTime = askCandleData.time;
/*      */ 
/*  432 */               askCandleData = closeCandle(askCandleData);
/*  433 */               bidCandleData = closeCandle(bidCandleData);
/*      */ 
/*  435 */               formedCandles.add(new Object[] { instrument, period, askCandleData, bidCandleData });
/*  436 */               IntraPeriodCandleData newAskCandleData = new IntraPeriodCandleData();
/*  437 */               IntraPeriodCandleData newBidCandleData = new IntraPeriodCandleData();
/*      */ 
/*  439 */               newAskCandleData.time = (newBidCandleData.time = DataCacheUtils.getNextCandleStartFast(period, lastTime));
/*  440 */               makeFlat(newAskCandleData, askCandleData);
/*  441 */               makeFlat(newBidCandleData, bidCandleData);
/*      */ 
/*  443 */               askCandleData = newAskCandleData;
/*  444 */               bidCandleData = newBidCandleData;
/*  445 */             }while (askCandleData.time < currentCandleTime);
/*  446 */             assert ((askCandleData.time == currentCandleTime) && (askCandleData.time == bidCandleData.time));
/*      */ 
/*  448 */             put(offerSideMap, OfferSide.ASK, askCandleData);
/*  449 */             put(offerSideMap, OfferSide.BID, bidCandleData);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void cacheUpdated(Instrument instrument, long from, long to)
/*      */   {
/*  459 */     Map periodMap = getPeriodGeneratedCandlesMap(instrument);
/*  460 */     if (periodMap != null) {
/*  461 */       Map periodMapToProcess = new HashMap();
/*      */ 
/*  463 */       boolean process = false;
/*  464 */       for (Period period : periodMap.keySet()) {
/*  465 */         boolean fill = false;
/*      */         Map offerSideMap;
/*  467 */         synchronized (this.generatedCandlesMap) {
/*  468 */           offerSideMap = getOfferSideGeneratedCandlesMap(instrument, period);
/*  469 */           if (offerSideMap != null) {
/*  470 */             IntraPeriodCandleData askIntraPeriodCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*      */ 
/*  472 */             if (askIntraPeriodCandleData == null)
/*      */             {
/*      */               continue;
/*      */             }
/*  476 */             if (to > askIntraPeriodCandleData.time) {
/*  477 */               fill = true;
/*      */             }
/*      */           }
/*      */         }
/*  481 */         if (fill) {
/*  482 */           periodMapToProcess.put(period, offerSideMap);
/*  483 */           process = true;
/*      */         }
/*      */       }
/*  486 */       if (process)
/*  487 */         fillInProgressBars(instrument, periodMapToProcess, -9223372036854775808L, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fillInProgressBars(Instrument instrument, Map<Period, Map<OfferSide, IntraPeriodCandleData>> periodMap, long to, boolean refill)
/*      */   {
/*  498 */     if (this.feedDataProvider == null)
/*      */     {
/*  500 */       return;
/*      */     }
/*      */ 
/*  503 */     this.executorService.execute(new Object(to, refill, instrument, periodMap) {
/*  504 */       private long toTime = this.val$to;
/*      */ 
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*      */           TickData tick;
/*  508 */           if (this.val$refill)
/*      */           {
/*  510 */             tick = IntraperiodCandlesGenerator.this.feedDataProvider.getLastTick(this.val$instrument);
/*  511 */             if (tick == null)
/*      */             {
/*  513 */               return;
/*      */             }
/*  515 */             for (Period period : this.val$periodMap.keySet()) {
/*  516 */               Map offerSideMap = (Map)this.val$periodMap.get(period);
/*      */ 
/*  518 */               IntraPeriodCandleData askIntraPeriodCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*  519 */               IntraPeriodCandleData bidIntraPeriodCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*      */ 
/*  521 */               if ((askIntraPeriodCandleData == null) || (bidIntraPeriodCandleData == null))
/*      */               {
/*      */                 continue;
/*      */               }
/*  525 */               askIntraPeriodCandleData.setVolume(tick.askVol);
/*  526 */               bidIntraPeriodCandleData.setVolume(tick.bidVol);
/*      */ 
/*  528 */               if (this.toTime > DataCacheUtils.getNextCandleStartFast(period, askIntraPeriodCandleData.time))
/*      */               {
/*  530 */                 IntraperiodCandlesGenerator.this.feedDataProvider.fireCacheDataChanged(this.val$instrument, -9223372036854775808L, -9223372036854775808L);
/*  531 */                 return;
/*      */               }
/*      */ 
/*  534 */               this.toTime = tick.time;
/*      */             }
/*      */           }
/*  537 */           Map candles = new HashMap();
/*  538 */           int[] result = { 0 };
/*  539 */           Exception[] exceptions = new Exception[1];
/*  540 */           LoadingProgressListener loadingProgressListener = new LoadingProgressListener(result, exceptions) {
/*      */             public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */             }
/*      */ 
/*      */             public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  545 */               this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  546 */               this.val$exceptions[0] = e;
/*      */             }
/*      */ 
/*      */             public boolean stopJob() {
/*  550 */               return IntraperiodCandlesGenerator.this.stop;
/*      */             }
/*      */           };
/*  553 */           LiveFeedListener feedListener = new LiveFeedListener(candles) {
/*      */             public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*      */             }
/*      */ 
/*      */             public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  558 */               CandleData[] data = (CandleData[])this.val$candles.get(period);
/*  559 */               if (data == null) {
/*  560 */                 data = new CandleData[2];
/*  561 */                 this.val$candles.put(period, data);
/*      */               }
/*  563 */               data[(side == OfferSide.ASK ? 0 : 1)] = new CandleData(time, open, close, low, high, vol);
/*      */             }
/*      */           };
/*  566 */           if (IntraperiodCandlesGenerator.this.stop) {
/*  567 */             return;
/*      */           }
/*  569 */           result[0] = 0;
/*  570 */           candles.clear();
/*  571 */           IntraperiodCandlesGenerator.this.feedDataProvider.loadInProgressCandleDataSynched(this.val$instrument, this.toTime, feedListener, loadingProgressListener);
/*  572 */           if (exceptions[0] != null) {
/*  573 */             IntraperiodCandlesGenerator.LOGGER.error(exceptions[0].getMessage(), exceptions[0]);
/*      */           }
/*      */ 
/*  576 */           for (Period period : this.val$periodMap.keySet()) {
/*  577 */             if ((Period.isPeriodBasic(period) != null) && (result[0] != 2)) {
/*      */               continue;
/*      */             }
/*  580 */             Map offerSideMap = (Map)this.val$periodMap.get(period);
/*      */ 
/*  582 */             if (offerSideMap != null) {
/*  583 */               IntraPeriodCandleData askCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*  584 */               IntraPeriodCandleData bidCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.BID);
/*  585 */               IntraperiodCandlesGenerator.this.fillInProgressBars(this.val$instrument, period, askCandleData, bidCandleData, this.val$to, this.val$refill);
/*      */             }
/*      */           }
/*      */ 
/*  589 */           if (result[0] == 2) {
/*  590 */             return;
/*      */           }
/*  592 */           if (candles.isEmpty()) {
/*  593 */             return;
/*      */           }
/*  595 */           boolean reload = false;
/*      */ 
/*  597 */           for (Period period : this.val$periodMap.keySet()) {
/*  598 */             CandleData[] data = (CandleData[])candles.get(period);
/*  599 */             Map offerSideMap = (Map)this.val$periodMap.get(period);
/*  600 */             if (offerSideMap != null) {
/*  601 */               IntraPeriodCandleData askCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK);
/*  602 */               IntraPeriodCandleData bidCandleData = (IntraPeriodCandleData)offerSideMap.get(OfferSide.BID);
/*      */ 
/*  604 */               if ((data == null) || (askCandleData == null) || (bidCandleData == null))
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/*  612 */               IntraperiodCandlesGenerator.access$600(askCandleData, data[0]);
/*  613 */               IntraperiodCandlesGenerator.access$600(bidCandleData, data[1]);
/*      */ 
/*  615 */               askCandleData.empty = false;
/*  616 */               bidCandleData.empty = false;
/*      */ 
/*  618 */               if (Period.isPeriodBasic(period) != null) {
/*  619 */                 IntraperiodCandlesGenerator.this.feedDataProvider.getLocalCacheManager().addIntraPeriodCandle(this.val$instrument, period, OfferSide.ASK, askCandleData);
/*  620 */                 IntraperiodCandlesGenerator.this.feedDataProvider.getLocalCacheManager().addIntraPeriodCandle(this.val$instrument, period, OfferSide.BID, bidCandleData);
/*      */               }
/*      */ 
/*  623 */               synchronized (IntraperiodCandlesGenerator.this.inProgressWaitNotify)
/*      */               {
/*  625 */                 IntraperiodCandlesGenerator.this.inProgressWaitNotify.notifyAll();
/*      */               }
/*      */ 
/*  629 */               synchronized (this) {
/*  630 */                 CandleData inProgressCandle = IntraperiodCandlesGenerator.this.getInProgressCandle(this.val$instrument, period, OfferSide.ASK);
/*  631 */                 long candleDataTime = askCandleData.time;
/*      */ 
/*  633 */                 if ((!reload) && (inProgressCandle != null) && (candleDataTime >= inProgressCandle.time)) {
/*  634 */                   IntraperiodCandlesGenerator.this.feedDataProvider.fireInProgressCandleUpdated(this.val$instrument, period, OfferSide.ASK, askCandleData);
/*  635 */                   IntraperiodCandlesGenerator.this.feedDataProvider.fireInProgressCandleUpdated(this.val$instrument, period, OfferSide.BID, bidCandleData);
/*      */                 } else {
/*  637 */                   reload = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*  642 */           if (reload)
/*  643 */             IntraperiodCandlesGenerator.this.feedDataProvider.fireCacheDataChanged(this.val$instrument, -9223372036854775808L, -9223372036854775808L);
/*      */         }
/*      */         catch (DataCacheException e) {
/*  646 */           IntraperiodCandlesGenerator.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void fillInProgressBars(Instrument instrument, Period period, IntraPeriodCandleData askCandleData, IntraPeriodCandleData bidCandleData, long to, boolean refill)
/*      */   {
/*  661 */     if (this.feedDataProvider == null)
/*      */     {
/*  663 */       return;
/*      */     }
/*      */ 
/*  666 */     this.executorService.execute(new Object(to, refill, instrument, askCandleData, bidCandleData, period) {
/*  667 */       private long toTime = this.val$to;
/*      */ 
/*      */       public void run() {
/*      */         try {
/*  671 */           if (this.val$refill)
/*      */           {
/*  673 */             TickData tick = IntraperiodCandlesGenerator.this.feedDataProvider.getLastTick(this.val$instrument);
/*  674 */             if (tick == null)
/*      */             {
/*  676 */               return;
/*      */             }
/*  678 */             this.val$askCandleData.setVolume(tick.askVol);
/*  679 */             this.val$bidCandleData.setVolume(tick.bidVol);
/*      */ 
/*  681 */             this.toTime = tick.time;
/*      */           }
/*  683 */           if (this.toTime > DataCacheUtils.getNextCandleStartFast(this.val$period, this.val$askCandleData.time))
/*      */           {
/*  685 */             IntraperiodCandlesGenerator.this.feedDataProvider.fireCacheDataChanged(this.val$instrument, -9223372036854775808L, -9223372036854775808L);
/*  686 */             return;
/*      */           }
/*  688 */           DataCacheUtils.ToLoad[] intervalsToLoad = DataCacheUtils.getIntervalsToLoadForCandleFilling(this.val$period, this.toTime);
/*  689 */           List candles = new ArrayList();
/*  690 */           List ticks = new ArrayList();
/*  691 */           int[] result = { 0 };
/*  692 */           Exception[] exceptions = new Exception[1];
/*  693 */           LoadingProgressListener loadingProgressListener = new LoadingProgressListener(result, exceptions) {
/*      */             public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */             }
/*      */ 
/*      */             public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e) {
/*  698 */               this.val$result[0] = (allDataLoaded ? 1 : 2);
/*  699 */               this.val$exceptions[0] = e;
/*      */             }
/*      */ 
/*      */             public boolean stopJob() {
/*  703 */               return IntraperiodCandlesGenerator.this.stop;
/*      */             }
/*      */           };
/*  706 */           LiveFeedListener feedListener = new LiveFeedListener(ticks, candles) {
/*      */             public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*  708 */               this.val$ticks.add(new TickData(time, ask, bid, askVol, bidVol));
/*      */             }
/*      */ 
/*      */             public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
/*  712 */               this.val$candles.add(new CandleData(time, open, close, low, high, vol));
/*      */             }
/*      */           };
/*  715 */           boolean firstBidCandle = true;
/*  716 */           boolean firstAskCandle = true;
/*  717 */           for (DataCacheUtils.ToLoad toLoad : intervalsToLoad) {
/*  718 */             if (IntraperiodCandlesGenerator.this.stop) {
/*  719 */               return;
/*      */             }
/*  721 */             if (toLoad.period != Period.TICK) {
/*  722 */               candles.clear();
/*  723 */               IntraperiodCandlesGenerator.this.feedDataProvider.loadCandlesDataSynched(this.val$instrument, toLoad.period, OfferSide.ASK, toLoad.from, toLoad.to, feedListener, loadingProgressListener);
/*  724 */               if (result[0] == 2) {
/*  725 */                 if (exceptions[0] != null) {
/*  726 */                   IntraperiodCandlesGenerator.LOGGER.error(exceptions[0].getMessage(), exceptions[0]);
/*      */                 }
/*  728 */                 return;
/*      */               }
/*  730 */               if (candles.isEmpty()) {
/*      */                 continue;
/*      */               }
/*  733 */               firstAskCandle = IntraperiodCandlesGenerator.access$800(this.val$askCandleData, candles, firstAskCandle);
/*  734 */               result[0] = 0;
/*  735 */               exceptions[0] = null;
/*  736 */               candles.clear();
/*  737 */               IntraperiodCandlesGenerator.this.feedDataProvider.loadCandlesDataSynched(this.val$instrument, toLoad.period, OfferSide.BID, toLoad.from, toLoad.to, feedListener, loadingProgressListener);
/*  738 */               if (result[0] == 2) {
/*  739 */                 if (exceptions[0] != null) {
/*  740 */                   IntraperiodCandlesGenerator.LOGGER.error(exceptions[0].getMessage(), exceptions[0]);
/*      */                 }
/*  742 */                 return;
/*      */               }
/*  744 */               firstBidCandle = IntraperiodCandlesGenerator.access$800(this.val$bidCandleData, candles, firstBidCandle);
/*      */             } else {
/*  746 */               ticks.clear();
/*  747 */               IntraperiodCandlesGenerator.this.feedDataProvider.loadTicksDataSynched(this.val$instrument, toLoad.from, toLoad.to, feedListener, loadingProgressListener);
/*  748 */               if (result[0] == 2) {
/*  749 */                 return;
/*      */               }
/*  751 */               if (ticks.isEmpty())
/*      */               {
/*      */                 continue;
/*      */               }
/*  755 */               IntraperiodCandlesGenerator.access$900(this.val$askCandleData, ticks, firstAskCandle, true);
/*  756 */               IntraperiodCandlesGenerator.access$900(this.val$bidCandleData, ticks, firstBidCandle, false);
/*      */ 
/*  758 */               firstAskCandle = false;
/*  759 */               firstBidCandle = false;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  765 */           this.val$askCandleData.empty = false;
/*  766 */           this.val$bidCandleData.empty = false;
/*      */ 
/*  768 */           synchronized (IntraperiodCandlesGenerator.this.inProgressWaitNotify)
/*      */           {
/*  770 */             IntraperiodCandlesGenerator.this.inProgressWaitNotify.notifyAll();
/*      */           }
/*      */ 
/*  773 */           if (Period.isPeriodBasic(this.val$period) != null) {
/*  774 */             IntraperiodCandlesGenerator.this.feedDataProvider.getLocalCacheManager().addIntraPeriodCandle(this.val$instrument, this.val$period, OfferSide.ASK, this.val$askCandleData);
/*  775 */             IntraperiodCandlesGenerator.this.feedDataProvider.getLocalCacheManager().addIntraPeriodCandle(this.val$instrument, this.val$period, OfferSide.BID, this.val$bidCandleData);
/*      */           }
/*      */ 
/*  779 */           synchronized (this) {
/*  780 */             CandleData inProgressCandle = IntraperiodCandlesGenerator.this.getInProgressCandle(this.val$instrument, this.val$period, OfferSide.ASK);
/*  781 */             long candleDataTime = this.val$askCandleData.time;
/*      */ 
/*  783 */             if (inProgressCandle != null)
/*  784 */               if (candleDataTime < inProgressCandle.time) {
/*  785 */                 IntraperiodCandlesGenerator.this.feedDataProvider.fireCacheDataChanged(this.val$instrument, -9223372036854775808L, -9223372036854775808L);
/*      */               } else {
/*  787 */                 IntraperiodCandlesGenerator.this.feedDataProvider.fireInProgressCandleUpdated(this.val$instrument, this.val$period, OfferSide.ASK, this.val$askCandleData);
/*  788 */                 IntraperiodCandlesGenerator.this.feedDataProvider.fireInProgressCandleUpdated(this.val$instrument, this.val$period, OfferSide.BID, this.val$bidCandleData);
/*      */               }
/*      */           }
/*      */         }
/*      */         catch (DataCacheException e) {
/*  793 */           IntraperiodCandlesGenerator.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       } } );
/*      */   }
/*      */ 
/*      */   private static void addInProgressDataCandleToCandle(IntraPeriodCandleData candleData, CandleData candle) {
/*  800 */     if ((candleData == null) || (candle == null)) {
/*  801 */       return;
/*      */     }
/*      */ 
/*  804 */     if ((candleData.open != candleData.close) || (candleData.close != candleData.high) || (candleData.high != candleData.low) || (candleData.vol != 0.0D))
/*      */     {
/*  810 */       candleData.open = candle.open;
/*      */ 
/*  812 */       candleData.flat = false;
/*      */ 
/*  814 */       candleData.high = (candleData.high < candle.high ? candle.high : candleData.high);
/*  815 */       candleData.low = (candleData.low > candle.low ? candle.low : candleData.low);
/*      */ 
/*  817 */       candleData.vol = StratUtils.round(candleData.vol + candle.vol, 6);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static boolean addCandlesToCandle(IntraPeriodCandleData candleData, List<CandleData> candles, boolean firstCandle) {
/*  822 */     for (CandleData candle : candles) {
/*  823 */       if ((candleData.open != candleData.close) || (candleData.close != candleData.high) || (candleData.high != candleData.low) || (candleData.vol != 0.0D))
/*      */       {
/*  829 */         if (firstCandle)
/*      */         {
/*  831 */           candleData.open = candle.open;
/*  832 */           candleData.high = candle.high;
/*  833 */           candleData.low = candle.low;
/*      */ 
/*  835 */           candleData.flat = false;
/*  836 */           firstCandle = false;
/*      */         }
/*      */ 
/*  839 */         candleData.high = (candleData.high < candle.high ? candle.high : candleData.high);
/*  840 */         candleData.low = (candleData.low > candle.low ? candle.low : candleData.low);
/*      */ 
/*  842 */         candleData.vol = StratUtils.round(candleData.vol + candle.vol, 6);
/*      */       }
/*      */     }
/*  845 */     return firstCandle;
/*      */   }
/*      */ 
/*      */   private static void addTicksToCandle(IntraPeriodCandleData candleData, List<TickData> ticks, boolean firstCandle, boolean ask) {
/*  849 */     for (TickData tick : ticks) {
/*  850 */       double price = ask ? tick.ask : tick.bid;
/*  851 */       if (firstCandle)
/*      */       {
/*  853 */         candleData.open = price;
/*  854 */         candleData.high = price;
/*  855 */         candleData.low = price;
/*      */ 
/*  857 */         candleData.flat = false;
/*  858 */         firstCandle = false;
/*      */       }
/*      */ 
/*  861 */       candleData.high = (candleData.high < price ? price : candleData.high);
/*  862 */       candleData.low = (candleData.low > price ? price : candleData.low);
/*      */ 
/*  864 */       candleData.vol = StratUtils.round(candleData.vol + (ask ? tick.askVol : tick.bidVol), 6);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void addTickToCandle(double price, double vol, IntraPeriodCandleData candleData) {
/*  869 */     if (candleData.flat)
/*      */     {
/*  871 */       candleData.open = price;
/*  872 */       candleData.high = price;
/*  873 */       candleData.low = price;
/*      */ 
/*  875 */       candleData.flat = false;
/*      */     }
/*      */ 
/*  878 */     candleData.high = (candleData.high < price ? price : candleData.high);
/*  879 */     candleData.low = (candleData.low > price ? price : candleData.low);
/*      */ 
/*  881 */     candleData.vol = StratUtils.round(candleData.vol + vol, 6);
/*      */ 
/*  884 */     candleData.close = price;
/*      */   }
/*      */ 
/*      */   private static void makeFlat(IntraPeriodCandleData newCandleData, IntraPeriodCandleData candleData) {
/*  888 */     newCandleData.flat = true;
/*  889 */     newCandleData.open = candleData.close;
/*  890 */     newCandleData.high = candleData.close;
/*  891 */     newCandleData.low = candleData.close;
/*  892 */     newCandleData.vol = 0.0D;
/*  893 */     newCandleData.close = candleData.close;
/*      */   }
/*      */ 
/*      */   public void addInstrument(Instrument instrument) {
/*  897 */     synchronized (this.generatedCandlesMap)
/*      */     {
/*  902 */       List periods = DataCacheUtils.getOldBasicPeriods();
/*  903 */       for (Period period : periods)
/*  904 */         if (period != Period.TICK) {
/*  905 */           putIntraPeriodCandleData(instrument, period, OfferSide.ASK, null);
/*  906 */           putIntraPeriodCandleData(instrument, period, OfferSide.BID, null);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeInstrument(Instrument instrument)
/*      */   {
/*  913 */     synchronized (this.generatedCandlesMap) {
/*  914 */       freePeriodMap((Map)this.generatedCandlesMap.get(instrument));
/*  915 */       this.generatedCandlesMap.remove(instrument);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void fireInProgressCandleUpdated(Instrument instrument, Period period, OfferSide side, CandleData candle) {
/*  920 */     this.feedDataProvider.fireInProgressCandleUpdated(instrument, period, side, candle);
/*      */   }
/*      */ 
/*      */   public CandleData getInProgressCandle(Instrument instrument, Period period, OfferSide side) {
/*  924 */     synchronized (this.generatedCandlesMap) {
/*  925 */       IntraPeriodCandleData candle = getIntraPeriodCandleData(instrument, period, side);
/*  926 */       if (candle != null) {
/*  927 */         return candle.empty ? null : candle.clone();
/*      */       }
/*  929 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public CandleData getInProgressCandleBlocking(Instrument instrument, Period period, OfferSide side) throws DataCacheException
/*      */   {
/*  935 */     long start = System.currentTimeMillis();
/*  936 */     long timeout = 120000L;
/*  937 */     while (start + timeout > System.currentTimeMillis()) {
/*  938 */       synchronized (this.generatedCandlesMap) {
/*  939 */         Map sides = getOfferSideGeneratedCandlesMap(instrument, period);
/*  940 */         if (sides == null)
/*      */         {
/*  942 */           return null;
/*      */         }
/*  944 */         IntraPeriodCandleData candle = (IntraPeriodCandleData)sides.get(side);
/*  945 */         if (candle != null) {
/*  946 */           candle = candle.empty ? null : (IntraPeriodCandleData)candle.clone();
/*      */         }
/*  948 */         if (candle != null) {
/*  949 */           return candle;
/*      */         }
/*      */       }
/*  952 */       synchronized (this.inProgressWaitNotify) {
/*  953 */         long timeToWait = start + timeout - System.currentTimeMillis();
/*  954 */         if (timeToWait > 200L) {
/*  955 */           timeToWait = 200L;
/*      */         }
/*  957 */         if (timeToWait > 0L)
/*      */           try {
/*  959 */             this.inProgressWaitNotify.wait(timeToWait);
/*      */           }
/*      */           catch (InterruptedException e)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*  966 */     throw new DataCacheException("Failed to load in-progress candle data in timeout period");
/*      */   }
/*      */ 
/*      */   protected void fireCandlesFormed(Instrument instrument, Period period, IntraPeriodCandleData askCandleData, IntraPeriodCandleData bidCandleData) {
/*      */     try {
/*  971 */       if (Period.isPeriodBasic(period) != null) {
/*  972 */         if (askCandleData != null) {
/*  973 */           this.feedDataProvider.getLocalCacheManager().addIntraPeriodCandle(instrument, period, OfferSide.ASK, askCandleData);
/*      */         }
/*  975 */         if (bidCandleData != null) {
/*  976 */           this.feedDataProvider.getLocalCacheManager().addIntraPeriodCandle(instrument, period, OfferSide.BID, bidCandleData);
/*      */         }
/*      */       }
/*  979 */       if (LOGGER.isTraceEnabled()) {
/*  980 */         LOGGER.trace(new StringBuilder().append("Added candle ").append(instrument).append(" period ").append(period).append(" time ").append(askCandleData != null ? askCandleData.time : bidCandleData.time).toString());
/*      */       }
/*  982 */       this.feedDataProvider.fireCandlesFormed(instrument, period, askCandleData, bidCandleData);
/*      */     } catch (Throwable t) {
/*  984 */       LOGGER.error(t.getMessage(), t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/*  989 */     processTick(instrument, time, ask, bid, askVol, bidVol);
/*      */   }
/*      */ 
/*      */   public void stop() {
/*  993 */     this.stop = true;
/*  994 */     this.flatsGeneratorByTimeout.interrupt();
/*      */   }
/*      */ 
/*      */   private Map<Period, Map<OfferSide, IntraPeriodCandleData>> getPeriodGeneratedCandlesMap(Instrument instrument)
/*      */   {
/*  999 */     return (Map)this.generatedCandlesMap.get(instrument);
/*      */   }
/*      */ 
/*      */   private Map<OfferSide, IntraPeriodCandleData> getOfferSideGeneratedCandlesMap(Instrument instrument, Period period) {
/* 1003 */     Map periodMap = getPeriodGeneratedCandlesMap(instrument);
/* 1004 */     if (periodMap == null) {
/* 1005 */       return null;
/*      */     }
/* 1007 */     return (Map)periodMap.get(period);
/*      */   }
/*      */ 
/*      */   private IntraPeriodCandleData getIntraPeriodCandleData(Instrument instrument, Period period, OfferSide offerSide) {
/* 1011 */     Map offerSideMap = getOfferSideGeneratedCandlesMap(instrument, period);
/* 1012 */     if (offerSideMap == null) {
/* 1013 */       return null;
/*      */     }
/* 1015 */     return (IntraPeriodCandleData)offerSideMap.get(offerSide);
/*      */   }
/*      */ 
/*      */   private void putIntraPeriodCandleData(Instrument instrument, Period period, OfferSide offerSide, IntraPeriodCandleData intraPeriodCandleData)
/*      */   {
/* 1024 */     Map periodMap = getPeriodGeneratedCandlesMap(instrument);
/* 1025 */     if (periodMap == null) {
/* 1026 */       this.generatedCandlesMap.put(instrument, new HashMap());
/* 1027 */       periodMap = getPeriodGeneratedCandlesMap(instrument);
/*      */     }
/* 1029 */     Map offerSideMap = getOfferSideGeneratedCandlesMap(instrument, period);
/* 1030 */     if (offerSideMap == null) {
/* 1031 */       synchronized (periodMap) {
/* 1032 */         periodMap.put(period, new HashMap());
/*      */       }
/* 1034 */       offerSideMap = getOfferSideGeneratedCandlesMap(instrument, period);
/*      */     }
/* 1036 */     put(offerSideMap, offerSide, intraPeriodCandleData);
/*      */   }
/*      */ 
/*      */   private static void freePeriodMap(Map<Period, Map<OfferSide, IntraPeriodCandleData>> periodMap) {
/* 1040 */     if (periodMap == null) {
/* 1041 */       return;
/*      */     }
/* 1043 */     for (Period period : periodMap.keySet()) {
/* 1044 */       freeOfferSideMap((Map)periodMap.get(period));
/*      */     }
/* 1046 */     periodMap.clear();
/*      */   }
/*      */ 
/*      */   private static void freeOfferSideMap(Map<OfferSide, IntraPeriodCandleData> offerSideMap) {
/* 1050 */     if (offerSideMap == null) {
/* 1051 */       return;
/*      */     }
/* 1053 */     offerSideMap.clear();
/*      */   }
/*      */ 
/*      */   public static void addTickToCandles(long time, double ask, double bid, double askVolume, double bidVolume, Instrument instrument, IntraPeriodCandleData[][] generatedCandles, List<Object[]> formedCandles, IntraperiodCandlesGenerator generator)
/*      */   {
/* 1067 */     Map periodMap = new HashMap();
/*      */ 
/* 1069 */     for (int i = generatedCandles.length - 1; i >= 0; i--) {
/* 1070 */       IntraPeriodCandleData[] candleData = generatedCandles[i];
/* 1071 */       if (candleData == null) {
/*      */         continue;
/*      */       }
/* 1074 */       Period period = Period.values()[i];
/* 1075 */       Map offerSideMap = new HashMap();
/* 1076 */       periodMap.put(period, offerSideMap);
/*      */ 
/* 1078 */       if (candleData.length > 0) {
/* 1079 */         put(offerSideMap, OfferSide.ASK, candleData[0]);
/* 1080 */         put(offerSideMap, OfferSide.BID, candleData[1]);
/*      */       } else {
/* 1082 */         put(offerSideMap, OfferSide.ASK, null);
/* 1083 */         put(offerSideMap, OfferSide.BID, null);
/*      */       }
/*      */     }
/*      */ 
/* 1087 */     addTickToCandles(time, ask, bid, askVolume, bidVolume, instrument, periodMap, formedCandles, generator);
/*      */ 
/* 1104 */     for (int i = generatedCandles.length - 1; i >= 0; i--) {
/* 1105 */       if (generatedCandles[i] == null) {
/*      */         continue;
/*      */       }
/* 1108 */       Period period = Period.values()[i];
/* 1109 */       Map offerSideMap = (Map)periodMap.get(period);
/* 1110 */       if (offerSideMap == null) {
/*      */         continue;
/*      */       }
/* 1113 */       if (generatedCandles[i].length > 0) {
/* 1114 */         generatedCandles[i][0] = ((IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK));
/* 1115 */         generatedCandles[i][1] = ((IntraPeriodCandleData)offerSideMap.get(OfferSide.BID));
/*      */       } else {
/* 1117 */         generatedCandles[i] = { (IntraPeriodCandleData)offerSideMap.get(OfferSide.ASK), (IntraPeriodCandleData)offerSideMap.get(OfferSide.BID) };
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void inProgressCandleListenerAdded(Instrument instrument, Period period, OfferSide side, LiveFeedListener listener) {
/* 1123 */     synchronized (this.generatedCandlesMap) {
/* 1124 */       if (getIntraPeriodCandleData(instrument, period, side) == null) {
/* 1125 */         putIntraPeriodCandleData(instrument, period, OfferSide.ASK, null);
/* 1126 */         putIntraPeriodCandleData(instrument, period, OfferSide.BID, null);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void inProgressCandleListenerRemoved(Instrument instrument, Period period, OfferSide side, LiveFeedListener listener)
/*      */   {
/*      */     Map periodMap;
/* 1133 */     synchronized (this.generatedCandlesMap) {
/* 1134 */       periodMap = getPeriodGeneratedCandlesMap(instrument);
/*      */     }
/*      */ 
/* 1137 */     if ((periodMap != null) && (!this.feedDataProvider.isPeriodSubscribedInProgressCandle(instrument, period)) && (Period.isPeriodBasic(period) == null))
/*      */     {
/* 1142 */       synchronized (periodMap) {
/* 1143 */         periodMap.remove(period);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void put(Map<OfferSide, IntraPeriodCandleData> offerSideMap, OfferSide offerSide, IntraPeriodCandleData data) {
/* 1149 */     offerSideMap.put(offerSide, data);
/*      */   }
/*      */ 
/*      */   public IIntraperiodBarsGenerator getIntraperiodBarsGenerator() {
/* 1153 */     return this.intraperiodBarsGenerator;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   34 */     LOGGER = LoggerFactory.getLogger(IntraperiodCandlesGenerator.class);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.IntraperiodCandlesGenerator
 * JD-Core Version:    0.6.0
 */