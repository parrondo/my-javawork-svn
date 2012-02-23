/*    */ package com.dukascopy.charts.data.datacache.renko;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.feed.IRenkoBar;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.InProgressDataLoadingChecker;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationUtils;
/*    */ 
/*    */ public abstract class AbstractRenkoWithInProgressBarCheckLoader<R>
/*    */   implements IBarsWithInProgressBarCheckLoader<R>
/*    */ {
/*    */   private final IFeedDataProvider feedDataProvider;
/*    */   private final Instrument instrument;
/*    */   private final OfferSide offerSide;
/*    */   private final PriceRange brickSize;
/* 25 */   private final IRenkoLiveFeedListener listener = new RenkoLiveFeedAdapter() { } ;
/*    */ 
/*    */   public AbstractRenkoWithInProgressBarCheckLoader(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange brickSize) {
/* 28 */     this.feedDataProvider = feedDataProvider;
/* 29 */     this.instrument = instrument;
/* 30 */     this.offerSide = offerSide;
/* 31 */     this.brickSize = brickSize;
/*    */   }
/*    */ 
/*    */   public void addInProgressListener()
/*    */   {
/* 36 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressRenkoListener(this.instrument, this.offerSide, this.brickSize, this.listener);
/*    */   }
/*    */ 
/*    */   public void removeInProgressListener() {
/* 40 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressRenkoListener(this.listener);
/*    */   }
/*    */ 
/*    */   private InProgressDataLoadingChecker<IRenkoBar> createInProgressRenkoLoadingChecker(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*    */   {
/* 49 */     return new InProgressDataLoadingChecker(feedDataProvider, instrument, offerSide, brickSize)
/*    */     {
/*    */       public boolean isLoadingInProgress() {
/* 52 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().isInProgressRenkoLoadingNow(this.val$instrument, this.val$offerSide, this.val$brickSize);
/*    */       }
/*    */ 
/*    */       public IRenkoBar getInProgressData() {
/* 56 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().getInProgressRenko(this.val$instrument, this.val$offerSide, this.val$brickSize);
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public boolean doesInProgressBarExistWaitIfCurrentlyLoading() {
/* 63 */     InProgressDataLoadingChecker checker = createInProgressRenkoLoadingChecker(this.feedDataProvider, this.instrument, this.offerSide, this.brickSize);
/* 64 */     return PriceAggregationUtils.checkAndWaitInProgressBarLoaded(checker);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.AbstractRenkoWithInProgressBarCheckLoader
 * JD-Core Version:    0.6.0
 */