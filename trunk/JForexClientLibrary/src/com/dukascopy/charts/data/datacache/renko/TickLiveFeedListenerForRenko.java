/*    */ package com.dukascopy.charts.data.datacache.renko;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationLiveFeedListener;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TickLiveFeedListenerForRenko extends AbstractPriceAggregationLiveFeedListener<RenkoData, TickData, IRenkoLiveFeedListener, IRenkoCreator>
/*    */ {
/*    */   public TickLiveFeedListenerForRenko(IRenkoCreator creator)
/*    */   {
/* 17 */     super(creator);
/*    */   }
/*    */ 
/*    */   public TickLiveFeedListenerForRenko(IRenkoCreator creator, long lastPossibleTime)
/*    */   {
/* 24 */     super(creator, lastPossibleTime);
/*    */   }
/*    */ 
/*    */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*    */   {
/* 36 */     this.collectedDatas.add(new TickData(time, ask, bid, askVol, bidVol));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.TickLiveFeedListenerForRenko
 * JD-Core Version:    0.6.0
 */