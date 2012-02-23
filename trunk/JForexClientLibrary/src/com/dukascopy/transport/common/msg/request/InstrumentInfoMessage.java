/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ 
/*     */ public class InstrumentInfoMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "instrumentInfo";
/*     */   public static final String INSTRUMENT_TYPE_CURRENCY = "ccy";
/*     */   public static final String INSTRUMENT_TYPE_STOCK_CFD = "stock";
/*     */   public static final String INSTRUMENT_TYPE_INDEX_CFD = "index";
/*     */   public static final String SOURCE_MARKET_MAKER = "mm";
/*     */   public static final String SOURCE_INTERBANK = "ib";
/*     */   public static final String INSTRUMENT_ID = "instrId";
/*     */   public static final String BASE_INSTRUMENT_ID = "baseId";
/*     */   public static final String NAME = "name";
/*     */   public static final String INSTRUMENT_TYPE = "type";
/*     */   public static final String SOURCE = "src";
/*     */   public static final String DESCRIPTION = "description";
/*     */   public static final String PIP_VALUE = "pip";
/*     */   public static final String PRIMARY = "primary";
/*     */   public static final String SECONDARY = "secondary";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String REVERSE = "rev";
/*     */ 
/*     */   public InstrumentInfoMessage()
/*     */   {
/*  45 */     setType("instrumentInfo");
/*     */   }
/*     */ 
/*     */   public InstrumentInfoMessage(ProtocolMessage message)
/*     */   {
/*  54 */     super(message);
/*  55 */     setType("instrumentInfo");
/*  56 */     setInstrumentTypeCurrency(message.getString("ccy"));
/*  57 */     setInstrumentId(message.getString("instrId"));
/*  58 */     setBaseInstrumentId(message.getString("baseId"));
/*  59 */     setName(message.getString("name"));
/*  60 */     setInstrumentType(message.getString("type"));
/*  61 */     setSource(message.getString("src"));
/*  62 */     setDescription(message.getString("description"));
/*  63 */     setPipValue(message.getBigDecimal("pip"));
/*  64 */     setPrimary(message.getString("primary"));
/*  65 */     setSecondary(message.getString("secondary"));
/*  66 */     setReverse(Boolean.valueOf(message.getBoolean("rev")));
/*     */   }
/*     */ 
/*     */   public void setInstrumentTypeCurrency(String currency) {
/*  70 */     put("ccy", currency);
/*     */   }
/*     */ 
/*     */   public String getInstrumentTypeCurrency() {
/*  74 */     return getString("ccy");
/*     */   }
/*     */ 
/*     */   public void setInstrumentId(String instrumentId) {
/*  78 */     put("instrId", instrumentId);
/*     */   }
/*     */ 
/*     */   public String getInstrumentId() {
/*  82 */     return getString("instrId");
/*     */   }
/*     */ 
/*     */   public void setBaseInstrumentId(String baseInstrumentId) {
/*  86 */     put("baseId", baseInstrumentId);
/*     */   }
/*     */ 
/*     */   public String getBaseInstrumentId() {
/*  90 */     return getString("baseId");
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  94 */     put("name", name);
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  98 */     return getString("name");
/*     */   }
/*     */ 
/*     */   public void setInstrumentType(String instrumentType) {
/* 102 */     put("type", instrumentType);
/*     */   }
/*     */ 
/*     */   public String getInstrumentType() {
/* 106 */     return getString("type");
/*     */   }
/*     */ 
/*     */   public void setSource(String source) {
/* 110 */     put("src", source);
/*     */   }
/*     */ 
/*     */   public String getSource() {
/* 114 */     return getString("src");
/*     */   }
/*     */ 
/*     */   public void setDescription(String description) {
/* 118 */     put("description", description);
/*     */   }
/*     */ 
/*     */   public String getDescription() {
/* 122 */     return getString("description");
/*     */   }
/*     */ 
/*     */   public void setPipValue(BigDecimal pipValue) {
/* 126 */     put("pip", pipValue);
/*     */   }
/*     */ 
/*     */   public BigDecimal getPipValue() {
/* 130 */     return getBigDecimal("pip");
/*     */   }
/*     */ 
/*     */   public void setPrimary(String primary) {
/* 134 */     put("primary", primary);
/*     */   }
/*     */ 
/*     */   public String getPrimary() {
/* 138 */     return getString("primary");
/*     */   }
/*     */ 
/*     */   public void setSecondary(String secondary) {
/* 142 */     put("secondary", secondary);
/*     */   }
/*     */ 
/*     */   public String getSecondary() {
/* 146 */     return getString("secondary");
/*     */   }
/*     */ 
/*     */   public void setReverse(Boolean reverse) {
/* 150 */     put("rev", reverse);
/*     */   }
/*     */ 
/*     */   public Boolean getReverse() {
/* 154 */     return getBool("rev");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.InstrumentInfoMessage
 * JD-Core Version:    0.6.0
 */