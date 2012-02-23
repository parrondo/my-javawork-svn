/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*    */ 
/*    */ public class EquityHistoricalTesterIndicator extends AbstractHistoricalTesterIndicator
/*    */ {
/*    */   public EquityHistoricalTesterIndicator(long initialTime, double deposit)
/*    */   {
/* 14 */     super(initialTime, deposit);
/*    */   }
/*    */ 
/*    */   protected void prepareNameTitle()
/*    */   {
/* 19 */     this.name = "Equity";
/*    */   }
/*    */ 
/*    */   protected OutputParameterInfo[] createOutputParamsInfo()
/*    */   {
/* 24 */     return new OutputParameterInfo[] { new OutputParameterInfo("Equity", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, true) };
/*    */   }
/*    */ 
/*    */   protected double getIndicatorDataValue(StrategyDataStorageImpl.TesterIndicatorData data)
/*    */   {
/* 29 */     return data.equity;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.EquityHistoricalTesterIndicator
 * JD-Core Version:    0.6.0
 */