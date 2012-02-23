/*    */ package com.dukascopy.charts.data.datacache.rangebar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TickLiveFeedListenerForRangeBars extends AbstractPriceAggregationLiveFeedListener<PriceRangeData, TickData, IPriceRangeLiveFeedListener, IPriceRangeCreator>
/*    */ {
/*    */   public TickLiveFeedListenerForRangeBars(IPriceRangeCreator creator)
/*    */   {
/* 14 */     super(creator);
/*    */   }
/*    */ 
/*    */   public TickLiveFeedListenerForRangeBars(IPriceRangeCreator creator, long lastPossibleTime)
/*    */   {
/* 21 */     super(creator, lastPossibleTime);
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/* 33 */     this.collectedDatas.add(new TickData(time, ask, bid, askVol, bidVol));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.TickLiveFeedListenerForRangeBars
 * JD-Core Version:    0.6.0
 */