/*    */ package com.dukascopy.api.impl.execution.post;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.feed.IRenkoBar;
/*    */ import com.dukascopy.api.feed.IRenkoBarFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PostRenkoTask extends AbstractPostBarTask
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(PostRenkoTask.class);
/*    */   private final IRenkoBarFeedListener barFeedListener;
/*    */   private final PriceRange priceRange;
/*    */   private final IRenkoBar bar;
/*    */ 
/*    */   public PostRenkoTask(JForexTaskManager taskManager, IStrategy strategy, IStrategyExceptionHandler exceptionHandler, IRenkoBarFeedListener barFeedListener, Instrument instrument, OfferSide offerSide, PriceRange priceRange, IRenkoBar bar)
/*    */   {
/* 41 */     super(taskManager, strategy, exceptionHandler, instrument, offerSide);
/* 42 */     this.barFeedListener = barFeedListener;
/* 43 */     this.priceRange = priceRange;
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
/* 54 */     this.barFeedListener.onBar(this.instrument, this.offerSide, this.priceRange, this.bar);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.post.PostRenkoTask
 * JD-Core Version:    0.6.0
 */