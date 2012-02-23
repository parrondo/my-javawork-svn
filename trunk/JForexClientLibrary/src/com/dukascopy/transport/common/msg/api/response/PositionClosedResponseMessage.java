/*     */ package com.dukascopy.transport.common.msg.api.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class PositionClosedResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "position_closed";
/*     */ 
/*     */   public PositionClosedResponseMessage()
/*     */   {
/*  25 */     setType("position_closed");
/*     */   }
/*     */ 
/*     */   public PositionClosedResponseMessage(ProtocolMessage message)
/*     */   {
/*  34 */     super(message);
/*     */ 
/*  36 */     setType("position_closed");
/*     */ 
/*  38 */     setPositionId(message.getString("positionId"));
/*  39 */     setOrderId(message.getString("orderId"));
/*  40 */     setLots(message.getBigDecimal("lots"));
/*  41 */     setEntryPrice(message.getBigDecimal("entryPrice"));
/*  42 */     setEntryTimestamp(message.getDate("entryTimestamp"));
/*  43 */     setSide(message.getString("side"));
/*  44 */     setInstrument(message.getString("instrument"));
/*  45 */     setClosePrice(message.getBigDecimal("closePrice"));
/*  46 */     setCloseTimestamp(message.getDate("closeTimestamp"));
/*  47 */     setProfit(message.getBigDecimal("profit"));
/*     */   }
/*     */ 
/*     */   public PositionClosedResponseMessage(Position position)
/*     */   {
/*  56 */     setType("position_closed");
/*     */ 
/*  58 */     setPositionId(position.getPositionID());
/*  59 */     setOrderId(position.getPositionID());
/*  60 */     setLots(position.getAmount().getValue().divide(ONE_MILLION));
/*  61 */     setEntryPrice(position.getPriceOpen().getValue());
/*  62 */     setEntryTimestamp(new Date());
/*  63 */     setSide(PositionSide.LONG.equals(position.getPositionSide()) ? "BUY" : "SELL");
/*  64 */     setInstrument(position.getInstrument());
/*  65 */     if (position.getPriceCurrent() != null) {
/*  66 */       setClosePrice(position.getPriceCurrent().getValue());
/*     */     }
/*  68 */     setCloseTimestamp(new Date());
/*  69 */     if (position.getProfitLoss() != null)
/*  70 */       setProfit(position.getProfitLoss().getValue());
/*     */   }
/*     */ 
/*     */   public void setPositionId(String positionId)
/*     */   {
/*  75 */     put("positionId", positionId);
/*     */   }
/*     */ 
/*     */   public String getPositionId() {
/*  79 */     return getString("positionId");
/*     */   }
/*     */ 
/*     */   public void setLots(BigDecimal lots) {
/*  83 */     put("lots", lots);
/*     */   }
/*     */ 
/*     */   public BigDecimal getLots() {
/*  87 */     return getBigDecimal("lots");
/*     */   }
/*     */ 
/*     */   public void setEntryPrice(BigDecimal entryPrice) {
/*  91 */     put("entryPrice", entryPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getEntryPrice() {
/*  95 */     return getBigDecimal("entryPrice");
/*     */   }
/*     */ 
/*     */   public void setEntryTimestamp(Date date) {
/*  99 */     putDate("entryTimestamp", date);
/*     */   }
/*     */ 
/*     */   public Date getEntryTimestamp() {
/* 103 */     return getDate("entryTimestamp");
/*     */   }
/*     */ 
/*     */   public void setSide(String side) {
/* 107 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public String getSide() {
/* 111 */     return getString("side");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/* 115 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/* 119 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/* 123 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getOrderId() {
/* 127 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setClosePrice(BigDecimal closePrice) {
/* 131 */     put("closePrice", closePrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getClosePrice() {
/* 135 */     return getBigDecimal("closePrice");
/*     */   }
/*     */ 
/*     */   public void setCloseTimestamp(Date date) {
/* 139 */     putDate("closeTimestamp", date);
/*     */   }
/*     */ 
/*     */   public Date getCloseTimestamp() {
/* 143 */     return getDate("closeTimestamp");
/*     */   }
/*     */ 
/*     */   public void setProfit(BigDecimal profit) {
/* 147 */     put("profit", profit);
/*     */   }
/*     */ 
/*     */   public BigDecimal getProfit() {
/* 151 */     return getBigDecimal("profit");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.PositionClosedResponseMessage
 * JD-Core Version:    0.6.0
 */