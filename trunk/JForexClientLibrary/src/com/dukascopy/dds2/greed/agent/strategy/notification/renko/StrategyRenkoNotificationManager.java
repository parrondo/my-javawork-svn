/*     */ package com.dukascopy.dds2.greed.agent.strategy.notification.renko;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.feed.IRenkoBar;
/*     */ import com.dukascopy.api.feed.IRenkoBarFeedListener;
/*     */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*     */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*     */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*     */ import com.dukascopy.charts.data.datacache.renko.RenkoLiveFeedAdapter;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractStrategyRateDataNotificationManager;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.RenkoDataLoadingThread;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ 
/*     */ public class StrategyRenkoNotificationManager extends AbstractStrategyRateDataNotificationManager<IRenkoBarFeedListener, IRenkoLiveFeedListener, LiveRenkoFeedListenerWrapper, IRenkoBar, RenkoData>
/*     */   implements IStrategyRenkoNotificationManager
/*     */ {
/*     */   public StrategyRenkoNotificationManager(FeedDataProvider feedDataProvider)
/*     */   {
/*  42 */     super(feedDataProvider);
/*     */   }
/*     */ 
/*     */   protected void addLiveInProgressBarListener(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, LiveRenkoFeedListenerWrapper listenerWrapper)
/*     */   {
/*  52 */     getFeedDataProvider().getIntraperiodBarsGenerator().addRenkoNotificationListener(instrument, offerSide, jForexPeriod.getPriceRange(), listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected IDataLoadingThread<RenkoData> createDataLoadingThread(IStrategyRunner strategyRunner, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, ArrayBlockingQueue<RenkoData> queue)
/*     */   {
/*  63 */     String threadName = "Strategy tester data loading thread - " + jForexPeriod.toString();
/*     */ 
/*  65 */     return new RenkoDataLoadingThread(threadName, instrument, jForexPeriod, offerSide, queue, strategyRunner.getFrom(), strategyRunner.getTo(), strategyRunner.getFeedDataProvider());
/*     */   }
/*     */ 
/*     */   protected LiveRenkoFeedListenerWrapper createListenerWrapper(Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IRenkoBarFeedListener barFeedListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*     */   {
/*  88 */     return new LiveRenkoFeedListenerWrapper(barFeedListener, new RenkoLiveFeedAdapter()
/*     */     {
/*     */     }
/*     */     , instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*     */   }
/*     */ 
/*     */   protected void onBar(LiveRenkoFeedListenerWrapper listenerWrapper, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IRenkoBar bar)
/*     */   {
/* 109 */     ((IRenkoBarFeedListener)listenerWrapper.getLiveBarFormedListener()).onBar(instrument, offerSide, jForexPeriod.getPriceRange(), bar);
/*     */   }
/*     */ 
/*     */   protected void removeLiveInProgressBarListener(LiveRenkoFeedListenerWrapper listenerWrapper)
/*     */   {
/* 114 */     getFeedDataProvider().getIntraperiodBarsGenerator().removeRenkoNotificationListener(listenerWrapper);
/*     */   }
/*     */ 
/*     */   protected String validateJForexPeriodFields(JForexPeriod jForexPeriod)
/*     */   {
/* 119 */     if ((jForexPeriod.getPriceRange() == null) || (jForexPeriod.getPeriod() == null))
/*     */     {
/* 123 */       return "Period and PriceRange could not be nulls";
/*     */     }
/* 125 */     if (!DataType.RENKO.equals(jForexPeriod.getDataType())) {
/* 126 */       return "DataType has to be equal to " + DataType.RENKO;
/*     */     }
/* 128 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.renko.StrategyRenkoNotificationManager
 * JD-Core Version:    0.6.0
 */