/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification.tickbar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.feed.ITickBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.impl.execution.post.PostTickBarTask;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractLiveBarFeedListenerWrapper;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class LiveTickBarFeedListenerWrapper extends AbstractLiveBarFeedListenerWrapper<ITickBarFeedListener, ITickBarLiveFeedListener, TickBarData>
/*    */   implements ITickBarLiveFeedListener
/*    */ {
/*    */   public LiveTickBarFeedListenerWrapper(ITickBarFeedListener liveListener, ITickBarLiveFeedListener liveBarUpdatedListener, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*    */   {
/* 35 */     super(liveListener, liveBarUpdatedListener, instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*    */   }
/*    */ 
/*    */   public void newPriceData(TickBarData tickBar)
/*    */   {
/*    */     try
/*    */     {
/* 51 */       Callable task = new PostTickBarTask(this.taskManager, this.strategyProcessor.getStrategy(), this.exceptionHandler, (ITickBarFeedListener)getLiveBarFormedListener(), this.instrument, getOfferSide(), getJForexPeriod().getTickBarSize(), tickBar);
/*    */ 
/* 62 */       this.strategyProcessor.executeTask(task, false);
/*    */     }
/*    */     catch (Throwable t) {
/* 65 */       onException(t);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.tickbar.LiveTickBarFeedListenerWrapper
 * JD-Core Version:    0.6.0
 */