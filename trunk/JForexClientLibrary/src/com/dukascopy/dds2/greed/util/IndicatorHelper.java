/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import com.dukascopy.api.impl.History;
/*    */ import com.dukascopy.api.impl.IndicatorContext;
/*    */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*    */ import java.util.Currency;
/*    */ 
/*    */ public final class IndicatorHelper
/*    */ {
/*    */   private static Currency accountCurrency;
/*    */ 
/*    */   public static void setAccountCurrency(Currency accountCurrency)
/*    */   {
/* 22 */     accountCurrency = accountCurrency;
/*    */   }
/*    */ 
/*    */   public static IndicatorContext createIndicatorContext()
/*    */   {
/* 29 */     History history = new History(OrdersProvider.getInstance(), accountCurrency);
/* 30 */     return new IndicatorContext(NotificationUtilsProvider.getNotificationUtils(), history);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.IndicatorHelper
 * JD-Core Version:    0.6.0
 */