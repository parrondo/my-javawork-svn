/*     */ package com.dukascopy.transport.common.msg.group;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class TradeMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "trade";
/*     */   public static final String TRADE_ID = "tradeId";
/*     */   public static final String ORDER_ID = "orderId";
/*     */   public static final String ORDER_GROUP_ID = "orderGroupId";
/*     */   public static final String MARKET_PLACE = "marketPlace";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String INITIATOR = "initiator";
/*     */   public static final String COUNTER_PARTY = "counterParty";
/*     */   public static final String AMOUNT_PRIMARY = "amountPrimary";
/*     */   public static final String AMOUNT_SECONDARY = "amountSecondary";
/*     */   public static final String PRICE = "price";
/*     */   public static final String REF_PRICE = "refPrice";
/*     */   public static final String PARENT_ORDER_ID = "parentOrderId";
/*     */   public static final String SIDE = "side";
/*     */   public static final String ORIG_SYSTEM_ID = "origSystemId";
/*     */   private static final String VALUE_DATE = "valueDate";
/*     */   private static final String IS_FUND_TRADE = "isFundTrade";
/*     */   private static final String IS_WL_TRADE = "isWLTrade";
/*     */   public static final String CLIENT_ID = "clientId";
/*     */   private static final String USER_ID = "userId";
/*     */   public static final String BEST_BID = "bBid";
/*     */   public static final String BEST_ASK = "bAsk";
/*     */   public static final String FEED_COMM_PIP = "fcomm";
/*     */   public static final String FEED_COMM_DUKAS = "fcDuk";
/*     */   public static final String FEED_COMM_NONDUKAS = "fcNDuk";
/*     */ 
/*     */   public TradeMessage(Integer tradeId, String orderId, String marketPlace, String currencyPrimary, String currencySecondary, String initiator, String counterParty, Money amountPrimary, Money amountSecondary, Money price)
/*     */   {
/*  75 */     setType("trade");
/*  76 */     put("tradeId", tradeId);
/*  77 */     put("orderId", orderId);
/*  78 */     put("marketPlace", marketPlace);
/*  79 */     put("instrument", currencyPrimary + "/" + currencySecondary);
/*  80 */     put("initiator", initiator);
/*  81 */     put("counterParty", counterParty);
/*  82 */     put("amountPrimary", amountPrimary.getValue().divide(ONE_MILLION).toPlainString());
/*  83 */     if (amountSecondary != null) {
/*  84 */       put("amountSecondary", amountSecondary.getValue().divide(ONE_MILLION).toPlainString());
/*     */     }
/*  86 */     put("price", price.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public TradeMessage(Integer tradeId, String orderId, String marketPlace, String currencyPrimary, String currencySecondary, String initiator, String counterParty, Money amountPrimary, Money amountSecondary, Money price, String refPrice, String parentOrderId, String side, String origSysId)
/*     */   {
/* 103 */     setType("trade");
/* 104 */     put("tradeId", tradeId);
/* 105 */     put("orderId", orderId);
/* 106 */     put("marketPlace", marketPlace);
/* 107 */     put("instrument", currencyPrimary + "/" + currencySecondary);
/* 108 */     put("initiator", initiator);
/* 109 */     put("counterParty", counterParty);
/* 110 */     put("amountPrimary", amountPrimary.getValue().divide(ONE_MILLION).toPlainString());
/* 111 */     if (amountSecondary != null) {
/* 112 */       put("amountSecondary", amountSecondary.getValue().divide(ONE_MILLION).toPlainString());
/*     */     }
/* 114 */     put("price", price.getValue().toPlainString());
/* 115 */     put("refPrice", refPrice);
/* 116 */     put("parentOrderId", parentOrderId);
/* 117 */     put("side", side);
/* 118 */     put("origSystemId", origSysId);
/*     */   }
/*     */ 
/*     */   public TradeMessage(ProtocolMessage message) {
/* 122 */     super(message);
/* 123 */     setType("trade");
/* 124 */     put("tradeId", message.getInteger("tradeId"));
/* 125 */     put("orderId", message.getInteger("orderId"));
/* 126 */     put("orderGroupId", message.getString("orderGroupId"));
/* 127 */     put("marketPlace", message.getString("marketPlace"));
/* 128 */     put("instrument", message.getString("instrument"));
/* 129 */     put("initiator", message.getString("initiator"));
/* 130 */     put("counterParty", message.getString("counterParty"));
/* 131 */     put("amountPrimary", message.getString("amountPrimary"));
/* 132 */     put("amountSecondary", message.getString("amountSecondary"));
/* 133 */     put("price", message.getString("price"));
/* 134 */     put("refPrice", message.getString("refPrice"));
/* 135 */     put("parentOrderId", message.getString("parentOrderId"));
/* 136 */     put("side", message.getString("side"));
/* 137 */     put("origSystemId", message.getString("origSystemId"));
/* 138 */     put("isFundTrade", message.getString("isFundTrade"));
/* 139 */     put("valueDate", message.getString("valueDate"));
/* 140 */     put("clientId", message.getString("clientId"));
/* 141 */     put("bBid", message.getString("bBid"));
/* 142 */     put("bAsk", message.getString("bAsk"));
/* 143 */     put("isWLTrade", message.getString("isWLTrade"));
/* 144 */     put("fcomm", message.getString("fcomm"));
/* 145 */     put("fcDuk", message.getString("fcDuk"));
/* 146 */     put("fcNDuk", message.getString("fcNDuk"));
/*     */   }
/*     */ 
/*     */   public TradeMessage(String s) throws ParseException {
/* 150 */     super(s);
/* 151 */     setType("trade");
/*     */   }
/*     */ 
/*     */   public TradeMessage(JSONObject s) throws ParseException {
/* 155 */     super(s);
/* 156 */     setType("trade");
/*     */   }
/*     */ 
/*     */   public String getClientId()
/*     */   {
/* 165 */     return getString("clientId");
/*     */   }
/*     */ 
/*     */   public void setClientId(String clientId)
/*     */   {
/* 174 */     put("clientId", clientId);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 184 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public Integer getTradeId()
/*     */   {
/* 193 */     return getInteger("tradeId");
/*     */   }
/*     */ 
/*     */   public void setTradeId(Integer tradeId)
/*     */   {
/* 202 */     put("tradeId", tradeId);
/*     */   }
/*     */ 
/*     */   public String getOrigSystemId()
/*     */   {
/* 211 */     return getString("origSystemId");
/*     */   }
/*     */ 
/*     */   public void setOrigSystemId(String origSysId)
/*     */   {
/* 220 */     put("origSystemId", origSysId);
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/* 230 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/* 239 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getMarketPlace()
/*     */   {
/* 248 */     return getString("marketPlace");
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 257 */     return getString("instrument").substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/* 266 */     return getString("instrument").substring(4);
/*     */   }
/*     */ 
/*     */   public String getInitiator()
/*     */   {
/* 275 */     return getString("initiator");
/*     */   }
/*     */ 
/*     */   public String getCounterParty()
/*     */   {
/* 284 */     return getString("counterParty");
/*     */   }
/*     */ 
/*     */   public Money getAmountPrimary()
/*     */   {
/* 293 */     return new Money(getString("amountPrimary"), getCurrencyPrimary()).multiply(ONE_MILLION);
/*     */   }
/*     */ 
/*     */   public Money getAmountSecondary()
/*     */   {
/* 302 */     return new Money(getString("amountSecondary"), getCurrencySecondary()).multiply(ONE_MILLION);
/*     */   }
/*     */ 
/*     */   public Money getPrice()
/*     */   {
/* 312 */     return new Money(getString("price"), getCurrencySecondary());
/*     */   }
/*     */ 
/*     */   public Money getReferencePrice()
/*     */   {
/* 321 */     String refPriceString = getString("refPrice");
/* 322 */     if (refPriceString != null) {
/* 323 */       return new Money(refPriceString, getCurrencySecondary());
/*     */     }
/* 325 */     return null;
/*     */   }
/*     */ 
/*     */   public void setReferencePrice(Money price)
/*     */   {
/* 336 */     put("refPrice", price.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public String getOrderGroupId()
/*     */   {
/* 345 */     return getString("orderGroupId");
/*     */   }
/*     */ 
/*     */   public void setOrderGroupId(String id)
/*     */   {
/* 354 */     put("orderGroupId", id);
/*     */   }
/*     */ 
/*     */   public String getParentOrderId()
/*     */   {
/* 363 */     return getString("parentOrderId");
/*     */   }
/*     */ 
/*     */   public void setParentOrderId(String parentOrderId)
/*     */   {
/* 372 */     put("parentOrderId", parentOrderId);
/*     */   }
/*     */ 
/*     */   public OrderSide getSide()
/*     */   {
/* 381 */     String sideString = getString("side");
/* 382 */     if (sideString != null) {
/* 383 */       return OrderSide.fromString(sideString);
/*     */     }
/* 385 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSide(OrderSide side)
/*     */   {
/* 395 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public void strip()
/*     */   {
/* 402 */     remove("refPrice");
/* 403 */     remove("initiator");
/* 404 */     remove("counterParty");
/* 405 */     remove("marketPlace");
/* 406 */     remove("isFundTrade");
/*     */   }
/*     */ 
/*     */   public boolean isFundTrade()
/*     */   {
/* 415 */     return getBoolean("isFundTrade");
/*     */   }
/*     */ 
/*     */   public void setFundTrade(Boolean isFundTrade)
/*     */   {
/* 424 */     put("isFundTrade", isFundTrade);
/*     */   }
/*     */ 
/*     */   public boolean isWlTrade() {
/* 428 */     return getBoolean("isWLTrade");
/*     */   }
/*     */ 
/*     */   public void setWlTrade(Boolean isWLTrade) {
/* 432 */     put("isWLTrade", isWLTrade);
/*     */   }
/*     */ 
/*     */   public void setValueDate(Date valueDate) {
/* 436 */     putDate("valueDate", valueDate);
/*     */   }
/*     */ 
/*     */   public Date getValueDate() {
/* 440 */     return getDate("valueDate");
/*     */   }
/*     */ 
/*     */   public void setUserId(String userId) {
/* 444 */     if (userId != null)
/* 445 */       put("userId", userId);
/*     */   }
/*     */ 
/*     */   public String getUserId()
/*     */   {
/* 450 */     return getString("userId");
/*     */   }
/*     */ 
/*     */   public BigDecimal getBestBid() {
/* 454 */     String bid = getString("bBid");
/* 455 */     if (bid == null) {
/* 456 */       return null;
/*     */     }
/* 458 */     return new BigDecimal(bid);
/*     */   }
/*     */ 
/*     */   public void setBestBid(BigDecimal bid)
/*     */   {
/* 463 */     if (bid != null)
/* 464 */       put("bBid", bid.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getBestAsk()
/*     */   {
/* 469 */     String ask = getString("bAsk");
/* 470 */     if (ask == null) {
/* 471 */       return null;
/*     */     }
/* 473 */     return new BigDecimal(ask);
/*     */   }
/*     */ 
/*     */   public void setBestAsk(BigDecimal ask)
/*     */   {
/* 478 */     if (ask != null)
/* 479 */       put("bAsk", ask.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getFeedCommssion()
/*     */   {
/* 489 */     String fcomm = getString("fcomm");
/* 490 */     if (fcomm != null) {
/* 491 */       return new BigDecimal(fcomm);
/*     */     }
/* 493 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFeedCommission(BigDecimal fcomm)
/*     */   {
/* 503 */     if (fcomm == null)
/* 504 */       put("fcomm", null);
/*     */     else
/* 506 */       put("fcomm", fcomm.toPlainString());
/*     */   }
/*     */ 
/*     */   public void setFeedCommissionDukas(BigDecimal fcomm)
/*     */   {
/* 516 */     if (fcomm == null)
/* 517 */       put("fcDuk", null);
/*     */     else
/* 519 */       put("fcDuk", fcomm.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getFeedCommssionDukas()
/*     */   {
/* 529 */     String fcomm = getString("fcDuk");
/* 530 */     if (fcomm != null) {
/* 531 */       return new BigDecimal(fcomm);
/*     */     }
/* 533 */     return null;
/*     */   }
/*     */ 
/*     */   public BigDecimal getFeedCommssionNondukas()
/*     */   {
/* 544 */     String fcomm = getString("fcNDuk");
/* 545 */     if (fcomm != null) {
/* 546 */       return new BigDecimal(fcomm);
/*     */     }
/* 548 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFeedCommissionNondukas(BigDecimal fcomm)
/*     */   {
/* 558 */     if (fcomm == null)
/* 559 */       put("fcNDuk", null);
/*     */     else
/* 561 */       put("fcNDuk", fcomm.toPlainString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.group.TradeMessage
 * JD-Core Version:    0.6.0
 */