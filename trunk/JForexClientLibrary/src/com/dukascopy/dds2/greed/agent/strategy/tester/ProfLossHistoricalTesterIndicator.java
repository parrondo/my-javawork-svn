/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*    */ import com.dukascopy.api.indicators.OutputParameterInfo.Type;
/*    */ 
/*    */ public class ProfLossHistoricalTesterIndicator extends AbstractHistoricalTesterIndicator
/*    */ {
/*    */   public ProfLossHistoricalTesterIndicator(long initialTime, double deposit)
/*    */   {
/* 11 */     super(initialTime, deposit);
/*    */   }
/*    */ 
/*    */   protected void prepareNameTitle()
/*    */   {
/* 16 */     this.name = "Profit/Loss";
/*    */   }
/*    */ 
/*    */   protected OutputParameterInfo[] createOutputParamsInfo()
/*    */   {
/* 22 */     return new OutputParameterInfo[] { new OutputParameterInfo("Profit/Loss", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, true) };
/*    */   }
/*    */ 
/*    */   protected double getIndicatorDataValue(StrategyDataStorageImpl.TesterIndicatorData data)
/*    */   {
/* 27 */     return data.profitLoss;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.ProfLossHistoricalTesterIndicator
 * JD-Core Version:    0.6.0
 */