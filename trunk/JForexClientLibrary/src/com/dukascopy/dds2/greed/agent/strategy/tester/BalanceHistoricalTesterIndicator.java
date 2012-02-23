/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*    */ 
/*    */ public class BalanceHistoricalTesterIndicator extends AbstractHistoricalTesterIndicator
/*    */ {
/*    */   public BalanceHistoricalTesterIndicator(long initialTime, double deposit)
/*    */   {
/* 11 */     super(initialTime, deposit);
/*    */   }
/*    */ 
/*    */   protected void prepareNameTitle()
/*    */   {
/* 16 */     this.name = "Balance";
/*    */   }
/*    */ 
/*    */   protected OutputParameterInfo[] createOutputParamsInfo()
/*    */   {
/* 21 */     return new OutputParameterInfo[] { new OutputParameterInfo("Balance", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, true) };
/*    */   }
/*    */ 
/*    */   protected double getIndicatorDataValue(StrategyDataStorageImpl.TesterIndicatorData data)
/*    */   {
/* 26 */     return data.balance;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.BalanceHistoricalTesterIndicator
 * JD-Core Version:    0.6.0
 */