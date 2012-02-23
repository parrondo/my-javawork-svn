/*    */ package com.dukascopy.charts.data.datacache.tickbar;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.TickBarSize;
/*    */ 
/*    */ public class FlowTickBarCreator extends TickBarCreator
/*    */ {
/*    */   public FlowTickBarCreator(Instrument instrument, TickBarSize tickBarSize, OfferSide offerSide)
/*    */   {
/* 18 */     super(instrument, tickBarSize, offerSide, 1, true, true, null);
/*    */   }
/*    */ 
/*    */   protected void resetResulArray()
/*    */   {
/* 31 */     this.result = new TickBarData[1];
/*    */   }
/*    */ 
/*    */   public int getLastElementIndex()
/*    */   {
/* 36 */     return 0;
/*    */   }
/*    */ 
/*    */   public void setupLastData(TickBarData data)
/*    */   {
/* 41 */     this.lastElementIndex = -1;
/* 42 */     this.fullyCreatedBarsCount = 0;
/* 43 */     this.currentDataConstructionTicksIncluded = 0;
/* 44 */     ((TickBarData[])getResult())[0] = data;
/*    */   }
/*    */ 
/*    */   public boolean isAllDesiredDataLoaded()
/*    */   {
/* 49 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.tickbar.FlowTickBarCreator
 * JD-Core Version:    0.6.0
 */