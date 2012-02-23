/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public abstract class AbstractPriceAggregationDataLoadingThread<TD extends AbstractPriceAggregationData, LISTENER extends IPriceAggregationLiveFeedListener<TD>> extends AbstractDataLoadingThread<TD>
/*    */ {
/*    */   public AbstractPriceAggregationDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<TD> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 34 */     super(name, instrument, jForexPeriod, offerSide, queue, from, DataCacheUtils.getTradingSessionStart(to) == to ? DataCacheUtils.getPreviousPriceAggregationBarStart(to) : to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 48 */     boolean needToWaitUntilInProgressBarIsLoaded = false;
/*    */ 
/* 50 */     IPriceAggregationLiveFeedListener inProgressListener = createInProgressAdapter();
/* 51 */     AbstractPriceAggregationData inProgressBar = getInProgressBar();
/*    */ 
/* 53 */     if (inProgressBar == null) {
/* 54 */       boolean isLoadingNow = isInProgressBarLoadingNow();
/* 55 */       if (!isLoadingNow) {
/* 56 */         addInProgressBarListener(inProgressListener);
/* 57 */         needToWaitUntilInProgressBarIsLoaded = true;
/*    */       }
/*    */     }
/*    */     try
/*    */     {
/* 62 */       if (needToWaitUntilInProgressBarIsLoaded) {
/* 63 */         while ((isInProgressBarLoadingNow()) && 
/* 64 */           (!isStop()))
/*    */         {
/*    */           try
/*    */           {
/* 68 */             Thread.sleep(100L);
/*    */           } catch (InterruptedException e) {
/* 70 */             LOGGER.error(e.getLocalizedMessage(), e);
/*    */           }
/*    */         }
/*    */       }
/*    */ 
/* 75 */       executeRun();
/*    */     }
/*    */     finally {
/* 78 */       removeInProgressBarListener(inProgressListener);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected long checkToTimeRightBound(long toTime) {
/* 83 */     toTime = toTime >= getInProgressBar().getTime() ? DataCacheUtils.getPreviousPriceAggregationBarStart(getInProgressBar().getTime()) : toTime;
/* 84 */     return toTime;
/*    */   }
/*    */ 
/*    */   protected abstract TD getInProgressBar();
/*    */ 
/*    */   protected abstract void addInProgressBarListener(LISTENER paramLISTENER);
/*    */ 
/*    */   protected abstract void removeInProgressBarListener(LISTENER paramLISTENER);
/*    */ 
/*    */   protected abstract boolean isInProgressBarLoadingNow();
/*    */ 
/*    */   protected abstract LISTENER createInProgressAdapter();
/*    */ 
/*    */   protected abstract void executeRun();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.AbstractPriceAggregationDataLoadingThread
 * JD-Core Version:    0.6.0
 */