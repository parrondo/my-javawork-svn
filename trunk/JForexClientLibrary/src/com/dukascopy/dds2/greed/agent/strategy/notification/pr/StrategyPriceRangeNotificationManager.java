/*     */ package com.dukascopy.dds2.greed.agent.strategy.notification.pr;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.feed.IRangeBar;
/*     */ import com.dukascopy.api.feed.IRangeBarFeedListener;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*     */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeLiveFeedAdapter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractStrategyRateDataNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.PriceRangeDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ 
/*     */ public class StrategyPriceRangeNotificationManager extends AbstractStrategyRateDataNotificationManager<IRangeBarFeedListener, IPriceRangeLiveFeedListener, LivePriceRangeFeedListenerWrapper, IRangeBar, PriceRangeData>
/*     */   implements IStrategyPriceRangeNotificationManager
/*     */ {
/*     */   public StrategyPriceRangeNotificationManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  39 */     super(feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected void addLiveInProgressBarListener(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, LivePriceRangeFeedListenerWrapper listenerWrapper)
/*     */   {
/*  49 */     getFeedDataProvider().getIntraperiodBarsGenerator().addPriceRangeNotificationListener(instrument, offerSide, jForexPeriod.getPriceRange(), listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected IDataLoadingThread<PriceRangeData> createDataLoadingThread(IStrategyRunner strategyRunner, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ArrayBlockingQueue<PriceRangeData> queue)
/*     */   {
/*  60 */     String threadName = "Strategy tester data loading thread - " + jForexPeriod.toString();
/*     */ 
/*  62 */     return new PriceRangeDataLoadingThread(threadName, instrument, jForexPeriod, offerSide, queue, strategyRunner.getFrom(), strategyRunner.getTo(), strategyRunner.getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected LivePriceRangeFeedListenerWrapper createListenerWrapper(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IRangeBarFeedListener barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/*  85 */     return new LivePriceRangeFeedListenerWrapper(barFeedListener, new PriceRangeLiveFeedAdapter()
/*     */     {
/*     */     }
/*     */     , instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */   }
/*     */ 
/*     */   protected void onBar(LivePriceRangeFeedListenerWrapper listenerWrapper, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IRangeBar bar)
/*     */   {
/* 106 */     ((IRangeBarFeedListener)listenerWrapper.getLiveBarFormedListener()).onBar(instrument, offerSide, jForexPeriod.getPriceRange(), bar);
/*     */   }
/*     */ 
/*     */   protected void removeLiveInProgressBarListener(LivePriceRangeFeedListenerWrapper listenerWrapper)
/*     */   {
/* 111 */     getFeedDataProvider().getIntraperiodBarsGenerator().removePriceRangeNotificationListener(listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected String validateJForexPeriodFields(JForexPeriod jForexPeriod)
/*     */   {
/* 116 */     if ((jForexPeriod.getPriceRange() == null) || (jForexPeriod.getPeriod() == null))
/*     */     {
/* 120 */       return "Period and PriceRange could not be nulls";
/*     */     }
/* 122 */     if (!DataType.PRICE_RANGE_AGGREGATION.equals(jForexPeriod.getDataType())) {
/* 123 */       return "DataType has to be equal to " + DataType.PRICE_RANGE_AGGREGATION;
/*     */     }
/* 125 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.pr.StrategyPriceRangeNotificationManager
 * JD-Core Version:    0.6.0
 */