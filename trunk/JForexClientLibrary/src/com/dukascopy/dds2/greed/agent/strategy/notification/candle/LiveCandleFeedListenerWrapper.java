/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification.candle;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.feed.IBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.impl.execution.post.PostCandleTask;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.charts.data.datacache.CandleData;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractLiveBarFeedListenerWrapper;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class LiveCandleFeedListenerWrapper extends AbstractLiveBarFeedListenerWrapper<IBarFeedListener, LiveFeedListener, CandleData>
/*    */   implements LiveFeedListener
/*    */ {
/*    */   public LiveCandleFeedListenerWrapper(IBarFeedListener liveCandleFormedListener, LiveFeedListener liveCandleUpdatedListener, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*    */   {
/* 36 */     super(liveCandleFormedListener, liveCandleUpdatedListener, instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*    */   }
/*    */ 
/*    */   public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*    */   {
/* 62 */     CandleData data = new CandleData(time, open, close, low, high, vol);
/*    */     try
/*    */     {
/* 65 */       Callable task = new PostCandleTask(this.taskManager, this.strategyProcessor.getStrategy(), this.exceptionHandler, (IBarFeedListener)getLiveBarFormedListener(), instrument, side, period, data);
/*    */ 
/* 76 */       this.strategyProcessor.executeTask(task, false);
/*    */     }
/*    */     catch (Throwable t) {
/* 79 */       onException(t);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.candle.LiveCandleFeedListenerWrapper
 * JD-Core Version:    0.6.0
 */