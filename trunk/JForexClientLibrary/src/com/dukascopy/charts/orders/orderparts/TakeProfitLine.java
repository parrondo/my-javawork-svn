/*    */ package com.dukascopy.charts.orders.orderparts;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class TakeProfitLine extends HorizontalLine
/*    */ {
/*    */   public TakeProfitLine(String orderGroupId, ITheme theme)
/*    */   {
/* 14 */     super(orderGroupId, theme);
/*    */   }
/*    */ 
/*    */   public String getText(BigDecimal price)
/*    */   {
/* 19 */     return OrderHistoricalData.getTakeProfitPriceText(getOrderId(), getOpenOrderCommand(), price, getSlippage());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.TakeProfitLine
 * JD-Core Version:    0.6.0
 */