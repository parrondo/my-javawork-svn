/*    */ package com.dukascopy.charts.orders.orderparts;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class EntryLine extends HorizontalLine
/*    */ {
/*    */   private boolean isOco;
/*    */ 
/*    */   public EntryLine(String orderGroupId, ITheme theme)
/*    */   {
/* 15 */     super(orderGroupId, theme);
/*    */   }
/*    */ 
/*    */   public String getText(BigDecimal price)
/*    */   {
/* 20 */     return OrderHistoricalData.getOpenPriceText(getOrderGroupId(), this.isOco, getOpenOrderCommand(), price, getSlippage());
/*    */   }
/*    */ 
/*    */   public boolean isOco() {
/* 24 */     return this.isOco;
/*    */   }
/*    */ 
/*    */   public void setOco(boolean oco) {
/* 28 */     this.isOco = oco;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.EntryLine
 * JD-Core Version:    0.6.0
 */