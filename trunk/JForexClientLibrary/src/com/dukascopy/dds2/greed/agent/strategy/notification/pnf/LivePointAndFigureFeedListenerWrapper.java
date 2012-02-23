/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification.pnf;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.feed.IPointAndFigureFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.impl.execution.post.PostPointAndFigureTask;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.notification.AbstractLiveBarFeedListenerWrapper;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public class LivePointAndFigureFeedListenerWrapper extends AbstractLiveBarFeedListenerWrapper<IPointAndFigureFeedListener, IPointAndFigureLiveFeedListener, PointAndFigureData>
/*    */   implements IPointAndFigureLiveFeedListener
/*    */ {
/*    */   public LivePointAndFigureFeedListenerWrapper(IPointAndFigureFeedListener liveListener, IPointAndFigureLiveFeedListener liveBarUpdatedListener, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*    */   {
/* 35 */     super(liveListener, liveBarUpdatedListener, instrument, jForexPeriod, offerSide, exceptionHandler, taskManager, strategyProcessor, notificationUtils);
/*    */   }
/*    */ 
/*    */   public void newPriceData(PointAndFigureData pointAndFigure)
/*    */   {
/*    */     try
/*    */     {
/* 51 */       Callable task = new PostPointAndFigureTask(this.taskManager, this.strategyProcessor.getStrategy(), this.exceptionHandler, (IPointAndFigureFeedListener)getLiveBarFormedListener(), this.instrument, getOfferSide(), getJForexPeriod().getPriceRange(), getJForexPeriod().getReversalAmount(), pointAndFigure);
/*    */ 
/* 63 */       this.strategyProcessor.executeTask(task, false);
/*    */     }
/*    */     catch (Throwable t) {
/* 66 */       onException(t);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.pnf.LivePointAndFigureFeedListenerWrapper
 * JD-Core Version:    0.6.0
 */