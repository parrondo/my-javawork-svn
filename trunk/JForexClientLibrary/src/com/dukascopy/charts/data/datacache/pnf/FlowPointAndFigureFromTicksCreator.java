/*    */ package com.dukascopy.charts.data.datacache.pnf;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.PriceRange;
/*    */ import com.dukascopy.api.ReversalAmount;
/*    */ 
/*    */ public class FlowPointAndFigureFromTicksCreator extends PointAndFigureCreator
/*    */ {
/*    */   public FlowPointAndFigureFromTicksCreator(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount, OfferSide offerSide)
/*    */   {
/* 20 */     super(instrument, priceRange, reversalAmount, offerSide, 2147483647, true, true, null);
/*    */   }
/*    */ 
/*    */   protected void resetResultArray()
/*    */   {
/* 34 */     this.result = new PointAndFigureData[1];
/*    */   }
/*    */ 
/*    */   public int getLastElementIndex()
/*    */   {
/* 39 */     return 0;
/*    */   }
/*    */ 
/*    */   public void setupLastData(PointAndFigureData data)
/*    */   {
/* 44 */     super.setupLastData(data);
/* 45 */     this.currentBoxColumn = data;
/*    */   }
/*    */ 
/*    */   public boolean isAllDesiredDataLoaded()
/*    */   {
/* 50 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.pnf.FlowPointAndFigureFromTicksCreator
 * JD-Core Version:    0.6.0
 */