/*    */ package com.dukascopy.charts.data.datacache.intraperiod.listener;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;
/*    */ 
/*    */ public abstract class LastAbstractPriceAggregationLiveFeedListener<T extends AbstractPriceAggregationData>
/*    */   implements IPriceAggregationLiveFeedListener<T>
/*    */ {
/*    */   private T lastData;
/*    */ 
/*    */   public void newPriceData(T pointAndFigure)
/*    */   {
/* 19 */     this.lastData = pointAndFigure;
/*    */   }
/*    */ 
/*    */   public T getLastData() {
/* 23 */     return this.lastData;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.intraperiod.listener.LastAbstractPriceAggregationLiveFeedListener
 * JD-Core Version:    0.6.0
 */