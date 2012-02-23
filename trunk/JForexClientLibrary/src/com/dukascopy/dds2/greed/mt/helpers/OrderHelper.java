/*     */ package com.dukascopy.dds2.greed.mt.helpers;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Currency;
/*     */ 
/*     */ public class OrderHelper
/*     */ {
/*     */   private static OrderMessage createOpeningOrder(String slippageParam, BigDecimal amountParam, OrderSide sideParam, String instrumentParam, Money bestPrice, String externalSystemId, String strategyId, String comment)
/*     */   {
/*  33 */     OrderMessage openingOrder = new OrderMessage();
/*  34 */     String[] currencies = instrumentParam.split("/");
/*  35 */     openingOrder.setInstrument(instrumentParam);
/*  36 */     openingOrder.setOrderDirection(OrderDirection.OPEN);
/*  37 */     openingOrder.setSide(sideParam);
/*  38 */     openingOrder.setPriceClient(bestPrice);
/*  39 */     Money amount = new Money(amountParam, Currency.getInstance(currencies[0]));
/*     */ 
/*  41 */     openingOrder.setAmount(amount);
/*     */ 
/*  43 */     if (slippageParam != null) {
/*  44 */       openingOrder.setPriceTrailingLimit(slippageParam);
/*     */     }
/*     */ 
/*  47 */     setExternalSystemId(externalSystemId, openingOrder);
/*  48 */     setStrategyId(strategyId, openingOrder);
/*  49 */     setCommentTag(comment, openingOrder);
/*     */ 
/*  51 */     return openingOrder;
/*     */   }
/*     */ 
/*     */   private static void setExternalSystemId(String externalSysId, OrderMessage orderToTag)
/*     */   {
/*  61 */     if (externalSysId != null)
/*  62 */       orderToTag.setExternalSysId(externalSysId);
/*     */   }
/*     */ 
/*     */   private static void setStrategyId(String strategyId, OrderMessage orderToTag)
/*     */   {
/*  74 */     if (strategyId != null)
/*  75 */       orderToTag.setStrategySysId(strategyId);
/*     */   }
/*     */ 
/*     */   private static void setCommentTag(String tag, OrderMessage orderToTag)
/*     */   {
/*  87 */     if (tag != null)
/*  88 */       orderToTag.setTag(tag);
/*     */   }
/*     */ 
/*     */   public static String getPairsSeparator()
/*     */   {
/*  99 */     return "/";
/*     */   }
/*     */ 
/*     */   public static Instrument InstrumentFromMTString(String instrumentAsString)
/*     */   {
/* 109 */     return Instrument.fromString(instrumentAsString.substring(0, 3) + getPairsSeparator() + instrumentAsString.substring(3));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.helpers.OrderHelper
 * JD-Core Version:    0.6.0
 */