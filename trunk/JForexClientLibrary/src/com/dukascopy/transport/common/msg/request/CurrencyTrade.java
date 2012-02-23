/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.RequestMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ 
/*     */ public class CurrencyTrade extends RequestMessage
/*     */ {
/*     */   public CurrencyTrade(Integer tradeId, Integer orderId, Integer positionId, String marketPlace, String currencyPrimary, String currencySecondary, String initiator, String counterParty, Money amountPrimary, Money amountSecondary, Money price)
/*     */   {
/*  44 */     put("tradeId", tradeId);
/*  45 */     put("orderId", orderId);
/*  46 */     put("positionId", positionId);
/*  47 */     put("marketPlace", marketPlace);
/*  48 */     put("instrument", currencyPrimary + "/" + currencySecondary);
/*  49 */     put("initiator", initiator);
/*  50 */     put("counterParty", counterParty);
/*  51 */     put("amountPrimary", amountPrimary.divide(ONE_MILLION).getValue().toPlainString());
/*  52 */     put("amountSecondary", amountSecondary.divide(ONE_MILLION).getValue().toPlainString());
/*  53 */     put("price", price.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public CurrencyTrade(ProtocolMessage message) {
/*  57 */     put("tradeId", message.getInteger("tradeId"));
/*  58 */     put("orderId", message.getInteger("orderId"));
/*  59 */     put("positionId", message.getInteger("positionId"));
/*  60 */     put("marketPlace", message.getString("marketPlace"));
/*  61 */     put("instrument", message.getString("instrument"));
/*  62 */     put("initiator", message.getString("initiator"));
/*  63 */     put("counterParty", message.getString("counterParty"));
/*  64 */     put("amountPrimary", message.getString("amountPrimary"));
/*  65 */     put("amountSecondary", message.getString("amountSecondary"));
/*  66 */     put("price", message.getString("price"));
/*     */   }
/*     */ 
/*     */   public CurrencyTrade(String s) throws ParseException {
/*  70 */     super(s);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/*  79 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public Integer getTradeId()
/*     */   {
/*  87 */     return getInteger("tradeId");
/*     */   }
/*     */ 
/*     */   public Integer getOrderId()
/*     */   {
/*  95 */     return getInteger("orderId");
/*     */   }
/*     */ 
/*     */   public void setOrderId(Integer orderId)
/*     */   {
/* 103 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public Integer getPositionId()
/*     */   {
/* 111 */     return getInteger("positionId");
/*     */   }
/*     */ 
/*     */   public void setPositionId(Integer positionId)
/*     */   {
/* 119 */     put("positionId", positionId);
/*     */   }
/*     */ 
/*     */   public String getMarketPlace()
/*     */   {
/* 127 */     return getString("marketPlace");
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 135 */     return getString("instrument").substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/* 143 */     return getString("instrument").substring(4);
/*     */   }
/*     */ 
/*     */   public String getInitiator()
/*     */   {
/* 151 */     return getString("initiator");
/*     */   }
/*     */ 
/*     */   public String getCounterParty()
/*     */   {
/* 159 */     return getString("counterParty");
/*     */   }
/*     */ 
/*     */   public Money getAmountPrimary()
/*     */   {
/* 167 */     if (getString("amountPrimary") != null) {
/* 168 */       return new Money(getString("amountPrimary"), getCurrencyPrimary()).multiply(ONE_MILLION);
/*     */     }
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   public Money getAmountSecondary()
/*     */   {
/* 179 */     if (getString("amountSecondary") != null) {
/* 180 */       return new Money(getString("amountSecondary"), getCurrencySecondary()).multiply(ONE_MILLION);
/*     */     }
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */   public Money getPrice()
/*     */   {
/* 192 */     if (getString("price") != null) {
/* 193 */       return new Money(getString("price"), getCurrencySecondary());
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.CurrencyTrade
 * JD-Core Version:    0.6.0
 */