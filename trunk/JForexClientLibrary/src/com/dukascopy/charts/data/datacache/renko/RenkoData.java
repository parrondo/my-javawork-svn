/*    */ package com.dukascopy.charts.data.datacache.renko;
/*    */ 
/*    */ import com.dukascopy.api.feed.IRenkoBar;
/*    */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*    */ 
/*    */ public class RenkoData extends AbstractPriceAggregationData
/*    */   implements IRenkoBar
/*    */ {
/*    */   private IRenkoBar inProgressBar;
/*    */   private Boolean rising;
/*    */ 
/*    */   public RenkoData()
/*    */   {
/*    */   }
/*    */ 
/*    */   public RenkoData(long time, long endTime, double open, double close, double low, double high, double vol, long formedElementsCount)
/*    */   {
/* 37 */     super(time, endTime, open, close, low, high, vol, formedElementsCount);
/*    */   }
/*    */ 
/*    */   public RenkoData clone()
/*    */   {
/* 42 */     return (RenkoData)super.clone();
/*    */   }
/*    */ 
/*    */   public void setInProgressBar(IRenkoBar inProgressBar) {
/* 46 */     this.inProgressBar = inProgressBar;
/*    */   }
/*    */ 
/*    */   public IRenkoBar getInProgressBar()
/*    */   {
/* 51 */     return this.inProgressBar;
/*    */   }
/*    */ 
/*    */   public RenkoData getInProgressRenko()
/*    */   {
/* 58 */     return (RenkoData)getInProgressBar();
/*    */   }
/*    */ 
/*    */   public Boolean isRising() {
/* 62 */     return this.rising;
/*    */   }
/*    */ 
/*    */   public void setRising(Boolean rising) {
/* 66 */     this.rising = rising;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.renko.RenkoData
 * JD-Core Version:    0.6.0
 */