/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.threads.CurrencyWorkerExecutor;
/*    */ import com.dukascopy.dds2.greed.threads.SingleThreadExecutorFactory;
/*    */ import com.dukascopy.dds2.greed.threads.WorkerExecutor;
/*    */ import com.dukascopy.dds2.greed.util.event.ApplicationEvent;
/*    */ import com.dukascopy.dds2.greed.util.event.ApplicationListener;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.Executor;
/*    */ 
/*    */ public class AppActionListener
/*    */   implements ApplicationListener
/*    */ {
/*    */   private Executor commonActionProcessor;
/*    */   private Executor messagesActionProcessor;
/*    */   private Executor ordersActionProcessor;
/*    */   private Executor ogmUpdateActionProcessor;
/*    */   private Executor instrumentStatusUpdateActionProcessor;
/*    */   private Map<String, Executor> currencyActionProcessor;
/*    */ 
/*    */   public AppActionListener()
/*    */   {
/* 30 */     this.commonActionProcessor = new WorkerExecutor("common");
/*    */ 
/* 32 */     this.ordersActionProcessor = new WorkerExecutor("orders");
/* 33 */     this.instrumentStatusUpdateActionProcessor = SingleThreadExecutorFactory.newSingleThreadExecutor("isupd");
/* 34 */     this.ogmUpdateActionProcessor = SingleThreadExecutorFactory.newSingleThreadExecutor("ogmupd");
/* 35 */     this.messagesActionProcessor = SingleThreadExecutorFactory.newSingleThreadExecutor("msgs");
/*    */ 
/* 37 */     this.currencyActionProcessor = new HashMap();
/*    */   }
/*    */ 
/*    */   public void onApplicationEvent(ApplicationEvent applicationEvent)
/*    */   {
/* 45 */     ActionExecution command = new ActionExecution(applicationEvent);
/*    */ 
/* 47 */     if ((applicationEvent instanceof PostMessageAction)) {
/* 48 */       this.messagesActionProcessor.execute(command);
/* 49 */     } else if (((applicationEvent instanceof OrderEntryAction)) || ((applicationEvent instanceof OrderGroupCloseAction)) || ((applicationEvent instanceof MassOrderGroupCloseAction)) || ((applicationEvent instanceof MergePositionsAction)) || ((applicationEvent instanceof CancelOrderAction)))
/*    */     {
/* 56 */       this.ordersActionProcessor.execute(command);
/* 57 */     } else if ((applicationEvent instanceof OrderGroupUpdateAction)) {
/* 58 */       this.ogmUpdateActionProcessor.execute(command);
/* 59 */     } else if ((applicationEvent instanceof UpdateTradabilityAction)) {
/* 60 */       this.instrumentStatusUpdateActionProcessor.execute(command);
/* 61 */     } else if ((applicationEvent instanceof MarketStateAction)) {
/* 62 */       MarketStateAction msa = (MarketStateAction)applicationEvent;
/* 63 */       Executor executor = null;
/* 64 */       if (this.currencyActionProcessor.containsKey(msa.instrument)) {
/* 65 */         executor = (Executor)this.currencyActionProcessor.get(msa.instrument);
/*    */       } else {
/* 67 */         executor = new CurrencyWorkerExecutor(msa.instrument);
/* 68 */         this.currencyActionProcessor.put(msa.instrument, executor);
/*    */       }
/* 70 */       executor.execute(command);
/*    */     } else {
/* 72 */       this.commonActionProcessor.execute(command);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AppActionListener
 * JD-Core Version:    0.6.0
 */