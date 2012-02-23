/*     */ package com.dukascopy.transport.common.msg.exposure;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class AccountInstrumentExposureMessage extends ProtocolMessage
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 220706151507L;
/*     */   public static final String TYPE = "accInstrExp";
/*     */   public static final String INSTRUMENT = "instrument";
/*     */   public static final String EXPOSURE = "exposure";
/*     */   public static final String ACCOUNT_ID = "accountId";
/*     */   public static final String MARKETPLACE_NAME = "marketPlaceName";
/*     */   public static final String SIGN_CHANGED = "signChanged";
/*     */ 
/*     */   public AccountInstrumentExposureMessage()
/*     */   {
/*  34 */     setType("accInstrExp");
/*     */   }
/*     */ 
/*     */   public AccountInstrumentExposureMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  44 */     super(s);
/*  45 */     setType("accInstrExp");
/*     */   }
/*     */ 
/*     */   public AccountInstrumentExposureMessage(JSONObject s)
/*     */     throws ParseException
/*     */   {
/*  55 */     super(s);
/*  56 */     setType("accInstrExp");
/*     */   }
/*     */ 
/*     */   public AccountInstrumentExposureMessage(ProtocolMessage message)
/*     */   {
/*  65 */     super(message);
/*  66 */     setType("accInstrExp");
/*  67 */     put("instrument", message.getString("instrument"));
/*  68 */     put("exposure", message.getBigDecimal("exposure"));
/*  69 */     put("accountId", message.getString("accountId"));
/*  70 */     put("marketPlaceName", message.getString("marketPlaceName"));
/*  71 */     put("signChanged", message.getString("signChanged"));
/*     */   }
/*     */ 
/*     */   public String getInstrument() {
/*  75 */     return getString("instrument");
/*     */   }
/*     */ 
/*     */   public void setInstrument(String accountId) {
/*  79 */     put("instrument", accountId);
/*     */   }
/*     */ 
/*     */   public BigDecimal getExposure() {
/*  83 */     String exposureString = getString("exposure");
/*  84 */     if (exposureString != null) {
/*  85 */       return new BigDecimal(exposureString);
/*     */     }
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   public void setExposure(BigDecimal exposure)
/*     */   {
/*  92 */     put("exposure", exposure.toPlainString());
/*     */   }
/*     */ 
/*     */   public String getAccountId() {
/*  96 */     return getString("accountId");
/*     */   }
/*     */ 
/*     */   public void setAccountId(String accountId) {
/* 100 */     put("accountId", accountId);
/*     */   }
/*     */ 
/*     */   public String getMarketPlaceName() {
/* 104 */     return getString("marketPlaceName");
/*     */   }
/*     */ 
/*     */   public void setMarketPlaceName(String accountName) {
/* 108 */     put("marketPlaceName", accountName);
/*     */   }
/*     */ 
/*     */   public boolean isSignChanged() {
/* 112 */     return getBoolean("signChanged");
/*     */   }
/*     */ 
/*     */   public void setSignChanged(Boolean virtual) {
/* 116 */     put("signChanged", virtual);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.exposure.AccountInstrumentExposureMessage
 * JD-Core Version:    0.6.0
 */