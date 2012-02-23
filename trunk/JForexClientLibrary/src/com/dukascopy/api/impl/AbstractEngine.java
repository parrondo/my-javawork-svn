/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.api.IEngine.OrderCommand;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.api.JFException.Error;
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ 
/*    */ public class AbstractEngine
/*    */ {
/*    */   protected String validateLabel(String label)
/*    */     throws JFException
/*    */   {
/* 16 */     if (label == null) {
/* 17 */       throw new JFException(JFException.Error.LABEL_INCONSISTENT);
/*    */     }
/* 19 */     if (label.length() > 256) {
/* 20 */       throw new JFException(JFException.Error.LABEL_INCONSISTENT);
/*    */     }
/* 22 */     if (!label.matches("^[a-zA-Z_][a-zA-Z0-9_]+")) {
/* 23 */       throw new JFException(JFException.Error.LABEL_INCONSISTENT);
/*    */     }
/* 25 */     return label.trim();
/*    */   }
/*    */ 
/*    */   protected void validateOrder(boolean isGlobal, Instrument instrument, IEngine.OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment)
/*    */     throws JFException
/*    */   {
/* 33 */     if (orderCommand == null) {
/* 34 */       throw new JFException(JFException.Error.COMMAND_IS_NULL);
/*    */     }
/*    */ 
/* 39 */     if ((price == 0.0D) && 
/* 40 */       (orderCommand != IEngine.OrderCommand.BUY) && (orderCommand != IEngine.OrderCommand.SELL)) {
/* 41 */       throw new JFException(JFException.Error.ZERO_PRICE_NOT_ALLOWED);
/*    */     }
/*    */ 
/* 46 */     if ((goodTillTime > 0L) && (orderCommand != IEngine.OrderCommand.PLACE_BID) && (orderCommand != IEngine.OrderCommand.PLACE_OFFER)) {
/* 47 */       throw new JFException("Order should be \"place bid\" or \"place offer\"");
/*    */     }
/*    */ 
/* 50 */     if (isGlobal) {
/* 51 */       if (StratUtils.round(stopLossPrice, 7) > 0.0D) {
/* 52 */         throw new JFException("Stop loss orders are not allowed on global accounts");
/*    */       }
/* 54 */       if (StratUtils.round(takeProfitPrice, 7) > 0.0D)
/* 55 */         throw new JFException("Take profit orders are not allowed on global accounts");
/*    */     }
/*    */   }
/*    */ 
/*    */   protected String generateFeedCommissionWarning(Instrument instrument)
/*    */   {
/* 61 */     return String.format("Unable to submit \"Place bid\" or \"Place offer\" orders. Instrument \"%s\" has feed commissions.", new Object[] { instrument });
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.AbstractEngine
 * JD-Core Version:    0.6.0
 */