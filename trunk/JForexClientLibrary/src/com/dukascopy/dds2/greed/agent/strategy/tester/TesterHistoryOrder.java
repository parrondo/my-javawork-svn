/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.IEngine.OrderCommand;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.impl.HistoryOrder;
/*    */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*    */ import java.util.Currency;
/*    */ 
/*    */ public class TesterHistoryOrder extends HistoryOrder
/*    */ {
/*    */   private AbstractCurrencyConverter currencyConverter;
/*    */ 
/*    */   public TesterHistoryOrder(Instrument instrument, String label, String id, long fillTime, long closeTime, IEngine.OrderCommand orderCommand, double filledAmount, double openPrice, double closePrice, String comment, Currency accountCurrency, AbstractCurrencyConverter currencyConverter, double commission)
/*    */   {
/* 22 */     super(instrument, label, id, fillTime, closeTime, orderCommand, filledAmount, openPrice, closePrice, comment, accountCurrency, commission);
/* 23 */     this.currencyConverter = currencyConverter;
/*    */   }
/*    */ 
/*    */   protected AbstractCurrencyConverter getCurrencyConverter()
/*    */   {
/* 28 */     return this.currencyConverter;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterHistoryOrder
 * JD-Core Version:    0.6.0
 */