/*     */ package com.dukascopy.dds2.greed.agent.strategy.notification.candle;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.feed.IBarFeedListener;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractStrategyRateDataNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.CandleDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ 
/*     */ public class StrategyCandleNotificationManager extends AbstractStrategyRateDataNotificationManager<IBarFeedListener, LiveFeedListener, LiveCandleFeedListenerWrapper, IBar, CandleData>
/*     */   implements IStrategyCandleNotificationManager
/*     */ {
/*     */   public StrategyCandleNotificationManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  32 */     super(feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected LiveCandleFeedListenerWrapper createListenerWrapper(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IBarFeedListener barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/*  46 */     return new LiveCandleFeedListenerWrapper(barFeedListener, new LiveFeedListener()
/*     */     {
/*     */       public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*     */       {
/*     */       }
/*     */     }
/*     */     , instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */   }
/*     */ 
/*     */   protected String validateJForexPeriodFields(JForexPeriod jForexPeriod)
/*     */   {
/*  69 */     if (jForexPeriod.getPeriod() == null) {
/*  70 */       return "Period could not be null";
/*     */     }
/*  72 */     if (!DataType.TIME_PERIOD_AGGREGATION.equals(jForexPeriod.getDataType())) {
/*  73 */       return "DataType has to be equals to " + DataType.TIME_PERIOD_AGGREGATION;
/*     */     }
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   protected IDataLoadingThread<CandleData> createDataLoadingThread(IStrategyRunner strategyRunner, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ArrayBlockingQueue<CandleData> queue)
/*     */   {
/*  86 */     String threadName = "Strategy tester data loading thread - " + jForexPeriod.toString();
/*     */ 
/*  88 */     IDataLoadingThread dataLoadingThread = new CandleDataLoadingThread(threadName, instrument, jForexPeriod, offerSide, queue, strategyRunner.getFrom(), strategyRunner.getTo(), strategyRunner.getFeedDataProvider());
/*     */ 
/*  99 */     return dataLoadingThread;
/*     */   }
/*     */ 
/*     */   protected void addLiveInProgressBarListener(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, LiveCandleFeedListenerWrapper formedListener)
/*     */   {
/* 109 */     getFeedDataProvider().subscribeToPeriodNotifications(instrument, jForexPeriod.getPeriod(), offerSide, formedListener);
/* 110 */     getFeedDataProvider().addInProgressCandleListener(instrument, jForexPeriod.getPeriod(), offerSide, (LiveFeedListener)formedListener.getLiveBarUpdatedListener());
/*     */   }
/*     */ 
/*     */   protected void onBar(LiveCandleFeedListenerWrapper listenerWrapper, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IBar bar)
/*     */   {
/* 121 */     ((IBarFeedListener)listenerWrapper.getLiveBarFormedListener()).onBar(instrument, jForexPeriod.getPeriod(), offerSide, bar);
/*     */   }
/*     */ 
/*     */   protected void removeLiveInProgressBarListener(LiveCandleFeedListenerWrapper listenerWrapper)
/*     */   {
/* 126 */     getFeedDataProvider().unsubscribeFromPeriodNotifications(listenerWrapper.getInstrument(), listenerWrapper.getJForexPeriod().getPeriod(), listenerWrapper.getOfferSide(), listenerWrapper);
/*     */ 
/* 133 */     getFeedDataProvider().removeInProgressCandleListener(listenerWrapper.getInstrument(), listenerWrapper.getJForexPeriod().getPeriod(), listenerWrapper.getOfferSide(), (LiveFeedListener)listenerWrapper.getLiveBarUpdatedListener());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.candle.StrategyCandleNotificationManager
 * JD-Core Version:    0.6.0
 */