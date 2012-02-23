/*    */ package com.dukascopy.charts.orders.orderparts;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class StopLossLine extends HorizontalLine
/*    */ {
/*    */   private boolean stopLossByBid;
/*    */ 
/*    */   public StopLossLine(String orderGroupId, ITheme theme)
/*    */   {
/* 15 */     super(orderGroupId, theme);
/*    */   }
/*    */ 
/*    */   public String getText(BigDecimal price)
/*    */   {
/* 20 */     return OrderHistoricalData.getStopLossPriceText(getOrderId(), getOpenOrderCommand(), price, getSlippage(), this.stopLossByBid);
/*    */   }
/*    */ 
/*    */   public boolean isStopLossByBid() {
/* 24 */     return this.stopLossByBid;
/*    */   }
/*    */ 
/*    */   public void setStopLossByBid(boolean stopLossByBid) {
/* 28 */     this.stopLossByBid = stopLossByBid;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.StopLossLine
 * JD-Core Version:    0.6.0
 */