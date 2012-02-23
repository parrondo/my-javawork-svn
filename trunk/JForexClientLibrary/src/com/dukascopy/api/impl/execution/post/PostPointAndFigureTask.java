/*    */ package com.dukascopy.api.impl.execution.post;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.ReversalAmount;
/*    */ import com.dukascopy.api.feed.IPointAndFigure;
/*    */ import com.dukascopy.api.feed.IPointAndFigureFeedListener;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PostPointAndFigureTask extends AbstractPostBarTask
/*    */ {
/* 25 */   private static final Logger LOGGER = LoggerFactory.getLogger(PostPointAndFigureTask.class);
/*    */   private final IPointAndFigureFeedListener barFeedListener;
/*    */   private final PriceRange boxSize;
/*    */   private final ReversalAmount reversalAmount;
/*    */   private final IPointAndFigure bar;
/*    */ 
/*    */   public PostPointAndFigureTask(JForexTaskManager taskManager, IStrategy strategy, IStrategyExceptionHandler exceptionHandler, IPointAndFigureFeedListener barFeedListener, Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, IPointAndFigure bar)
/*    */   {
/* 44 */     super(taskManager, strategy, exceptionHandler, instrument, offerSide);
/* 45 */     this.barFeedListener = barFeedListener;
/* 46 */     this.boxSize = boxSize;
/* 47 */     this.reversalAmount = reversalAmount;
/* 48 */     this.bar = bar;
/*    */   }
/*    */ 
/*    */   protected Logger getLogger()
/*    */   {
/* 53 */     return LOGGER;
/*    */   }
/*    */ 
/*    */   protected void postData() throws Throwable
/*    */   {
/* 58 */     this.barFeedListener.onBar(this.instrument, this.offerSide, this.boxSize, this.reversalAmount, this.bar);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.post.PostPointAndFigureTask
 * JD-Core Version:    0.6.0
 */