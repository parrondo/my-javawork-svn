/*     */ package com.dukascopy.transport.common.msg.api.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.backoffice.type.Position;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class OrderCancelledResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "order_cancelled";
/*     */ 
/*     */   public OrderCancelledResponseMessage()
/*     */   {
/*  24 */     setType("order_cancelled");
/*     */   }
/*     */ 
/*     */   public OrderCancelledResponseMessage(ProtocolMessage message)
/*     */   {
/*  33 */     super(message);
/*     */ 
/*  35 */     setType("order_cancelled");
/*     */ 
/*  37 */     setOrderId(message.getString("orderId"));
/*  38 */     setLots(message.getBigDecimal("lots"));
/*  39 */     setEntryPrice(message.getBigDecimal("entryPrice"));
/*  40 */     setEntryTimestamp(message.getDate("entryTimestamp"));
/*  41 */     setSide(message.getString("side"));
/*  42 */     setInstrument(message.getString("instrument"));
/*  43 */     setTakeProfitPrice(message.getBigDecimal("takeProfitPrice"));
/*  44 */     setStopLossPrice(message.getBigDecimal("stopLossPrice"));
/*     */   }
/*     */ 
/*     */   public OrderCancelledResponseMessage(Position position)
/*     */   {
/*  53 */     setType("order_cancelled");
/*     */ 
/*  55 */     setOrderId(Integer.toString(position.getPositionId()));
/*  56 */     setLots(position.getLots());
/*  57 */     setEntryPrice(position.getOpenPrice());
/*  58 */     setEntryTimestamp(new Date());
/*  59 */     setSide(position.isUp() ? "BUY" : "SELL");
/*  60 */     setInstrument(position.getInstrument());
/*  61 */     setTakeProfitPrice(position.getTakeProfitPrice());
/*  62 */     setStopLossPrice(position.getStopLossPrice());
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/*  66 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getOrderId() {
/*  70 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setLots(BigDecimal lots) {
/*  74 */     put("lots", lots);
/*     */   }
/*     */ 
/*     */   public BigDecimal getLots() {
/*  78 */     return getBigDecimal("lots");
/*     */   }
/*     */ 
/*     */   public void setEntryPrice(BigDecimal entryPrice) {
/*  82 */     put("entryPrice", entryPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getEntryPrice() {
/*  86 */     return getBigDecimal("entryPrice");
/*     */   }
/*     */ 
/*     */   public void setEntryTimestamp(Date entryTimestamp) {
/*  90 */     putDate("entryTimestamp", entryTimestamp);
/*     */   }
/*     */ 
/*     */   public Date getEntryTimestamp() {
/*  94 */     return getDate("entryTimestamp");
/*     */   }
/*     */ 
/*     */   public void setSide(String side) {
/*  98 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public String getSide() {
/* 102 */     return getString("side");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 106 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 110 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPrice(BigDecimal takeProfitPrice) {
/* 114 */     put("takeProfitPrice", takeProfitPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getTakeProfitPrice() {
/* 118 */     return getBigDecimal("takeProfitPrice");
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(BigDecimal stopLossPrice) {
/* 122 */     put("stopLossPrice", stopLossPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopLossPrice() {
/* 126 */     return getBigDecimal("stopLossPrice");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.OrderCancelledResponseMessage
 * JD-Core Version:    0.6.0
 */