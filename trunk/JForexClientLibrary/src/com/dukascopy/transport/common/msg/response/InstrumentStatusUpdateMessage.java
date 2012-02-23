/*     */ package com.dukascopy.transport.common.msg.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class InstrumentStatusUpdateMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "instrumentStatus";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String TRADABLE = "tradable";
/*     */   public static final String CRITIC_SPREAD = "critic_spread";
/*     */   public static final String SMART_SPREAD_LIMIT = "smart_limit";
/*     */   public static final String LOGGING = "logging";
/*     */   public static final String SPREAD_GUARD_MIN_OFFER = "spreadGuard";
/*     */   public static final String USE_FILTERED_SPREADS_DISTANCE = "useFilteredSpreadsDistance";
/*     */   public static final String PRICE_SHIFT_MULTIPLICAND = "shiftMultiplicand";
/*     */   public static final int TRADING_ALLOWED = 0;
/*     */   public static final int TRADING_TEMPORARY_BLOCKED = 1;
/*     */   public static final int TRADING_RESTRICTED = 2;
/*     */ 
/*     */   public InstrumentStatusUpdateMessage()
/*     */   {
/*  33 */     setType("instrumentStatus");
/*     */   }
/*     */ 
/*     */   public InstrumentStatusUpdateMessage(ProtocolMessage message)
/*     */   {
/*  42 */     super(message);
/*  43 */     setType("instrumentStatus");
/*     */ 
/*  45 */     setInstrument(message.getString("instrument"));
/*  46 */     setTradable(message.getInteger("tradable"));
/*  47 */     setCriticSpread(message.getInteger("critic_spread"));
/*  48 */     setSpreadGuardMinOfferAmount(message.getBigDecimal("spreadGuard"));
/*  49 */     setUseFilteredSpreadsDistance(message.getInteger("useFilteredSpreadsDistance"));
/*  50 */     setSmartSpreadLimit(message.getBigDecimal("smart_limit"));
/*  51 */     setPriceShiftMultiplicand(message.getBigDecimal("shiftMultiplicand"));
/*     */   }
/*     */ 
/*     */   public String getInstrument()
/*     */   {
/*  60 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String instrument)
/*     */   {
/*  69 */     put("instrument", instrument);
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary()
/*     */   {
/*  77 */     return getInstrument().substring(0, 3);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary()
/*     */   {
/*  85 */     return getInstrument().substring(4);
/*     */   }
/*     */ 
/*     */   public int getTradable()
/*     */   {
/*  93 */     return getInt("tradable");
/*     */   }
/*     */ 
/*     */   public void setTradable(Integer tradable)
/*     */   {
/* 101 */     put("tradable", tradable);
/*     */   }
/*     */ 
/*     */   public Integer getCriticSpread()
/*     */   {
/* 110 */     String criticSpread = getString("critic_spread");
/* 111 */     if (criticSpread != null) {
/* 112 */       return new Integer(criticSpread);
/*     */     }
/* 114 */     return Integer.valueOf(20);
/*     */   }
/*     */ 
/*     */   public void setCriticSpread(Integer criticSpread)
/*     */   {
/* 124 */     if (criticSpread != null)
/* 125 */       put("critic_spread", criticSpread.toString());
/*     */     else
/* 127 */       put("critic_spread", null);
/*     */   }
/*     */ 
/*     */   public Boolean isAdvancedLogging()
/*     */   {
/* 135 */     return Boolean.valueOf(getBoolean("logging"));
/*     */   }
/*     */ 
/*     */   public void setAdvancedLogging(Boolean advancedLoggong)
/*     */   {
/* 142 */     put("logging", advancedLoggong);
/*     */   }
/*     */ 
/*     */   public BigDecimal getSpreadGuardMinOfferAmount()
/*     */   {
/* 149 */     String spreadGuardMinOfferAmount = getString("spreadGuard");
/* 150 */     if (spreadGuardMinOfferAmount != null) {
/* 151 */       return new BigDecimal(spreadGuardMinOfferAmount);
/*     */     }
/* 153 */     return new BigDecimal("1000000");
/*     */   }
/*     */ 
/*     */   public void setSpreadGuardMinOfferAmount(BigDecimal spreadGuardMinOfferAmount)
/*     */   {
/* 161 */     if (spreadGuardMinOfferAmount != null)
/* 162 */       put("spreadGuard", spreadGuardMinOfferAmount.toPlainString());
/*     */     else
/* 164 */       put("spreadGuard", null);
/*     */   }
/*     */ 
/*     */   public BigDecimal getPriceShiftMultiplicand()
/*     */   {
/* 174 */     String spreadGuardMinOfferAmount = getString("shiftMultiplicand");
/* 175 */     if (spreadGuardMinOfferAmount != null) {
/* 176 */       return new BigDecimal(spreadGuardMinOfferAmount);
/*     */     }
/* 178 */     return new BigDecimal("1");
/*     */   }
/*     */ 
/*     */   public void setPriceShiftMultiplicand(BigDecimal priceShiftMultiplicand)
/*     */   {
/* 187 */     if (priceShiftMultiplicand != null)
/* 188 */       put("shiftMultiplicand", priceShiftMultiplicand.toPlainString());
/*     */     else
/* 190 */       put("shiftMultiplicand", "1");
/*     */   }
/*     */ 
/*     */   public void setSmartSpreadLimit(BigDecimal spreadLimit)
/*     */   {
/* 199 */     if (spreadLimit != null)
/* 200 */       put("smart_limit", spreadLimit.toPlainString());
/*     */     else
/* 202 */       put("smart_limit", null);
/*     */   }
/*     */ 
/*     */   public BigDecimal getSmartSpreadLimit()
/*     */   {
/* 211 */     String spreadLimit = getString("smart_limit");
/* 212 */     if (spreadLimit != null) {
/* 213 */       return new BigDecimal(spreadLimit);
/*     */     }
/* 215 */     return new BigDecimal("100");
/*     */   }
/*     */ 
/*     */   public Integer getUseFilteredSpreadsDistance()
/*     */   {
/* 223 */     String useFilteredSpreadsDistance = getString("useFilteredSpreadsDistance");
/* 224 */     if (useFilteredSpreadsDistance != null) {
/* 225 */       return new Integer(useFilteredSpreadsDistance);
/*     */     }
/* 227 */     return Integer.valueOf(3);
/*     */   }
/*     */ 
/*     */   public void setUseFilteredSpreadsDistance(Integer useFilteredSpreadsDistance)
/*     */   {
/* 235 */     if (useFilteredSpreadsDistance != null)
/* 236 */       put("useFilteredSpreadsDistance", useFilteredSpreadsDistance.toString());
/*     */     else
/* 238 */       put("useFilteredSpreadsDistance", null);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage
 * JD-Core Version:    0.6.0
 */