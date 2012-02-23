/*    */ package com.dukascopy.api.impl.execution.post;
/*    */ 
/*    */ import com.dukascopy.api.IBar;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.feed.IBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PostCandleTask extends AbstractPostBarTask
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(PostCandleTask.class);
/*    */   private final IBarFeedListener barFeedListener;
/*    */   private final Period period;
/*    */   private final IBar bar;
/*    */ 
/*    */   public PostCandleTask(JForexTaskManager taskManager, IStrategy strategy, IStrategyExceptionHandler exceptionHandler, IBarFeedListener barFeedListener, Instrument instrument, OfferSide offerSide, Period period, IBar bar)
/*    */   {
/* 41 */     super(taskManager, strategy, exceptionHandler, instrument, offerSide);
/* 42 */     this.barFeedListener = barFeedListener;
/* 43 */     this.period = period;
/* 44 */     this.bar = bar;
/*    */   }
/*    */ 
/*    */   protected Logger getLogger()
/*    */   {
/* 49 */     return LOGGER;
/*    */   }
/*    */ 
/*    */   protected void postData() throws Throwable
/*    */   {
/* 54 */     this.barFeedListener.onBar(this.instrument, this.period, this.offerSide, this.bar);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.post.PostCandleTask
 * JD-Core Version:    0.6.0
 */