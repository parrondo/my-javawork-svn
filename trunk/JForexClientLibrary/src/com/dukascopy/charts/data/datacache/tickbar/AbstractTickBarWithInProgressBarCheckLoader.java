/*    */ package com.dukascopy.charts.data.datacache.tickbar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.TickBarSize;
/*    */ import com.dukascopy.api.feed.ITickBar;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.InProgressDataLoadingChecker;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationUtils;
/*    */ 
/*    */ public abstract class AbstractTickBarWithInProgressBarCheckLoader<R>
/*    */   implements IBarsWithInProgressBarCheckLoader<R>
/*    */ {
/*    */   private final IFeedDataProvider feedDataProvider;
/*    */   private final Instrument instrument;
/*    */   private final OfferSide offerSide;
/*    */   private final TickBarSize tickBarSize;
/* 25 */   private final ITickBarLiveFeedListener listener = new TickBarLiveFeedAdapter() { } ;
/*    */ 
/*    */   public AbstractTickBarWithInProgressBarCheckLoader(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize) {
/* 28 */     this.feedDataProvider = feedDataProvider;
/* 29 */     this.instrument = instrument;
/* 30 */     this.offerSide = offerSide;
/* 31 */     this.tickBarSize = tickBarSize;
/*    */   }
/*    */ 
/*    */   public void addInProgressListener()
/*    */   {
/* 36 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressTickBarListener(this.instrument, this.offerSide, this.tickBarSize, this.listener);
/*    */   }
/*    */ 
/*    */   public boolean doesInProgressBarExistWaitIfCurrentlyLoading()
/*    */   {
/* 41 */     return doesInProgressTickBarExist(this.feedDataProvider, this.instrument, this.offerSide, this.tickBarSize);
/*    */   }
/*    */ 
/*    */   public void removeInProgressListener()
/*    */   {
/* 46 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressTickBarListener(this.listener);
/*    */   }
/*    */ 
/*    */   private boolean doesInProgressTickBarExist(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*    */   {
/* 55 */     InProgressDataLoadingChecker ckecker = createInProgressTickBarLoadingChecker(feedDataProvider, instrument, offerSide, tickBarSize);
/*    */ 
/* 61 */     return PriceAggregationUtils.checkAndWaitInProgressBarLoaded(ckecker);
/*    */   }
/*    */ 
/*    */   private InProgressDataLoadingChecker<ITickBar> createInProgressTickBarLoadingChecker(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize)
/*    */   {
/* 70 */     return new InProgressDataLoadingChecker(feedDataProvider, instrument, offerSide, tickBarSize)
/*    */     {
/*    */       public boolean isLoadingInProgress() {
/* 73 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().isInProgressTickBarLoadingNow(this.val$instrument, this.val$offerSide, this.val$tickBarSize);
/*    */       }
/*    */ 
/*    */       public ITickBar getInProgressData() {
/* 77 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().getInProgressTickBar(this.val$instrument, this.val$offerSide, this.val$tickBarSize);
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.AbstractTickBarWithInProgressBarCheckLoader
 * JD-Core Version:    0.6.0
 */