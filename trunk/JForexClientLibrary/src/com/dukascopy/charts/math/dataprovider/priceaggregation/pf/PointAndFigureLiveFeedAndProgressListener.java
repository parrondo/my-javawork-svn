/*    */ package com.dukascopy.charts.math.dataprovider.priceaggregation.pf;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*    */ import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
/*    */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*    */ import com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationLiveFeedAndProgressListener;
/*    */ 
/*    */ public class PointAndFigureLiveFeedAndProgressListener extends AbstractPriceAggregationLiveFeedAndProgressListener<PointAndFigureData>
/*    */   implements IPointAndFigureLiveFeedListener
/*    */ {
/*    */   public PointAndFigureLiveFeedAndProgressListener(LoadingProgressListener delegate)
/*    */   {
/* 18 */     super(delegate);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureLiveFeedAndProgressListener
 * JD-Core Version:    0.6.0
 */