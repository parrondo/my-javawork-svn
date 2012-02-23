/*     */ package com.dukascopy.transport.common.msg.api.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class PositionUpdatedResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "position_updated";
/*     */ 
/*     */   public PositionUpdatedResponseMessage()
/*     */   {
/*  25 */     setType("position_updated");
/*     */   }
/*     */ 
/*     */   public PositionUpdatedResponseMessage(ProtocolMessage message)
/*     */   {
/*  34 */     super(message);
/*     */ 
/*  36 */     setType("position_updated");
/*     */ 
/*  38 */     setPositionId(message.getString("positionId"));
/*  39 */     setLots(message.getBigDecimal("lots"));
/*  40 */     setEntryPrice(message.getBigDecimal("entryPrice"));
/*  41 */     setEntryTimestamp(message.getDate("entryTimestamp"));
/*  42 */     setSide(message.getString("side"));
/*  43 */     setInstrument(message.getString("instrument"));
/*  44 */     setCommissions(message.getBigDecimal("commissions"));
/*  45 */     setTakeProfitPrice(message.getBigDecimal("takeProfitPrice"));
/*  46 */     setStopLossPrice(message.getBigDecimal("stopLossPrice"));
/*     */   }
/*     */ 
/*     */   public PositionUpdatedResponseMessage(Position position)
/*     */   {
/*  55 */     setType("position_updated");
/*  56 */     setPositionId(position.getPositionID());
/*  57 */     setLots(position.getAmount().getValue().divide(new BigDecimal(1000000), 6));
/*  58 */     setEntryPrice(position.getPriceOpen().getValue());
/*  59 */     setEntryTimestamp(position.getTimestamp());
/*  60 */     setSide(PositionSide.LONG == position.getPositionSide() ? "LONG" : "SHORT");
/*  61 */     setInstrument(position.getInstrument());
/*  62 */     if (position.getCommission() != null)
/*  63 */       setCommissions(position.getCommission().getValue());
/*     */   }
/*     */ 
/*     */   public void setPositionId(String positionId)
/*     */   {
/*  68 */     put("positionId", positionId);
/*     */   }
/*     */ 
/*     */   public String getPositionId() {
/*  72 */     return getString("positionId");
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
/*     */   public void setEntryTimestamp(Date date) {
/*  92 */     putDate("entryTimestamp", date);
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
/*     */   public void setCommissions(BigDecimal commissions) {
/* 116 */     put("commissions", commissions);
/*     */   }
/*     */ 
/*     */   public BigDecimal getCommissions() {
/* 120 */     return getBigDecimal("commissions");
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPrice(BigDecimal takeProfitPrice) {
/* 124 */     put("takeProfitPrice", takeProfitPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getTakeProfitPrice() {
/* 128 */     return getBigDecimal("takeProfitPrice");
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(BigDecimal stopLossPrice) {
/* 132 */     put("stopLossPrice", stopLossPrice);
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopLossPrice() {
/* 136 */     return getBigDecimal("stopLossPrice");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.response.PositionUpdatedResponseMessage
 * JD-Core Version:    0.6.0
 */