/*     */ package com.dukascopy.transport.common.msg.exposure;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.Serializable;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class AccountExposureMessage extends ProtocolMessage
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 220706151507L;
/*     */   public static final String TYPE = "accExp";
/*     */   public static final String ACCOUNT_ID = "accountId";
/*     */   public static final String MARKETPLACE_NAME = "marketPlaceName";
/*     */   public static final String EXPOSURES = "exposures";
/*     */ 
/*     */   public AccountExposureMessage()
/*     */   {
/*  34 */     setType("accExp");
/*     */   }
/*     */ 
/*     */   public AccountExposureMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  44 */     super(s);
/*  45 */     setType("accExp");
/*     */   }
/*     */ 
/*     */   public AccountExposureMessage(JSONObject s)
/*     */     throws ParseException
/*     */   {
/*  55 */     super(s);
/*  56 */     setType("accExp");
/*     */   }
/*     */ 
/*     */   public AccountExposureMessage(ProtocolMessage message)
/*     */   {
/*  65 */     super(message);
/*  66 */     setType("accExp");
/*  67 */     put("accountId", message.getString("accountId"));
/*  68 */     put("marketPlaceName", message.getString("marketPlaceName"));
/*  69 */     put("exposures", message.getJSONArray("exposures"));
/*     */   }
/*     */ 
/*     */   public String getAccountId() {
/*  73 */     return getString("accountId");
/*     */   }
/*     */ 
/*     */   public void setAccountId(String accountId) {
/*  77 */     put("accountId", accountId);
/*     */   }
/*     */ 
/*     */   public String getMarketPlaceName() {
/*  81 */     return getString("marketPlaceName");
/*     */   }
/*     */ 
/*     */   public void setMarketPlaceName(String accountName) {
/*  85 */     put("marketPlaceName", accountName);
/*     */   }
/*     */ 
/*     */   public List<AccountInstrumentExposureMessage> getExposures() {
/*  89 */     List exposures = new ArrayList();
/*     */     try {
/*  91 */       JSONArray exposuresArray = getJSONArray("exposures");
/*  92 */       if (exposuresArray != null)
/*  93 */         for (int i = 0; i < exposuresArray.length(); i++)
/*  94 */           exposures.add(new AccountInstrumentExposureMessage(exposuresArray.getJSONObject(i)));
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/*     */     }
/*  99 */     return exposures;
/*     */   }
/*     */ 
/*     */   public void setExposures(List<AccountInstrumentExposureMessage> exposures) {
/* 103 */     put("exposures", new JSONArray());
/* 104 */     JSONArray exposuresArray = getJSONArray("exposures");
/* 105 */     for (AccountInstrumentExposureMessage exposure : exposures)
/* 106 */       exposuresArray.put(exposure);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.exposure.AccountExposureMessage
 * JD-Core Version:    0.6.0
 */