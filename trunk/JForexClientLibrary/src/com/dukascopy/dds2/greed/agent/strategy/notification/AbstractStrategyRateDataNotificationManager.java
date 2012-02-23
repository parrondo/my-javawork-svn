/*     */ package com.dukascopy.dds2.greed.agent.strategy.notification;
/*     */ 
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.impl.TimedData;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractStrategyRateDataNotificationManager<FORMED_LISTENER, UPDATED_LISTENER, LISTENER_WRAPPER extends AbstractLiveBarFeedListenerWrapper<FORMED_LISTENER, UPDATED_LISTENER, TD>, BAR extends IBar, TD extends TimedData>
/*     */   implements IStrategyNotificationManager<FORMED_LISTENER, BAR>
/*     */ {
/*  42 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStrategyRateDataNotificationManager.class);
/*     */   private final FeedDataProvider feedDataProvider;
/*     */   private Map<IStrategy, List<LISTENER_WRAPPER>> liveFeedListenersMap;
/*     */   private Map<IStrategy, List<LISTENER_WRAPPER>> historicalFeedListenersMap;
/*     */ 
/*     */   public AbstractStrategyRateDataNotificationManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  51 */     this.feedDataProvider = feedDataProvider;
/*     */   }
/*     */ 
/*     */   protected abstract String validateJForexPeriodFields(JForexPeriod paramJForexPeriod);
/*     */ 
/*     */   protected abstract LISTENER_WRAPPER createListenerWrapper(Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, FORMED_LISTENER paramFORMED_LISTENER, IStrategyExceptionHandler paramIStrategyExceptionHandler, JForexTaskManager paramJForexTaskManager, StrategyProcessor paramStrategyProcessor, INotificationUtils paramINotificationUtils);
/*     */ 
/*     */   protected abstract IDataLoadingThread<TD> createDataLoadingThread(IStrategyRunner paramIStrategyRunner, Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, ArrayBlockingQueue<TD> paramArrayBlockingQueue);
/*     */ 
/*     */   protected abstract void addLiveInProgressBarListener(Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, LISTENER_WRAPPER paramLISTENER_WRAPPER);
/*     */ 
/*     */   protected abstract void removeLiveInProgressBarListener(LISTENER_WRAPPER paramLISTENER_WRAPPER);
/*     */ 
/*     */   protected abstract void onBar(LISTENER_WRAPPER paramLISTENER_WRAPPER, Instrument paramInstrument, JForexPeriod paramJForexPeriod, OfferSide paramOfferSide, BAR paramBAR);
/*     */ 
/*     */   protected void validateInstrument(Set<Instrument> instruments, Instrument instrument)
/*     */   {
/* 102 */     boolean found = false;
/* 103 */     for (Instrument instr : instruments) {
/* 104 */       if (instr.equals(instrument)) {
/* 105 */         found = true;
/* 106 */         break;
/*     */       }
/*     */     }
/* 109 */     if (!found)
/* 110 */       throw new IllegalArgumentException("Could not load data for Instrument=" + instrument + " because this Instrument isn't subscribed!");
/*     */   }
/*     */ 
/*     */   protected FeedDataProvider getFeedDataProvider()
/*     */   {
/* 115 */     return this.feedDataProvider;
/*     */   }
/*     */ 
/*     */   protected Map<IStrategy, List<LISTENER_WRAPPER>> getLiveFeedListenersMap() {
/* 119 */     if (this.liveFeedListenersMap == null) {
/* 120 */       this.liveFeedListenersMap = new Hashtable();
/*     */     }
/* 122 */     return this.liveFeedListenersMap;
/*     */   }
/*     */ 
/*     */   protected Map<IStrategy, List<LISTENER_WRAPPER>> getHistoricalFeedListenersMap() {
/* 126 */     if (this.historicalFeedListenersMap == null) {
/* 127 */       this.historicalFeedListenersMap = new Hashtable();
/*     */     }
/* 129 */     return this.historicalFeedListenersMap;
/*     */   }
/*     */ 
/*     */   protected LISTENER_WRAPPER subscribeToBarsFeed(IStrategy strategy, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, Map<IStrategy, List<LISTENER_WRAPPER>> map, FORMED_LISTENER barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/* 144 */     if ((barFeedListener == null) || (instrument == null) || (offerSide == null))
/*     */     {
/* 149 */       throw new NullPointerException("Params are not correctly set - listener=" + barFeedListener + " instrument=" + instrument + " period=" + jForexPeriod.getPeriod() + " offerSide=" + offerSide);
/*     */     }
/*     */ 
/* 152 */     String msg = validateJForexPeriodFields(jForexPeriod);
/* 153 */     if (msg != null) {
/* 154 */       throw new NullPointerException(msg);
/*     */     }
/*     */ 
/* 157 */     List listeners = (List)map.get(strategy);
/* 158 */     if (listeners == null) {
/* 159 */       listeners = new ArrayList();
/* 160 */       map.put(strategy, listeners);
/*     */     }
/*     */ 
/* 163 */     AbstractLiveBarFeedListenerWrapper listener = createListenerWrapper(instrument, jForexPeriod, offerSide, barFeedListener, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */ 
/* 174 */     listeners.add(listener);
/*     */ 
/* 176 */     return listener;
/*     */   }
/*     */ 
/*     */   protected List<LISTENER_WRAPPER> getHistoricalFeedListeners(IStrategy strategy) {
/* 180 */     synchronized (getHistoricalFeedListenersMap()) {
/* 181 */       List result = (List)getHistoricalFeedListenersMap().get(strategy);
/* 182 */       if (result == null) {
/* 183 */         return result;
/*     */       }
/* 185 */       result = Collections.unmodifiableList(result);
/* 186 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected LISTENER_WRAPPER findFeedListener(IStrategy strategy, Map<IStrategy, List<LISTENER_WRAPPER>> map, FORMED_LISTENER barFeedListener)
/*     */   {
/* 195 */     List listeners = (List)map.get(strategy);
/* 196 */     return findCandleFeedListener(barFeedListener, listeners);
/*     */   }
/*     */ 
/*     */   private LISTENER_WRAPPER findCandleFeedListener(FORMED_LISTENER barFeedListener, List<LISTENER_WRAPPER> listeners)
/*     */   {
/* 203 */     if (listeners != null) {
/* 204 */       for (AbstractLiveBarFeedListenerWrapper listener : listeners) {
/* 205 */         if (listener.getLiveBarFormedListener().equals(barFeedListener)) {
/* 206 */           return listener;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 211 */     return null;
/*     */   }
/*     */ 
/*     */   public void subscribeToBarsFeedForHistoricalTester(IStrategy strategy, IStrategyRunner strategyRunner, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, FORMED_LISTENER barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/* 228 */     synchronized (getHistoricalFeedListenersMap())
/*     */     {
/* 230 */       validateInstrument(strategyRunner.getInstruments(), instrument);
/*     */ 
/* 232 */       AbstractLiveBarFeedListenerWrapper listenerWrapper = subscribeToBarsFeed(strategy, instrument, jForexPeriod, offerSide, getHistoricalFeedListenersMap(), barFeedListener, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */ 
/* 245 */       boolean containsDataLoadingThread = strategyRunner.containsDataLoadingThread(instrument, jForexPeriod, offerSide);
/*     */ 
/* 247 */       if (!containsDataLoadingThread)
/*     */       {
/* 251 */         ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(100, true);
/*     */ 
/* 253 */         IDataLoadingThread dataLoadingThread = createDataLoadingThread(strategyRunner, instrument, jForexPeriod, offerSide, arrayBlockingQueue);
/*     */ 
/* 255 */         new Thread(dataLoadingThread).start();
/*     */ 
/* 257 */         listenerWrapper.setDataLoadingThread(dataLoadingThread);
/*     */ 
/* 259 */         strategyRunner.addDataLoadingThread(dataLoadingThread);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void subscribeToLiveBarsFeed(IStrategy strategy, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, FORMED_LISTENER barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/* 276 */     synchronized (getLiveFeedListenersMap()) {
/* 277 */       AbstractLiveBarFeedListenerWrapper listenerWrapper = subscribeToBarsFeed(strategy, instrument, jForexPeriod, offerSide, getLiveFeedListenersMap(), barFeedListener, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */ 
/* 290 */       addLiveInProgressBarListener(instrument, jForexPeriod, offerSide, listenerWrapper);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected List<LISTENER_WRAPPER> getHistoricalFeedListeners(IStrategy strategy, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide)
/*     */   {
/* 300 */     List strategyListeners = getHistoricalFeedListeners(strategy);
/* 301 */     List result = new ArrayList();
/* 302 */     if (strategyListeners != null) {
/* 303 */       for (AbstractLiveBarFeedListenerWrapper listener : strategyListeners) {
/* 304 */         if ((listener.getInstrument().equals(instrument)) && (listener.getJForexPeriod().equals(jForexPeriod)) && (listener.getOfferSide().equals(offerSide)))
/*     */         {
/* 309 */           result.add(listener);
/*     */         }
/*     */       }
/*     */     }
/* 313 */     return result;
/*     */   }
/*     */ 
/*     */   public void historicalBarReceived(IStrategy strategy, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BAR bar)
/*     */   {
/* 324 */     synchronized (getHistoricalFeedListenersMap()) {
/* 325 */       if ((strategy == null) || (instrument == null) || (jForexPeriod == null) || (jForexPeriod.getPeriod() == null) || (offerSide == null) || (bar == null))
/*     */       {
/* 333 */         throw new NullPointerException("Params are not correctly set - strategy=" + strategy + " instrument=" + instrument + " period=" + jForexPeriod.getPeriod() + " offerSide=" + offerSide + " bar=" + bar);
/*     */       }
/*     */ 
/* 336 */       List listenerWrappers = getHistoricalFeedListeners(strategy, instrument, jForexPeriod, offerSide);
/* 337 */       for (AbstractLiveBarFeedListenerWrapper listenerWrapper : listenerWrappers)
/*     */         try {
/* 339 */           onBar(listenerWrapper, instrument, jForexPeriod, offerSide, bar);
/*     */         } catch (Throwable t) {
/* 341 */           LOGGER.error(t.getLocalizedMessage(), t);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private LISTENER_WRAPPER unsubscribeFromBarsFeed(IStrategy strategy, Map<IStrategy, List<LISTENER_WRAPPER>> map, FORMED_LISTENER barFeedListener)
/*     */   {
/* 352 */     List listeners = (List)map.get(strategy);
/* 353 */     if (listeners != null)
/*     */     {
/* 355 */       AbstractLiveBarFeedListenerWrapper listenerToRemove = findFeedListener(strategy, map, barFeedListener);
/*     */ 
/* 357 */       if (listenerToRemove != null) {
/* 358 */         listeners.remove(listenerToRemove);
/*     */       }
/*     */ 
/* 361 */       if (listeners.isEmpty()) {
/* 362 */         map.remove(strategy);
/*     */       }
/*     */ 
/* 365 */       return listenerToRemove;
/*     */     }
/* 367 */     return null;
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromLiveBarsFeed(IStrategy strategy, FORMED_LISTENER barFeedListener)
/*     */   {
/* 375 */     synchronized (getLiveFeedListenersMap()) {
/* 376 */       AbstractLiveBarFeedListenerWrapper listener = unsubscribeFromBarsFeed(strategy, getLiveFeedListenersMap(), barFeedListener);
/*     */ 
/* 378 */       unsubscribeLiveCandleFeedListener(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromBarsFeedForHistoricalTester(IStrategy strategy, IStrategyRunner strategyRunner, FORMED_LISTENER barFeedListener)
/*     */   {
/* 388 */     synchronized (getHistoricalFeedListenersMap()) {
/* 389 */       AbstractLiveBarFeedListenerWrapper listener = unsubscribeFromBarsFeed(strategy, getHistoricalFeedListenersMap(), barFeedListener);
/* 390 */       strategyRunner.removeDataLoadingThread(listener.getDataLoadingThread());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void unsubscribeLiveCandleFeedListener(LISTENER_WRAPPER listenerWrapper)
/*     */   {
/* 396 */     if (listenerWrapper == null) {
/* 397 */       return;
/*     */     }
/*     */ 
/* 400 */     removeLiveInProgressBarListener(listenerWrapper);
/*     */   }
/*     */ 
/*     */   public void unsubscribeFromAll(IStrategy strategy)
/*     */   {
/* 406 */     synchronized (getLiveFeedListenersMap()) {
/* 407 */       List liveListeners = (List)getLiveFeedListenersMap().remove(strategy);
/*     */ 
/* 409 */       if (liveListeners != null) {
/* 410 */         for (AbstractLiveBarFeedListenerWrapper listener : liveListeners) {
/* 411 */           unsubscribeLiveCandleFeedListener(listener);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 416 */     synchronized (getHistoricalFeedListenersMap()) {
/* 417 */       getHistoricalFeedListenersMap().remove(strategy);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument(IStrategy strategy, FORMED_LISTENER listener)
/*     */   {
/* 424 */     AbstractLiveBarFeedListenerWrapper listenerWrapper = findFeedListener(strategy, getLiveFeedListenersMap(), listener);
/* 425 */     if (listenerWrapper != null) {
/* 426 */       return listenerWrapper.getInstrument();
/*     */     }
/*     */ 
/* 429 */     listenerWrapper = findFeedListener(strategy, getHistoricalFeedListenersMap(), listener);
/* 430 */     if (listenerWrapper != null) {
/* 431 */       return listenerWrapper.getInstrument();
/*     */     }
/*     */ 
/* 434 */     return null;
/*     */   }
/*     */ 
/*     */   public JForexPeriod getJForexPeriod(IStrategy strategy, FORMED_LISTENER listener)
/*     */   {
/* 441 */     AbstractLiveBarFeedListenerWrapper listenerWrapper = findFeedListener(strategy, getLiveFeedListenersMap(), listener);
/* 442 */     if (listenerWrapper != null) {
/* 443 */       return listenerWrapper.getJForexPeriod();
/*     */     }
/*     */ 
/* 446 */     listenerWrapper = findFeedListener(strategy, getHistoricalFeedListenersMap(), listener);
/* 447 */     if (listenerWrapper != null) {
/* 448 */       return listenerWrapper.getJForexPeriod();
/*     */     }
/*     */ 
/* 451 */     return null;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide(IStrategy strategy, FORMED_LISTENER listener)
/*     */   {
/* 458 */     AbstractLiveBarFeedListenerWrapper listenerWrapper = findFeedListener(strategy, getLiveFeedListenersMap(), listener);
/* 459 */     if (listenerWrapper != null) {
/* 460 */       return listenerWrapper.getOfferSide();
/*     */     }
/*     */ 
/* 463 */     listenerWrapper = findFeedListener(strategy, getHistoricalFeedListenersMap(), listener);
/* 464 */     if (listenerWrapper != null) {
/* 465 */       return listenerWrapper.getOfferSide();
/*     */     }
/*     */ 
/* 468 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.AbstractStrategyRateDataNotificationManager
 * JD-Core Version:    0.6.0
 */