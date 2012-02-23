/*     */ package com.dukascopy.transport.common.msg.signals;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.SignalSide;
/*     */ import com.dukascopy.transport.common.model.type.SignalType;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ 
/*     */ public class SignalMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "smspc";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String SESSION_ID = "sigId";
/*     */   public static final String SIGNAL_TYPE = "stype";
/*     */   public static final String SIDE = "side";
/*     */   public static final String AMOUNT = "amount";
/*     */   public static final String TRAILING_LIMIT = "trailingLimit";
/*     */   public static final String RESET = "reset";
/*     */   public static final String PRICE_STOP = "priceStop";
/*     */   public static final String STOP_DIRECTION = "stopDir";
/*     */ 
/*     */   public SignalMessage(ProtocolMessage message)
/*     */   {
/*  37 */     super(message);
/*  38 */     setType("smspc");
/*  39 */     setPriority(1000000L);
/*  40 */     put("instrument", message.getString("instrument"));
/*  41 */     put("sigId", message.getString("sigId"));
/*  42 */     put("amount", message.getString("amount"));
/*  43 */     put("side", message.getString("side"));
/*  44 */     put("reset", message.getString("reset"));
/*  45 */     put("stype", message.getString("stype"));
/*  46 */     put("trailingLimit", message.getString("trailingLimit"));
/*  47 */     put("priceStop", message.getString("priceStop"));
/*  48 */     put("stopDir", message.getString("stopDir"));
/*     */   }
/*     */ 
/*     */   public SignalMessage()
/*     */   {
/*  53 */     setPriority(1000000L);
/*  54 */     setType("smspc");
/*     */   }
/*     */ 
/*     */   public SignalMessage(String instrument, String originator, OrderSide side, BigDecimal amount, String sessionId, String priceTrailingLimit)
/*     */   {
/*  72 */     setPriority(1000000L);
/*  73 */     setType("smspc");
/*  74 */     put("stype", originator);
/*  75 */     put("instrument", instrument);
/*  76 */     put("side", side.asString());
/*  77 */     put("amount", amount.divide(ONE_MILLION, 2, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString());
/*  78 */     put("sigId", sessionId);
/*  79 */     if (null != priceTrailingLimit) put("trailingLimit", priceTrailingLimit);
/*     */   }
/*     */ 
/*     */   public SignalMessage(String instrument, String originator, OrderSide side, BigDecimal amount, String sessionId)
/*     */   {
/*  88 */     setPriority(1000000L);
/*  89 */     setType("smspc");
/*  90 */     put("stype", originator);
/*  91 */     put("instrument", instrument);
/*  92 */     put("side", side.asString());
/*  93 */     put("amount", amount.divide(ONE_MILLION, 2, RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString());
/*  94 */     put("sigId", sessionId);
/*     */   }
/*     */ 
/*     */   public SignalMessage(String reset, String sessionId)
/*     */   {
/* 103 */     setPriority(1000000L);
/* 104 */     setType("smspc");
/* 105 */     put("reset", reset);
/* 106 */     put("sigId", sessionId);
/*     */   }
/*     */ 
/*     */   public boolean isReset() {
/* 110 */     boolean result = false;
/* 111 */     if ((get("reset") != null) && 
/* 112 */       (getString("reset").equalsIgnoreCase("all"))) {
/* 113 */       result = true;
/*     */     }
/*     */ 
/* 116 */     return result;
/*     */   }
/*     */ 
/*     */   public Money getAmount() {
/* 120 */     String amountString = getString("amount");
/* 121 */     if (amountString != null) {
/* 122 */       return new Money(amountString, getCurrencyPrimary()).multiply(ONE_MILLION);
/*     */     }
/* 124 */     return null;
/*     */   }
/*     */ 
/*     */   public void setAmount(Money amount)
/*     */   {
/* 130 */     put("amount", amount.getValue().divide(ONE_MILLION).toPlainString());
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/* 135 */     return getString("instrument").substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary() {
/* 139 */     return getString("instrument").substring(4);
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/* 148 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument)
/*     */   {
/* 157 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getSessionId()
/*     */   {
/* 167 */     return getString("sigId");
/*     */   }
/*     */ 
/*     */   public void setSessionId(String signal_id)
/*     */   {
/* 176 */     put("sigId", signal_id);
/*     */   }
/*     */ 
/*     */   public SignalSide getSide()
/*     */   {
/* 185 */     String sideString = getString("side");
/* 186 */     if (sideString != null) {
/* 187 */       return SignalSide.fromString(sideString);
/*     */     }
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSide(SignalSide side)
/*     */   {
/* 199 */     put("side", side);
/*     */   }
/*     */ 
/*     */   public SignalType getSignalType()
/*     */   {
/* 209 */     String sideString = getString("stype");
/* 210 */     if (sideString != null) {
/* 211 */       return SignalType.fromString(sideString);
/*     */     }
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSignalType(SignalType type)
/*     */   {
/* 223 */     put("stype", type.asString());
/*     */   }
/*     */ 
/*     */   public void setPriceTrailingLimit(Money priceLimit)
/*     */   {
/* 232 */     put("trailingLimit", priceLimit.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public void setPriceTrailingLimit(String priceLimit)
/*     */     throws NumberFormatException
/*     */   {
/* 242 */     new BigDecimal(priceLimit);
/* 243 */     put("trailingLimit", priceLimit);
/*     */   }
/*     */ 
/*     */   public Money getPriceTrailingLimit()
/*     */   {
/* 252 */     String priceString = getString("trailingLimit");
/* 253 */     if (priceString != null) {
/* 254 */       return new Money(priceString, getCurrencySecondary());
/*     */     }
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */   public void setPriceStop(Money priceStop)
/*     */   {
/* 267 */     if (priceStop != null)
/* 268 */       put("priceStop", priceStop.getValue().toPlainString());
/*     */   }
/*     */ 
/*     */   public Money getPriceStop()
/*     */   {
/* 278 */     String priceString = getString("priceStop");
/* 279 */     if (priceString != null) {
/* 280 */       return new Money(priceString, getCurrencySecondary());
/*     */     }
/* 282 */     return null;
/*     */   }
/*     */ 
/*     */   public void setStopDirection(StopDirection stopDirection)
/*     */   {
/* 293 */     if (stopDirection != null)
/* 294 */       put("stopDir", stopDirection.asString());
/*     */   }
/*     */ 
/*     */   public StopDirection getStopDirection()
/*     */   {
/* 305 */     String stopDirection = getString("stopDir");
/* 306 */     if (stopDirection != null) {
/* 307 */       return StopDirection.fromString(stopDirection);
/*     */     }
/* 309 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.signals.SignalMessage
 * JD-Core Version:    0.6.0
 */