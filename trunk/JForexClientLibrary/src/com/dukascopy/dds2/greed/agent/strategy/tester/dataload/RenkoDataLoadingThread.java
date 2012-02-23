/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoLiveFeedAdapter;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class RenkoDataLoadingThread extends AbstractPriceAggregationDataLoadingThread<RenkoData, IRenkoLiveFeedListener>
/*    */ {
/*    */   public RenkoDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<RenkoData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 33 */     super(name, instrument, jForexPeriod, offerSide, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   protected void addInProgressBarListener(IRenkoLiveFeedListener inProgressListener)
/*    */   {
/* 47 */     getFeedDataProvider().getIntraperiodBarsGenerator().addInProgressRenkoListener(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), inProgressListener);
/*    */   }
/*    */ 
/*    */   protected IRenkoLiveFeedListener createInProgressAdapter()
/*    */   {
/* 52 */     return new RenkoLiveFeedAdapter() { } ;
/*    */   }
/*    */ 
/*    */   protected void executeRun() {
/* 57 */     IRenkoLiveFeedListener liveFeedListener = new IRenkoLiveFeedListener()
/*    */     {
/*    */       public void newPriceData(RenkoData renko) {
/* 60 */         if (!RenkoDataLoadingThread.this.isStop())
/* 61 */           RenkoDataLoadingThread.this.putDataToQueue(renko);
/*    */       }
/*    */     };
/* 66 */     LoadingProgressListener progressListener = createLoadingProgressListener();
/*    */ 
/* 68 */     long toTime = checkToTimeRightBound(getTo());
/*    */ 
/* 70 */     getFeedDataProvider().getPriceAggregationDataProvider().loadRenkoTimeIntervalSynched(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), getFrom(), toTime, liveFeedListener, progressListener);
/*    */   }
/*    */ 
/*    */   protected RenkoData getInProgressBar()
/*    */   {
/* 83 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressRenko(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange());
/*    */   }
/*    */ 
/*    */   protected boolean isInProgressBarLoadingNow()
/*    */   {
/* 88 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressRenkoLoadingNow(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange());
/*    */   }
/*    */ 
/*    */   protected void removeInProgressBarListener(IRenkoLiveFeedListener inProgressListener)
/*    */   {
/* 93 */     getFeedDataProvider().getIntraperiodBarsGenerator().removeInProgressRenkoListener(inProgressListener);
/*    */   }
/*    */ 
/*    */   protected RenkoData createEmptyBar()
/*    */   {
/* 98 */     return new RenkoData(-9223372036854775808L, -9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, -1.0D, -1L);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.RenkoDataLoadingThread
 * JD-Core Version:    0.6.0
 */