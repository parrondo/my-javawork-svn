/*    */ package com.dukascopy.api.impl.execution.post;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.impl.connect.JForexTaskManager;
/*    */ import com.dukascopy.api.impl.execution.AbstractPostDataTask;
/*    */ import com.dukascopy.api.impl.execution.Task.Type;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*    */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*    */ 
/*    */ public abstract class AbstractPostBarTask extends AbstractPostDataTask<Void>
/*    */ {
/*    */   protected final Instrument instrument;
/*    */   protected final OfferSide offerSide;
/*    */ 
/*    */   public AbstractPostBarTask(JForexTaskManager taskManager, IStrategy strategy, IStrategyExceptionHandler exceptionHandler, Instrument instrument, OfferSide offerSide)
/*    */   {
/* 30 */     super(taskManager, strategy, exceptionHandler);
/*    */ 
/* 35 */     this.instrument = instrument;
/* 36 */     this.offerSide = offerSide;
/*    */   }
/*    */ 
/*    */   protected IStrategyExceptionHandler.Source getSource()
/*    */   {
/* 41 */     return IStrategyExceptionHandler.Source.ON_BAR;
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 46 */     return Task.Type.BAR;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.post.AbstractPostBarTask
 * JD-Core Version:    0.6.0
 */