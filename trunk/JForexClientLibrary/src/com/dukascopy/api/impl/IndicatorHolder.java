/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.api.indicators.IIndicator;
/*    */ 
/*    */ public class IndicatorHolder
/*    */ {
/*    */   private IIndicator indicator;
/*    */   private IndicatorContext indicatorContext;
/*    */ 
/*    */   public IndicatorHolder(IIndicator indicator, IndicatorContext indicatorContext)
/*    */   {
/* 17 */     this.indicator = indicator;
/* 18 */     this.indicatorContext = indicatorContext;
/*    */   }
/*    */ 
/*    */   public IIndicator getIndicator() {
/* 22 */     return this.indicator;
/*    */   }
/*    */ 
/*    */   public IndicatorContext getIndicatorContext() {
/* 26 */     return this.indicatorContext;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.IndicatorHolder
 * JD-Core Version:    0.6.0
 */