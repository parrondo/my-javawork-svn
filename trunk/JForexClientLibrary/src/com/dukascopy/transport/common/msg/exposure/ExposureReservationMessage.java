/*     */ package com.dukascopy.transport.common.msg.exposure;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ExposureReservationMessage extends ProtocolMessage
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 220706151507L;
/*     */   public static final String TYPE = "expRes";
/*     */   public static final String ID = "id";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String SIDE = "side";
/*     */   public static final String AMOUNT = "amount";
/*     */   public static final String PRICE = "price";
/*     */   public static final String MARKETPLACE_NAME = "marketplace";
/*     */   public static final String STATE = "state";
/*     */ 
/*     */   public ExposureReservationMessage()
/*     */   {
/*  37 */     setType("expRes");
/*     */   }
/*     */ 
/*     */   public ExposureReservationMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  47 */     super(s);
/*  48 */     setType("expRes");
/*     */   }
/*     */ 
/*     */   public ExposureReservationMessage(JSONObject s)
/*     */     throws ParseException
/*     */   {
/*  58 */     super(s);
/*  59 */     setType("expRes");
/*     */   }
/*     */ 
/*     */   public ExposureReservationMessage(ProtocolMessage message)
/*     */   {
/*  68 */     super(message);
/*  69 */     setType("expRes");
/*  70 */     put("id", message.getString("id"));
/*  71 */     put("instrument", message.getString("instrument"));
/*  72 */     put("side", message.getString("side"));
/*  73 */     put("amount", message.getBigDecimal("amount"));
/*  74 */     put("price", message.getBigDecimal("price"));
/*  75 */     put("marketplace", message.getString("marketplace"));
/*  76 */     put("state", message.getString("state"));
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  80 */     return getString("id");
/*     */   }
/*     */ 
/*     */   public void setId(String id) {
/*  84 */     put("id", id);
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  88 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String accountId) {
/*  92 */     put("instrument", accountId);
/*     */   }
/*     */ 
/*     */   public OrderSide getSide() {
/*  96 */     String sideString = getString("side");
/*  97 */     if (sideString != null) {
/*  98 */       return OrderSide.fromString(sideString);
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSide(OrderSide side)
/*     */   {
/* 105 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public BigDecimal getAmount() {
/* 109 */     String amountString = getString("amount");
/* 110 */     if (amountString != null) {
/* 111 */       return new BigDecimal(amountString);
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount(BigDecimal amount)
/*     */   {
/* 118 */     put("amount", amount.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice() {
/* 122 */     String priceString = getString("price");
/* 123 */     if (priceString != null) {
/* 124 */       return new BigDecimal(priceString);
/*     */     }
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPrice(BigDecimal price)
/*     */   {
/* 131 */     put("price", price.toPlainString());
/*     */   }
/*     */ 
/*     */   public String getMarketPlaceName() {
/* 135 */     return getString("marketplace");
/*     */   }
/*     */ 
/*     */   public void setMarketPlaceName(String marketplaceName) {
/* 139 */     put("marketplace", marketplaceName);
/*     */   }
/*     */ 
/*     */   public ReservationState getReservationState() {
/* 143 */     String reservationState = getString("state");
/* 144 */     if (reservationState != null) {
/* 145 */       return ReservationState.fromString(reservationState);
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   public void setReservationState(ReservationState reservationState)
/*     */   {
/* 152 */     if (reservationState != null)
/* 153 */       put("state", reservationState.asString());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.exposure.ExposureReservationMessage
 * JD-Core Version:    0.6.0
 */