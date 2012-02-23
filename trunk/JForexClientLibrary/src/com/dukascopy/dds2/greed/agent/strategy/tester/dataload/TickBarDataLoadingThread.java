/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
/*    */ import com.dukascopy.charts.data.datacache.tickbar.TickBarLiveFeedAdapter;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class TickBarDataLoadingThread extends AbstractPriceAggregationDataLoadingThread<TickBarData, ITickBarLiveFeedListener>
/*    */ {
/*    */   public TickBarDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<TickBarData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 30 */     super(name, instrument, jForexPeriod, offerSide, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   protected void executeRun()
/*    */   {
/* 44 */     ITickBarLiveFeedListener liveFeedListener = new ITickBarLiveFeedListener()
/*    */     {
/*    */       public void newPriceData(TickBarData tickBar) {
/* 47 */         if (!TickBarDataLoadingThread.this.isStop())
/* 48 */           TickBarDataLoadingThread.this.putDataToQueue(tickBar);
/*    */       }
/*    */     };
/* 53 */     LoadingProgressListener progressListener = createLoadingProgressListener();
/*    */ 
/* 55 */     long toTime = checkToTimeRightBound(getTo());
/*    */ 
/* 57 */     getFeedDataProvider().getPriceAggregationDataProvider().loadTickBarTimeIntervalSynched(getInstrument(), getOfferSide(), getJForexPeriod().getTickBarSize(), getFrom(), toTime, liveFeedListener, progressListener);
/*    */   }
/*    */ 
/*    */   protected TickBarData createEmptyBar()
/*    */   {
/* 70 */     return new TickBarData(-9223372036854775808L, -9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, -1.0D, -1L);
/*    */   }
/*    */ 
/*    */   protected void addInProgressBarListener(ITickBarLiveFeedListener inProgressListener)
/*    */   {
/* 75 */     getFeedDataProvider().getIntraperiodBarsGenerator().addInProgressTickBarListener(getInstrument(), getOfferSide(), getJForexPeriod().getTickBarSize(), inProgressListener);
/*    */   }
/*    */ 
/*    */   protected ITickBarLiveFeedListener createInProgressAdapter()
/*    */   {
/* 80 */     return new TickBarLiveFeedAdapter() { } ;
/*    */   }
/*    */ 
/*    */   protected TickBarData getInProgressBar() {
/* 85 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressTickBar(getInstrument(), getOfferSide(), getJForexPeriod().getTickBarSize());
/*    */   }
/*    */ 
/*    */   protected boolean isInProgressBarLoadingNow()
/*    */   {
/* 90 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressTickBarLoadingNow(getInstrument(), getOfferSide(), getJForexPeriod().getTickBarSize());
/*    */   }
/*    */ 
/*    */   protected void removeInProgressBarListener(ITickBarLiveFeedListener inProgressListener)
/*    */   {
/* 95 */     getFeedDataProvider().getIntraperiodBarsGenerator().removeInProgressTickBarListener(inProgressListener);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.TickBarDataLoadingThread
 * JD-Core Version:    0.6.0
 */