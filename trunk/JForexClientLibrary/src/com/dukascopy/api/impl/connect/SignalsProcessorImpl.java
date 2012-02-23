/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.ISignal;
/*    */ import com.dukascopy.api.ISignalsProcessor;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SignalsProcessorImpl
/*    */   implements ISignalsProcessor
/*    */ {
/* 11 */   private List<ISignal> signalsList = new ArrayList();
/*    */ 
/*    */   public synchronized void add(ISignal signal)
/*    */   {
/* 15 */     this.signalsList.add(signal);
/*    */   }
/*    */ 
/*    */   public synchronized void add(List<ISignal> signals)
/*    */   {
/* 20 */     this.signalsList.addAll(signals);
/*    */   }
/*    */ 
/*    */   public List<ISignal> retrieve()
/*    */   {
/* 25 */     return copyList();
/*    */   }
/*    */ 
/*    */   private synchronized List<ISignal> copyList() {
/* 29 */     List newList = new ArrayList();
/* 30 */     newList.addAll(this.signalsList);
/* 31 */     this.signalsList.clear();
/* 32 */     return newList;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.SignalsProcessorImpl
 * JD-Core Version:    0.6.0
 */