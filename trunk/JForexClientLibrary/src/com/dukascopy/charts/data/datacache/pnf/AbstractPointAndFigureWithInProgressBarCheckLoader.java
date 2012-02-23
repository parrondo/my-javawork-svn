/*    */ package com.dukascopy.charts.data.datacache.pnf;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.ReversalAmount;
/*    */ import com.dukascopy.api.feed.IPointAndFigure;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IBarsWithInProgressBarCheckLoader;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.InProgressDataLoadingChecker;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.PriceAggregationUtils;
/*    */ 
/*    */ public abstract class AbstractPointAndFigureWithInProgressBarCheckLoader<R>
/*    */   implements IBarsWithInProgressBarCheckLoader<R>
/*    */ {
/*    */   private final IFeedDataProvider feedDataProvider;
/*    */   private final Instrument instrument;
/*    */   private final OfferSide offerSide;
/*    */   private final PriceRange priceRange;
/*    */   private final ReversalAmount reversalAmount;
/* 27 */   private final IPointAndFigureLiveFeedListener listener = new PointAndFigureLiveFeedAdapter() { } ;
/*    */ 
/*    */   public AbstractPointAndFigureWithInProgressBarCheckLoader(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount) {
/* 30 */     this.feedDataProvider = feedDataProvider;
/* 31 */     this.instrument = instrument;
/* 32 */     this.offerSide = offerSide;
/* 33 */     this.priceRange = priceRange;
/* 34 */     this.reversalAmount = reversalAmount;
/*    */   }
/*    */ 
/*    */   public void addInProgressListener()
/*    */   {
/* 39 */     this.feedDataProvider.getIntraperiodBarsGenerator().addInProgressPointAndFigureListener(this.instrument, this.offerSide, this.priceRange, this.reversalAmount, this.listener);
/*    */   }
/*    */ 
/*    */   public void removeInProgressListener() {
/* 43 */     this.feedDataProvider.getIntraperiodBarsGenerator().removeInProgressPointAndFigureListener(this.listener);
/*    */   }
/*    */ 
/*    */   private boolean doesInProgressPointAndFigureExist(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*    */   {
/* 53 */     InProgressDataLoadingChecker ckecker = createInProgressPointAndFigureLoadingChecker(feedDataProvider, instrument, offerSide, priceRange, reversalAmount);
/*    */ 
/* 60 */     return PriceAggregationUtils.checkAndWaitInProgressBarLoaded(ckecker);
/*    */   }
/*    */ 
/*    */   private InProgressDataLoadingChecker<IPointAndFigure> createInProgressPointAndFigureLoadingChecker(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount)
/*    */   {
/* 70 */     return new InProgressDataLoadingChecker(feedDataProvider, instrument, offerSide, priceRange, reversalAmount)
/*    */     {
/*    */       public boolean isLoadingInProgress() {
/* 73 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().isInProgressPointAndFigureLoadingNow(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount);
/*    */       }
/*    */ 
/*    */       public IPointAndFigure getInProgressData() {
/* 77 */         return this.val$feedDataProvider.getIntraperiodBarsGenerator().getInProgressPointAndFigure(this.val$instrument, this.val$offerSide, this.val$priceRange, this.val$reversalAmount);
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public boolean doesInProgressBarExistWaitIfCurrentlyLoading() {
/* 84 */     return doesInProgressPointAndFigureExist(this.feedDataProvider, this.instrument, this.offerSide, this.priceRange, this.reversalAmount);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.AbstractPointAndFigureWithInProgressBarCheckLoader
 * JD-Core Version:    0.6.0
 */