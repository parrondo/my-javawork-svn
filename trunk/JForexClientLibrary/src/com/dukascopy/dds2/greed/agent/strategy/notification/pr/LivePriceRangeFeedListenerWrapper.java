/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification.pr;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.feed.IRangeBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.impl.execution.post.PostRangeBarTask;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractLiveBarFeedListenerWrapper;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class LivePriceRangeFeedListenerWrapper extends AbstractLiveBarFeedListenerWrapper<IRangeBarFeedListener, IPriceRangeLiveFeedListener, PriceRangeData>
/*    */   implements IPriceRangeLiveFeedListener
/*    */ {
/*    */   public LivePriceRangeFeedListenerWrapper(IRangeBarFeedListener liveListener, IPriceRangeLiveFeedListener liveBarUpdatedListener, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*    */   {
/* 34 */     super(liveListener, liveBarUpdatedListener, instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*    */   }
/*    */ 
/*    */   public void newPriceData(PriceRangeData priceRange)
/*    */   {
/*    */     try
/*    */     {
/* 50 */       Callable task = new PostRangeBarTask(this.taskManager, this.strategyProcessor.getStrategy(), this.exceptionHandler, (IRangeBarFeedListener)getLiveBarFormedListener(), this.instrument, getOfferSide(), getJForexPeriod().getPriceRange(), priceRange);
/*    */ 
/* 61 */       this.strategyProcessor.executeTask(task, false);
/*    */     }
/*    */     catch (Throwable t) {
/* 64 */       onException(t);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.pr.LivePriceRangeFeedListenerWrapper
 * JD-Core Version:    0.6.0
 */