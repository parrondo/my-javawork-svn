/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester.dataload;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureLiveFeedAdapter;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ 
/*    */ public class PointAndFigureDataLoadingThread extends AbstractPriceAggregationDataLoadingThread<PointAndFigureData, IPointAndFigureLiveFeedListener>
/*    */ {
/*    */   public PointAndFigureDataLoadingThread(String name, Instrument instrument, JForexPeriod jForexPeriod, OfferSide offerSide, BlockingQueue<PointAndFigureData> queue, long from, long to, IFeedDataProvider feedDataProvider)
/*    */   {
/* 30 */     super(name, instrument, jForexPeriod, offerSide, queue, from, to, feedDataProvider);
/*    */   }
/*    */ 
/*    */   protected void executeRun()
/*    */   {
/* 44 */     IPointAndFigureLiveFeedListener liveFeedListener = new IPointAndFigureLiveFeedListener()
/*    */     {
/*    */       public void newPriceData(PointAndFigureData pointAndFigure) {
/* 47 */         if (!PointAndFigureDataLoadingThread.this.isStop())
/* 48 */           PointAndFigureDataLoadingThread.this.putDataToQueue(pointAndFigure);
/*    */       }
/*    */     };
/* 53 */     LoadingProgressListener progressListener = createLoadingProgressListener();
/*    */ 
/* 55 */     long toTime = checkToTimeRightBound(getTo());
/*    */ 
/* 57 */     getFeedDataProvider().getPriceAggregationDataProvider().loadPointAndFigureTimeIntervalSynched(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), getJForexPeriod().getReversalAmount(), getFrom(), toTime, liveFeedListener, progressListener);
/*    */   }
/*    */ 
/*    */   protected PointAndFigureData createEmptyBar()
/*    */   {
/* 71 */     return new PointAndFigureData(-9223372036854775808L, -9223372036854775808L, -1.0D, -1.0D, -1.0D, -1.0D, -1.0D, -1L, null);
/*    */   }
/*    */ 
/*    */   protected void addInProgressBarListener(IPointAndFigureLiveFeedListener inProgressListener)
/*    */   {
/* 76 */     getFeedDataProvider().getIntraperiodBarsGenerator().addInProgressPointAndFigureListener(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), getJForexPeriod().getReversalAmount(), inProgressListener);
/*    */   }
/*    */ 
/*    */   protected IPointAndFigureLiveFeedListener createInProgressAdapter()
/*    */   {
/* 81 */     return new PointAndFigureLiveFeedAdapter() { } ;
/*    */   }
/*    */ 
/*    */   protected PointAndFigureData getInProgressBar() {
/* 86 */     return getFeedDataProvider().getIntraperiodBarsGenerator().getInProgressPointAndFigure(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), getJForexPeriod().getReversalAmount());
/*    */   }
/*    */ 
/*    */   protected boolean isInProgressBarLoadingNow()
/*    */   {
/* 91 */     return getFeedDataProvider().getIntraperiodBarsGenerator().isInProgressPointAndFigureLoadingNow(getInstrument(), getOfferSide(), getJForexPeriod().getPriceRange(), getJForexPeriod().getReversalAmount());
/*    */   }
/*    */ 
/*    */   protected void removeInProgressBarListener(IPointAndFigureLiveFeedListener inProgressListener)
/*    */   {
/* 96 */     getFeedDataProvider().getIntraperiodBarsGenerator().removeInProgressPointAndFigureListener(inProgressListener);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.dataload.PointAndFigureDataLoadingThread
 * JD-Core Version:    0.6.0
 */