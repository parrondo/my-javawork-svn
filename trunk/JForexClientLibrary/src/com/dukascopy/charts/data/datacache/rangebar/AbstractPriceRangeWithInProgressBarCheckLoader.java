/*    */ package com.dukascopy.charts.data.datacache.rangebar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.feed.IRangeBar;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.InProgressDataLoadingChecker;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationUtils;
/*    */ 
/*    */ public abstract class AbstractPriceRangeWithInProgressBarCheckLoader<R>
/*    */   implements IBarsWithInProgressBarCheckLoader<R>
/*    */ {
/*    */   private final IFeedDataProvider feedDataProvider;
/*    */   private final Instrument instrument;
/*    */   private final OfferSide offerSide;
/*    */   private final PriceRange priceRange;
/* 25 */   private final IPriceRangeLiveFeedListener listener = new PriceRangeLiveFeedAdapter() { } ;
/*    */ 
/*    */   public AbstractPriceRangeWithInProgressBarCheckLoader(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange) {
/* 28 */     this.feedDataProvider = feedDataProvider;
/* 29 */     this.instrument = instrument;
/* 30 */     this.offerSide = offerSide;
/* 31 */     this.priceRange = priceRange;
/*    */   }
/*    */ 
/*    */   public void addInProgressListener()
/*    */   {
/* 36 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressPriceRangeListener(this.instrument, this.offerSide, this.priceRange, this.listener);
/*    */   }
/*    */ 
/*    */   public void removeInProgressListener() {
/* 40 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressPriceRangeListener(this.listener);
/*    */   }
/*    */ 
/*    */   private boolean doesInProgressRangeBarExist(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*    */   {
/* 49 */     InProgressDataLoadingChecker ckecker = createInProgressRangeBarLoadingChecker(feedDataProvider, instrument, offerSide, priceRange);
/*    */ 
/* 55 */     return PriceAggregationUtils.checkAndWaitInProgressBarLoaded(ckecker);
/*    */   }
/*    */ 
/*    */   private InProgressDataLoadingChecker<IRangeBar> createInProgressRangeBarLoadingChecker(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*    */   {
/* 64 */     return new InProgressDataLoadingChecker(feedDataProvider, instrument, offerSide, priceRange)
/*    */     {
/*    */       public boolean isLoadingInProgress() {
/* 67 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().isInProgressPriceRangeLoadingNow(this.val$instrument, this.val$offerSide, this.val$priceRange);
/*    */       }
/*    */ 
/*    */       public IRangeBar getInProgressData() {
/* 71 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().getInProgressPriceRange(this.val$instrument, this.val$offerSide, this.val$priceRange);
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public boolean doesInProgressBarExistWaitIfCurrentlyLoading() {
/* 78 */     return doesInProgressRangeBarExist(this.feedDataProvider, this.instrument, this.offerSide, this.priceRange);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.AbstractPriceRangeWithInProgressBarCheckLoader
 * JD-Core Version:    0.6.0
 */