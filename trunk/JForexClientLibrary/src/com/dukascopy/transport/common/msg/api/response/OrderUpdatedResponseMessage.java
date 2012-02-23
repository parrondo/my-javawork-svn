/*     */ package com.dukascopy.transport.common.msg.api.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class OrderUpdatedResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "order_updated_response";
/*  18 */   private static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
/*     */ 
/*     */   public OrderUpdatedResponseMessage()
/*     */   {
/*  26 */     setType("order_updated_response");
/*     */   }
/*     */ 
/*     */   public OrderUpdatedResponseMessage(ProtocolMessage message)
/*     */   {
/*  35 */     super(message);
/*     */ 
/*  37 */     setType("order_updated_response");
/*     */ 
/*  39 */     setOrderId(message.getString("orderId"));
/*  40 */     setLots(message.getBigDecimal("lots"));
/*  41 */     setEntryPrice(message.getBigDecimal("entryPrice"));
/*  42 */     setEntryTimestamp(message.getDate("entryTimestamp"));
/*  43 */     setSide(message.getString("side"));
/*  44 */     setInstrument(message.getString("instrument"));
/*  45 */     setTakeProfitPrice(message.getBigDecimal("takeProfitPrice"));
/*  46 */     setStopLossPrice(message.getBigDecimal("stopLossPrice"));
/*     */   }
/*     */ 
/*     */   public OrderUpdatedResponseMessage(Position position, BigDecimal takeProfit, BigDecimal stopLoss)
/*     */   {
/*  55 */     setType("order_updated_response");
/*     */ 
/*  57 */     setOrderId(position.getPositionID());
/*  58 */     setLots(position.getAmount().getValue().divide(ONE_MILLION));
/*  59 */     setEntryPrice(position.getPriceOpen().getValue());
/*  60 */     setEntryTimestamp(new Date());
/*  61 */     setSide(PositionSide.LONG.equals(position.getPositionSide()) ? "BUY" : "SELL");
/*  62 */     setInstrument(position.getInstrument());
/*  63 */     setTakeProfitPrice(takeProfit);
/*  64 */     setStopLossPrice(stopLoss);
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/*  68 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getOrderId() {
/*  72 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setLots(BigDecimal lots) {
/*  76 */     put("lots", lots);
/*     */   }
/*     */ 
/*     */   public BigDecimal getLots() {
/*  80 */     return getBigDecimal("lots");
/*     */   }
/*     */ 
/*     */   public void setEntryPrice(BigDecimal entryPrice) {
/*  84 */     put("entryPrice", entryPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getEntryPrice() {
/*  88 */     return getBigDecimal("entryPrice");
/*     */   }
/*     */ 
/*     */   public void setEntryTimestamp(Date entryTimestamp) {
/*  92 */     putDate("entryTimestamp", entryTimestamp);
/*     */   }
/*     */ 
/*     */   public Date getEntryTimestamp() {
/*  96 */     return getDate("entryTimestamp");
/*     */   }
/*     */ 
/*     */   public void setSide(String side) {
/* 100 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public String getSide() {
/* 104 */     return getString("side");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 108 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 112 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPrice(BigDecimal takeProfitPrice) {
/* 116 */     put("takeProfitPrice", takeProfitPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getTakeProfitPrice() {
/* 120 */     return getBigDecimal("takeProfitPrice");
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(BigDecimal stopLossPrice) {
/* 124 */     put("stopLossPrice", stopLossPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopLossPrice() {
/* 128 */     return getBigDecimal("stopLossPrice");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.OrderUpdatedResponseMessage
 * JD-Core Version:    0.6.0
 */