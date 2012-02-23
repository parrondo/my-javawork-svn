/*     */ package com.dukascopy.transport.common.msg.api.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.backoffice.type.Position;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class OrderExecutedResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "order_executed";
/*     */ 
/*     */   public OrderExecutedResponseMessage()
/*     */   {
/*  24 */     setType("order_executed");
/*     */   }
/*     */ 
/*     */   public OrderExecutedResponseMessage(ProtocolMessage message)
/*     */   {
/*  33 */     super(message);
/*     */ 
/*  35 */     setType("order_executed");
/*     */ 
/*  37 */     setOrderId(message.getInteger("orderId"));
/*  38 */     setLots(message.getBigDecimal("lots"));
/*  39 */     setExecutionPrice(message.getBigDecimal("executionPrice"));
/*  40 */     setExecutionTimestamp(message.getDate("executionTimestamp"));
/*  41 */     setSide(message.getString("side"));
/*  42 */     setInstrument(message.getString("instrument"));
/*  43 */     setPositionId(message.getInteger("positionId"));
/*     */   }
/*     */ 
/*     */   public OrderExecutedResponseMessage(Position position)
/*     */   {
/*  52 */     setType("order_executed");
/*     */ 
/*  54 */     setOrderId(Integer.valueOf(position.getPositionId()));
/*  55 */     setLots(position.getLots());
/*  56 */     setExecutionPrice(position.getOpenPrice());
/*  57 */     setExecutionTimestamp(new Date());
/*  58 */     setSide(position.isUp() ? "BUY" : "SELL");
/*  59 */     setInstrument(position.getInstrument());
/*     */   }
/*     */ 
/*     */   public void setOrderId(Integer orderId) {
/*  63 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public Integer getOrderId() {
/*  67 */     return getInteger("orderId");
/*     */   }
/*     */ 
/*     */   public void setLots(BigDecimal lots) {
/*  71 */     put("lots", lots);
/*     */   }
/*     */ 
/*     */   public BigDecimal getLots() {
/*  75 */     return getBigDecimal("lots");
/*     */   }
/*     */ 
/*     */   public void setExecutionPrice(BigDecimal executionPrice) {
/*  79 */     put("executionPrice", executionPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getExecutionPrice() {
/*  83 */     return getBigDecimal("executionPrice");
/*     */   }
/*     */ 
/*     */   public void setExecutionTimestamp(Date executionTimestamp) {
/*  87 */     putDate("executionTimestamp", executionTimestamp);
/*     */   }
/*     */ 
/*     */   public Date getExecutionTimestamp() {
/*  91 */     return getDate("executionTimestamp");
/*     */   }
/*     */ 
/*     */   public void setSide(String side) {
/*  95 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public String getSide() {
/*  99 */     return getString("side");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 103 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 107 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setPositionId(Integer positionId) {
/* 111 */     put("positionId", positionId);
/*     */   }
/*     */ 
/*     */   public Integer getPositionId() {
/* 115 */     return getInteger("positionId");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.OrderExecutedResponseMessage
 * JD-Core Version:    0.6.0
 */