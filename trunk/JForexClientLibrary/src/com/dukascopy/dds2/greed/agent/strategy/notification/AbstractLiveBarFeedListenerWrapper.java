/*    */ package com.dukascopy.dds2.greed.agent.strategy.notification;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.TimedData;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.connect.StrategyProcessor;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class AbstractLiveBarFeedListenerWrapper<FORMED_LISTENER, UPDATED_LISTENER, TD extends TimedData>
/*    */ {
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLiveBarFeedListenerWrapper.class);
/*    */   private final FORMED_LISTENER liveBarFormedListener;
/*    */   private final UPDATED_LISTENER liveBarUpdatedListener;
/*    */   protected final Instrument instrument;
/*    */   protected final OfferSide offerSide;
/*    */   protected final JForexPeriod jForexPeriod;
/*    */   private IDataLoadingThread<TD> dataLoadingThread;
/*    */   protected IStrategyExceptionHandler exceptionHandler;
/*    */   protected JForexTaskManager taskManager;
/*    */   protected StrategyProcessor strategyProcessor;
/*    */   protected INotificationUtils notificationUtils;
/*    */ 
/*    */   public AbstractLiveBarFeedListenerWrapper(FORMED_LISTENER liveListener, UPDATED_LISTENER liveBarUpdatedListener, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, IStrategyExceptionHandler exceptionHandler, JForexTaskManager taskManager, StrategyProcessor strategyProcessor, INotificationUtils notificationUtils)
/*    */   {
/* 51 */     this.liveBarFormedListener = liveListener;
/* 52 */     this.liveBarUpdatedListener = liveBarUpdatedListener;
/* 53 */     this.instrument = instrument;
/* 54 */     this.jForexPeriod = jForexPeriod;
/* 55 */     this.offerSide = offerSide;
/* 56 */     this.exceptionHandler = exceptionHandler;
/* 57 */     this.taskManager = taskManager;
/* 58 */     this.strategyProcessor = strategyProcessor;
/* 59 */     this.notificationUtils = notificationUtils;
/*    */   }
/*    */ 
/*    */   public FORMED_LISTENER getLiveBarFormedListener() {
/* 63 */     return this.liveBarFormedListener;
/*    */   }
/*    */ 
/*    */   public IDataLoadingThread<TD> getDataLoadingThread() {
/* 67 */     return this.dataLoadingThread;
/*    */   }
/*    */ 
/*    */   public void setDataLoadingThread(IDataLoadingThread<TD> dataLoadingThread) {
/* 71 */     this.dataLoadingThread = dataLoadingThread;
/*    */   }
/*    */ 
/*    */   public UPDATED_LISTENER getLiveBarUpdatedListener() {
/* 75 */     return this.liveBarUpdatedListener;
/*    */   }
/*    */ 
/*    */   public Instrument getInstrument() {
/* 79 */     return this.instrument;
/*    */   }
/*    */ 
/*    */   public OfferSide getOfferSide() {
/* 83 */     return this.offerSide;
/*    */   }
/*    */ 
/*    */   public JForexPeriod getJForexPeriod() {
/* 87 */     return this.jForexPeriod;
/*    */   }
/*    */ 
/*    */   protected void onException(Throwable t) {
/* 91 */     Object strategy = this.strategyProcessor == null ? this : this.strategyProcessor.getStrategy();
/* 92 */     String msg = StrategyWrapper.representError(strategy, t);
/* 93 */     this.notificationUtils.postErrorMessage(msg, t, false);
/* 94 */     LOGGER.error(t.getMessage(), t);
/* 95 */     if (this.exceptionHandler != null)
/* 96 */       this.exceptionHandler.onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_BAR, t);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.notification.AbstractLiveBarFeedListenerWrapper
 * JD-Core Version:    0.6.0
 */