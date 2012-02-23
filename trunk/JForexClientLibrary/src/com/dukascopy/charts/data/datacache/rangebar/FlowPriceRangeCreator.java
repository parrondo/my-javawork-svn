/*    */ package com.dukascopy.charts.data.datacache.rangebar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ 
/*    */ public class FlowPriceRangeCreator extends PriceRangeCreator
/*    */ {
/*    */   public FlowPriceRangeCreator(Instrument instrument, OfferSide offerSide, PriceRange priceRange)
/*    */   {
/* 18 */     super(instrument, priceRange, offerSide, 2147483647, true, true, null);
/*    */   }
/*    */ 
/*    */   public boolean isAllDesiredDataLoaded()
/*    */   {
/* 31 */     return false;
/*    */   }
/*    */ 
/*    */   protected void resetResulArray()
/*    */   {
/* 36 */     this.result = new PriceRangeData[1];
/*    */   }
/*    */ 
/*    */   public int getLastElementIndex()
/*    */   {
/* 41 */     return 0;
/*    */   }
/*    */ 
/*    */   public void setupLastData(PriceRangeData data)
/*    */   {
/* 46 */     ((PriceRangeData[])getResult())[(((PriceRangeData[])getResult()).length - 1)] = data;
/* 47 */     setLastElementIndex(0);
/* 48 */     setLoadedPriceRangeCount(1);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.rangebar.FlowPriceRangeCreator
 * JD-Core Version:    0.6.0
 */