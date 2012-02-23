/*    */ package com.dukascopy.charts.data.datacache.priceaggregation;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.charts.data.datacache.CurvesDataLoader.IntraperiodExistsPolicy;
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class AbstractLoadPriceAggregationByTimeRangeAction<D extends AbstractPriceAggregationData, SD extends Data, L extends IPriceAggregationLiveFeedListener<D>, C extends IPriceAggregationCreator<D, SD, L>> extends AbstractLoadNumberOfPriceAggregationAction<D, SD, L, C>
/*    */ {
/*    */   private final long fromTime;
/* 28 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLoadPriceAggregationByTimeRangeAction.class);
/*    */ 
/*    */   public AbstractLoadPriceAggregationByTimeRangeAction(IFeedDataProvider feedDataProvider, Instrument instrument, OfferSide offerSide, long fromTime, long toTime, L liveFeedListener, LoadingProgressListener loadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy)
/*    */   {
/* 40 */     super(feedDataProvider, instrument, offerSide, -1, toTime, -1, liveFeedListener, loadingProgressListener, intraperiodExistsPolicy);
/*    */ 
/* 52 */     this.fromTime = fromTime;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 57 */     Throwable exception = null;
/* 58 */     boolean allDataLoaded = false;
/*    */     try
/*    */     {
/* 61 */       performDirectBarsLoadForTimeInterval(getFromTime(), getToTime(), true, true);
/* 62 */       allDataLoaded = true;
/*    */     }
/*    */     catch (Throwable e)
/*    */     {
/*    */       Exception e;
/* 64 */       LOGGER.error(t.getMessage(), t);
/* 65 */       exception = t;
/*    */     }
/*    */     finally
/*    */     {
/*    */       Exception e;
/* 67 */       Exception e = null;
/* 68 */       if (exception != null) {
/* 69 */         if ((exception instanceof Exception)) {
/* 70 */           e = (Exception)exception;
/*    */         }
/*    */         else {
/* 73 */           e = new Exception(exception);
/*    */         }
/*    */       }
/* 76 */       getLoadingProgress().loadingFinished(allDataLoaded, getFromTime(), getToTime(), getToTime(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public long getFromTime()
/*    */   {
/* 87 */     return this.fromTime;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.AbstractLoadPriceAggregationByTimeRangeAction
 * JD-Core Version:    0.6.0
 */