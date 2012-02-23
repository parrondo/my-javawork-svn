/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.impl.IndicatorContext;
/*    */ import com.dukascopy.api.impl.IndicatorHolder;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.api.indicators.IIndicator;
/*    */ 
/*    */ public class TesterIndicatorWrapper extends IndicatorWrapper
/*    */ {
/*    */   public TesterIndicatorWrapper(AbstractTesterIndicator indicator, IndicatorContext ctx)
/*    */   {
/* 16 */     super(new IndicatorHolder(indicator, ctx));
/*    */   }
/*    */ 
/*    */   public TesterIndicatorWrapper(AbstractTesterIndicator indicator, int id, IndicatorContext ctx) {
/* 20 */     super(new IndicatorHolder(indicator, ctx), id);
/*    */   }
/*    */ 
/*    */   public AbstractTesterIndicator getIndicator()
/*    */   {
/* 25 */     return (AbstractTesterIndicator)super.getIndicator();
/*    */   }
/*    */ 
/*    */   public IndicatorWrapper clone()
/*    */   {
/* 36 */     IIndicator indicator = getIndicator().clone();
/* 37 */     TesterIndicatorWrapper clone = (TesterIndicatorWrapper)super.clone();
/* 38 */     clone.indicatorHolder = new IndicatorHolder(indicator, null);
/* 39 */     return clone;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicatorWrapper
 * JD-Core Version:    0.6.0
 */