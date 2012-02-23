/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.Filter;
/*    */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.api.OfferSide;
/*    */ import com.dukascopy.api.Period;
/*    */ import com.dukascopy.api.impl.History;
/*    */ import com.dukascopy.api.impl.Indicators;
/*    */ 
/*    */ public class TesterIndicators extends Indicators
/*    */ {
/*    */   private IStrategyRunner strategyRunner;
/*    */ 
/*    */   public TesterIndicators(History history, IStrategyRunner strategyRunner)
/*    */   {
/* 18 */     super(history);
/* 19 */     this.strategyRunner = strategyRunner;
/*    */   }
/*    */ 
/*    */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, int shift) throws JFException
/*    */   {
/* 24 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*    */     try {
/* 26 */       Object[] arrayOfObject = super.calculateIndicator(instrument, period, side, functionName, inputTypes, optParams, shift);
/*    */       return arrayOfObject; } finally { this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.INDICATOR_CALLS); } throw localObject;
/*    */   }
/*    */ 
/*    */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, long from, long to)
/*    */     throws JFException
/*    */   {
/* 34 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*    */     try {
/* 36 */       Object[] arrayOfObject = super.calculateIndicator(instrument, period, side, functionName, inputTypes, optParams, from, to);
/*    */       return arrayOfObject; } finally { this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.INDICATOR_CALLS); } throw localObject;
/*    */   }
/*    */ 
/*    */   public Object[] calculateIndicator(Instrument instrument, Period period, OfferSide[] side, String functionName, IIndicators.AppliedPrice[] inputTypes, Object[] optParams, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter)
/*    */     throws JFException
/*    */   {
/* 44 */     long perfStatTimeStart = this.strategyRunner.perfStartTime();
/*    */     try {
/* 46 */       Object[] arrayOfObject = super.calculateIndicator(instrument, period, side, functionName, inputTypes, optParams, filter, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*    */       return arrayOfObject; } finally { this.strategyRunner.perfStopTime(perfStatTimeStart, ITesterReport.PerfStats.INDICATOR_CALLS); } throw localObject;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicators
 * JD-Core Version:    0.6.0
 */