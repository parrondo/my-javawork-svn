/*    */ package com.dukascopy.api.impl.execution.post;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.TickBarSize;
/*    */ import com.dukascopy.api.feed.ITickBar;
/*    */ import com.dukascopy.api.feed.ITickBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PostTickBarTask extends AbstractPostBarTask
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(PostTickBarTask.class);
/*    */   private final ITickBarFeedListener barFeedListener;
/*    */   private final TickBarSize tickBarSize;
/*    */   private final ITickBar bar;
/*    */ 
/*    */   public PostTickBarTask(JForexTaskManager taskManager, IStrategy strategy, IStrategyExceptionHandler exceptionHandler, ITickBarFeedListener barFeedListener, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, ITickBar bar)
/*    */   {
/* 41 */     super(taskManager, strategy, exceptionHandler, instrument, offerSide);
/* 42 */     this.barFeedListener = barFeedListener;
/* 43 */     this.tickBarSize = tickBarSize;
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
/* 54 */     this.barFeedListener.onBar(this.instrument, this.offerSide, this.tickBarSize, this.bar);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.post.PostTickBarTask
 * JD-Core Version:    0.6.0
 */