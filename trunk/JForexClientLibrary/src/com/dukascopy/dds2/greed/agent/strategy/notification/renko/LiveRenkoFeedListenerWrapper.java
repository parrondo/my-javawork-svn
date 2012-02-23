/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification.renko;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.feed.IRenkoBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.impl.execution.post.PostRenkoTask;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractLiveBarFeedListenerWrapper;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class LiveRenkoFeedListenerWrapper extends AbstractLiveBarFeedListenerWrapper<IRenkoBarFeedListener, IRenkoLiveFeedListener, RenkoData>
/*    */   implements IRenkoLiveFeedListener
/*    */ {
/*    */   public LiveRenkoFeedListenerWrapper(IRenkoBarFeedListener liveListener, IRenkoLiveFeedListener liveBarUpdatedListener, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*    */   {
/* 38 */     super(liveListener, liveBarUpdatedListener, instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*    */   }
/*    */ 
/*    */   public void newPriceData(RenkoData renko)
/*    */   {
/*    */     try
/*    */     {
/* 54 */       Callable task = new PostRenkoTask(this.taskManager, this.strategyProcessor.getStrategy(), this.exceptionHandler, (IRenkoBarFeedListener)getLiveBarFormedListener(), this.instrument, getOfferSide(), getJForexPeriod().getPriceRange(), renko);
/*    */ 
/* 65 */       this.strategyProcessor.executeTask(task, false);
/*    */     }
/*    */     catch (Throwable t) {
/* 68 */       onException(t);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.renko.LiveRenkoFeedListenerWrapper
 * JD-Core Version:    0.6.0
 */