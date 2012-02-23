/*     */ package com.dukascopy.transport.common.msg.api.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class PlaceOrderRequestMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "place_order";
/*     */   private static final String ORDER_TYPE = "orderType";
/*     */   private static final String INSTRUMENT = "instrument";
/*     */   private static final String SIDE = "side";
/*     */   private static final String LOTS = "lots";
/*     */   private static final String PRICE = "price";
/*     */   private static final String TAKE_PROFIT_PRICE = "takeProfitPrice";
/*     */   private static final String STOP_LOSS_PRICE = "stopLossPrice";
/*     */   private static final String SLIPPAGE = "slippage";
/*     */   private static final String STOP_PRICE_DIRECTION = "stopPriceDirection";
/*     */   public static final String PRICE_BETTER = "priceBetter";
/*     */   public static final String PRICE_WORSE = "priceWorse";
/*     */ 
/*     */   public PlaceOrderRequestMessage()
/*     */   {
/*  33 */     setType("place_order");
/*     */   }
/*     */ 
/*     */   public PlaceOrderRequestMessage(ProtocolMessage message)
/*     */   {
/*  42 */     super(message);
/*     */ 
/*  44 */     setType("place_order");
/*     */ 
/*  46 */     setOrderType(message.getString("orderType"));
/*  47 */     setInstrument(message.getString("instrument"));
/*  48 */     setSide(message.getString("side"));
/*  49 */     setLots(message.getBigDecimal("lots"));
/*  50 */     setPrice(message.getBigDecimal("price"));
/*  51 */     setTakeProfitPrice(message.getBigDecimal("takeProfitPrice"));
/*  52 */     setStopLossPrice(message.getBigDecimal("stopLossPrice"));
/*  53 */     setSlippage(message.getBigDecimal("slippage"));
/*  54 */     setStopPriceDirection(message.getString("stopPriceDirection"));
/*     */   }
/*     */ 
/*     */   public void setStopPriceDirection(String stopPriceDirection) {
/*  58 */     put("stopPriceDirection", stopPriceDirection);
/*     */   }
/*     */   public String getStopPriceDirection() {
/*     */     String stopPriceDirection;
/*     */     try {
/*  64 */       stopPriceDirection = getString("stopPriceDirection");
/*     */     } catch (NoSuchElementException nsee) {
/*  66 */       stopPriceDirection = "priceBetter";
/*     */     }
/*  68 */     return stopPriceDirection;
/*     */   }
/*     */ 
/*     */   public void setOrderType(String orderType) {
/*  72 */     put("orderType", orderType);
/*     */   }
/*     */ 
/*     */   public String getOrderType() {
/*  76 */     return getString("orderType");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument) {
/*  80 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  84 */     String result = null;
/*     */     try {
/*  86 */       result = getString("instrument");
/*     */     }
/*     */     catch (NoSuchElementException nsee) {
/*     */     }
/*  90 */     return result;
/*     */   }
/*     */ 
/*     */   public void setSide(String side) {
/*  94 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public String getSide() {
/*  98 */     return getString("side");
/*     */   }
/*     */ 
/*     */   public void setLots(BigDecimal lots) {
/* 102 */     put("lots", lots);
/*     */   }
/*     */ 
/*     */   public BigDecimal getLots() {
/* 106 */     return getBigDecimal("lots");
/*     */   }
/*     */ 
/*     */   public void setPrice(BigDecimal price) {
/* 110 */     put("price", price);
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice() {
/* 114 */     return getBigDecimal("price");
/*     */   }
/*     */ 
/*     */   public void setTakeProfitPrice(BigDecimal price) {
/* 118 */     put("takeProfitPrice", price);
/*     */   }
/*     */ 
/*     */   public BigDecimal getTakeProfitPrice() {
/* 122 */     return getBigDecimal("takeProfitPrice");
/*     */   }
/*     */ 
/*     */   public void setStopLossPrice(BigDecimal price) {
/* 126 */     put("stopLossPrice", price);
/*     */   }
/*     */ 
/*     */   public BigDecimal getStopLossPrice() {
/* 130 */     return getBigDecimal("stopLossPrice");
/*     */   }
/*     */ 
/*     */   public void setSlippage(BigDecimal price) {
/* 134 */     if (price == null) {
/* 135 */       price = BigDecimal.ZERO;
/*     */     }
/* 137 */     put("slippage", price);
/*     */   }
/*     */ 
/*     */   public BigDecimal getSlippage() {
/* 141 */     BigDecimal slippage = getBigDecimal("slippage");
/* 142 */     if (slippage == null) {
/* 143 */       return BigDecimal.ZERO;
/*     */     }
/* 145 */     return slippage;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.api.request.PlaceOrderRequestMessage
 * JD-Core Version:    0.6.0
 */