/*     */ package com.dukascopy.dds2.greed.agent.strategy.notification.tickbar;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.feed.ITickBar;
/*     */ import com.dukascopy.api.feed.ITickBarFeedListener;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*     */ import com.dukascopy.charts.data.datacache.tickbar.TickBarLiveFeedAdapter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractStrategyRateDataNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TickBarDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ 
/*     */ public class StrategyTickBarNotificationManager extends AbstractStrategyRateDataNotificationManager<ITickBarFeedListener, ITickBarLiveFeedListener, LiveTickBarFeedListenerWrapper, ITickBar, TickBarData>
/*     */   implements IStrategyTickBarNotificationManager
/*     */ {
/*     */   public StrategyTickBarNotificationManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  39 */     super(feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected void addLiveInProgressBarListener(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, LiveTickBarFeedListenerWrapper listenerWrapper)
/*     */   {
/*  49 */     getFeedDataProvider().getIntraperiodBarsGenerator().addTickBarNotificationListener(instrument, offerSide, jForexPeriod.getTickBarSize(), listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected IDataLoadingThread<TickBarData> createDataLoadingThread(IStrategyRunner strategyRunner, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ArrayBlockingQueue<TickBarData> queue)
/*     */   {
/*  65 */     String name = "Strategy tester data loading thread - " + jForexPeriod.toString();
/*     */ 
/*  67 */     return new TickBarDataLoadingThread(name, instrument, jForexPeriod, offerSide, queue, strategyRunner.getFrom(), strategyRunner.getTo(), strategyRunner.getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected LiveTickBarFeedListenerWrapper createListenerWrapper(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ITickBarFeedListener barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/*  90 */     return new LiveTickBarFeedListenerWrapper(barFeedListener, new TickBarLiveFeedAdapter()
/*     */     {
/*     */     }
/*     */     , instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */   }
/*     */ 
/*     */   protected void onBar(LiveTickBarFeedListenerWrapper listenerWrapper, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ITickBar bar)
/*     */   {
/* 111 */     ((ITickBarFeedListener)listenerWrapper.getLiveBarFormedListener()).onBar(instrument, offerSide, jForexPeriod.getTickBarSize(), bar);
/*     */   }
/*     */ 
/*     */   protected void removeLiveInProgressBarListener(LiveTickBarFeedListenerWrapper listenerWrapper)
/*     */   {
/* 116 */     getFeedDataProvider().getIntraperiodBarsGenerator().removeTickBarNotificationListener(listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected String validateJForexPeriodFields(JForexPeriod jForexPeriod)
/*     */   {
/* 121 */     if ((jForexPeriod.getTickBarSize() == null) || (jForexPeriod.getPeriod() == null))
/*     */     {
/* 125 */       return "Period and TickBarSize could not be nulls";
/*     */     }
/* 127 */     if (!DataType.TICK_BAR.equals(jForexPeriod.getDataType())) {
/* 128 */       return "DataType has to be equal to " + DataType.TICK_BAR;
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.StrategyTickBarNotificationManager
 * JD-Core Version:    0.6.0
 */