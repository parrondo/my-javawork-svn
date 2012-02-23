/*    */ package com.dukascopy.charts.data.datacache.renko;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ 
/*    */ public class FlowRenkoCreator extends RenkoCreator
/*    */ {
/*    */   public FlowRenkoCreator(Instrument instrument, OfferSide offerSide, PriceRange brickSize)
/*    */   {
/* 21 */     super(instrument, offerSide, brickSize, -1, true, true);
/*    */   }
/*    */ 
/*    */   public boolean isAllDesiredDataLoaded()
/*    */   {
/* 33 */     return false;
/*    */   }
/*    */ 
/*    */   protected void resetResulArray()
/*    */   {
/* 38 */     this.result = new RenkoData[1];
/*    */   }
/*    */ 
/*    */   public int getLastElementIndex()
/*    */   {
/* 43 */     return 0;
/*    */   }
/*    */ 
/*    */   public void setupLastData(RenkoData data)
/*    */   {
/* 48 */     ((RenkoData[])getResult())[(((RenkoData[])getResult()).length - 1)] = data;
/* 49 */     setLastElementIndex(0);
/* 50 */     setLoadedBarsCount(1);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.FlowRenkoCreator
 * JD-Core Version:    0.6.0
 */