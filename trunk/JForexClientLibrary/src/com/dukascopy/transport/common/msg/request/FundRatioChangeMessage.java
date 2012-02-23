/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class FundRatioChangeMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "ratioChange";
/*     */   public static final String UNIQ_ID = "uniqId";
/*     */   public static final String FUND_ACCOUNT_ID = "facct_id";
/*     */   public static final String OLD_RATIO = "oldRatio";
/*     */   public static final String NEW_RATIO = "newRatio";
/*     */   public static final String EXPOSURES = "exposures";
/*     */   public static final String STATUS = "status";
/*     */   public static final String REASON = "reason";
/*     */ 
/*     */   public FundRatioChangeMessage()
/*     */   {
/*  35 */     setType("ratioChange");
/*     */   }
/*     */ 
/*     */   public FundRatioChangeMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  45 */     super(s);
/*  46 */     setType("ratioChange");
/*     */   }
/*     */ 
/*     */   public FundRatioChangeMessage(JSONObject s)
/*     */     throws ParseException
/*     */   {
/*  56 */     super(s);
/*  57 */     setType("ratioChange");
/*     */   }
/*     */ 
/*     */   public FundRatioChangeMessage(ProtocolMessage message)
/*     */   {
/*  65 */     super(message);
/*  66 */     setType("ratioChange");
/*  67 */     put("uniqId", message.getString("uniqId"));
/*  68 */     put("facct_id", message.getString("facct_id"));
/*  69 */     put("oldRatio", message.getString("oldRatio"));
/*  70 */     put("newRatio", message.getString("newRatio"));
/*  71 */     put("exposures", message.getJSONArray("exposures"));
/*  72 */     put("status", message.getString("status"));
/*  73 */     put("reason", message.getString("reason"));
/*     */   }
/*     */ 
/*     */   public String getUniqId()
/*     */   {
/*  83 */     return getString("uniqId");
/*     */   }
/*     */ 
/*     */   public void setUniqId(String id)
/*     */   {
/*  92 */     put("uniqId", id);
/*     */   }
/*     */ 
/*     */   public String getFundAcctId()
/*     */   {
/* 101 */     return getString("facct_id");
/*     */   }
/*     */ 
/*     */   public void setFundAcctId(String id)
/*     */   {
/* 110 */     put("facct_id", id);
/*     */   }
/*     */ 
/*     */   public Integer getOldRatio()
/*     */   {
/* 119 */     String oldRatio = getString("oldRatio");
/* 120 */     if (oldRatio != null) {
/* 121 */       return new Integer(oldRatio);
/*     */     }
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   public void setOldRatio(Integer oldRatio)
/*     */   {
/* 133 */     put("oldRatio", oldRatio.toString());
/*     */   }
/*     */ 
/*     */   public Integer getNewRatio()
/*     */   {
/* 142 */     String newRatio = getString("newRatio");
/* 143 */     if (newRatio != null) {
/* 144 */       return new Integer(newRatio);
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public void setNewRatio(Integer newRatio)
/*     */   {
/* 156 */     put("newRatio", newRatio.toString());
/*     */   }
/*     */ 
/*     */   public Map<String, BigDecimal> getExposures()
/*     */   {
/* 165 */     Map exposures = new HashMap();
/* 166 */     JSONArray exposuresArray = getJSONArray("exposures");
/* 167 */     if (exposuresArray != null) {
/* 168 */       for (int i = 0; i < exposuresArray.length(); i++) {
/* 169 */         String instrumentExposure = exposuresArray.getString(i);
/* 170 */         String instrument = instrumentExposure.split(" ")[0];
/* 171 */         BigDecimal exposure = new BigDecimal(instrumentExposure.split(" ")[1]);
/* 172 */         exposures.put(instrument, exposure);
/*     */       }
/*     */     }
/* 175 */     return exposures;
/*     */   }
/*     */ 
/*     */   public void setExposures(Map<String, BigDecimal> exposures)
/*     */   {
/* 184 */     put("exposures", new JSONArray());
/* 185 */     JSONArray exposuresArray = getJSONArray("exposures");
/* 186 */     for (String instrument : exposures.keySet())
/* 187 */       exposuresArray.put(instrument + " " + exposures.get(instrument));
/*     */   }
/*     */ 
/*     */   public String getStatus()
/*     */   {
/* 197 */     return getString("status");
/*     */   }
/*     */ 
/*     */   public void setStatus(String status)
/*     */   {
/* 206 */     put("status", status);
/*     */   }
/*     */ 
/*     */   public boolean isStatusError()
/*     */   {
/* 215 */     return getStatus().equals("ERROR");
/*     */   }
/*     */ 
/*     */   public String getReason()
/*     */   {
/* 224 */     return getString("reason");
/*     */   }
/*     */ 
/*     */   public void setReason(String reason)
/*     */   {
/* 233 */     put("reason", reason);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.FundRatioChangeMessage
 * JD-Core Version:    0.6.0
 */