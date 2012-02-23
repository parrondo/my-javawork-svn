/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.impl.TimedData;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.tester.dataload.IDataLoadingThread;
/*    */ import java.util.ArrayDeque;
/*    */ 
/*    */ public class DataLoadingThreadsContainer<TD extends TimedData>
/*    */ {
/*    */   public ArrayDeque<TimedData> askThreadTimedData;
/*    */   public ArrayDeque<TimedData> bidThreadTimedData;
/*    */   public IDataLoadingThread<TD> askThread;
/*    */   public IDataLoadingThread<TD> bidThread;
/*    */ 
/*    */   public DataLoadingThreadsContainer(IDataLoadingThread<TD> askThread)
/*    */   {
/* 20 */     this.askThread = askThread;
/* 21 */     this.askThreadTimedData = new ArrayDeque(505);
/*    */   }
/*    */ 
/*    */   public DataLoadingThreadsContainer(IDataLoadingThread<TD> askThread, IDataLoadingThread<TD> bidThread) {
/* 25 */     this.askThread = askThread;
/* 26 */     this.bidThread = bidThread;
/* 27 */     this.askThreadTimedData = new ArrayDeque(105);
/* 28 */     this.bidThreadTimedData = new ArrayDeque(105);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.DataLoadingThreadsContainer
 * JD-Core Version:    0.6.0
 */