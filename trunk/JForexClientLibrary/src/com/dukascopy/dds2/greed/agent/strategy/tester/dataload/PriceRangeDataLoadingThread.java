/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
/*    */ import com.dukascopy.charts.data.datacache.rangebar.PriceRangeLiveFeedAdapter;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class PriceRangeDataLoadingThread extends AbstractPriceAggregationDataLoadingThread<PriceRangeData, IPriceRangeLiveFeedListener>
/*    */ {
/*    */   public PriceRangeDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<PriceRangeData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 30 */     super(name, instrument, jForexPeriod, offerSide, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   protected void executeRun()
/*    */   {
/* 44 */     IPriceRangeLiveFeedListener liveFeedListener = new IPriceRangeLiveFeedListener()
/*    */     {
/*    */       public void newPriceData(PriceRangeData priceRange) {
/* 47 */         if (!PriceRangeDataLoadingThread.this.isStop())
/* 48 */           PriceRangeDataLoadingThread.this.putDataToQueue(priceRange);
/*    */       }
/*    */     };
/* 53 */     LoadingProgressListener progressListener = createLoadingProgressListener();
/*    */ 
/* 55 */     long toTime = checkToTimeRightBound(getTo());
/*    */ 
/* 57 */     getFeedDataProvider().getPriceAggregationDataProvider().loadPriceRangeTimeIntervalSynched(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), getFrom(), toTime, liveFeedListener, progressListener);
/*    */   }
/*    */ 
/*    */   protected PriceRangeData createEmptyBar()
/*    */   {
/* 70 */     return new PriceRangeData(-9223372036854775808L, -9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, -1.0D, -1L);
/*    */   }
/*    */ 
/*    */   protected void addInProgressBarListener(IPriceRangeLiveFeedListener inProgressListener)
/*    */   {
/* 75 */     getFeedDataProvider().getIntraperiodBarsGenerator().addInProgressPriceRangeListener(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), inProgressListener);
/*    */   }
/*    */ 
/*    */   protected IPriceRangeLiveFeedListener createInProgressAdapter()
/*    */   {
/* 80 */     return new PriceRangeLiveFeedAdapter() { } ;
/*    */   }
/*    */ 
/*    */   protected PriceRangeData getInProgressBar() {
/* 85 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressPriceRange(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange());
/*    */   }
/*    */ 
/*    */   protected boolean isInProgressBarLoadingNow()
/*    */   {
/* 90 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressPriceRangeLoadingNow(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange());
/*    */   }
/*    */ 
/*    */   protected void removeInProgressBarListener(IPriceRangeLiveFeedListener inProgressListener)
/*    */   {
/* 95 */     getFeedDataProvider().getIntraperiodBarsGenerator().removeInProgressPriceRangeListener(inProgressListener);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.PriceRangeDataLoadingThread
 * JD-Core Version:    0.6.0
 */