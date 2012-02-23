/*     */ package com.dukascopy.transport.common.msg.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class ApiSessionReportMessage extends ProtocolMessage
/*     */   implements Serializable
/*     */ {
/*     */   public static final String TYPE = "sesn_report";
/*     */   public static final int MODE_REGULAR = 0;
/*     */   public static final int MODE_RESERVE = 1;
/*     */   public static final int TYPE_MINA = 0;
/*     */   public static final int TYPE_HTTP = 1;
/*     */   public static final int TYPE_DATAFEED = 2;
/*     */   public static final int TYPE_FIX = 3;
/*     */   public static final String SESN_COUNT = "ssn_count";
/*     */   public static final String API_URL = "api_url";
/*     */   public static final String API_REGION = "api_region";
/*     */   public static final String API_UCN = "api_ucn";
/*     */   public static final String API_MAX_USERS = "api_max_usrs";
/*     */   public static final String API_MODE = "api_mode";
/*     */   public static final String API_TYPE = "api_type";
/*     */   public static final String SESSN_LIST = "session_list";
/*     */ 
/*     */   public ApiSessionReportMessage()
/*     */   {
/*  32 */     setType("sesn_report");
/*     */   }
/*     */ 
/*     */   public ApiSessionReportMessage(ProtocolMessage msg) {
/*  36 */     super(msg);
/*  37 */     setType("sesn_report");
/*  38 */     put("ssn_count", msg.getInt("ssn_count"));
/*  39 */     put("api_url", msg.getString("api_url"));
/*  40 */     put("api_region", msg.getString("api_region"));
/*  41 */     put("api_ucn", msg.getString("api_ucn"));
/*  42 */     put("api_max_usrs", msg.getInt("api_max_usrs"));
/*  43 */     put("api_mode", msg.getInt("api_mode"));
/*  44 */     put("api_type", msg.getInt("api_type"));
/*  45 */     put("session_list", msg.getString("session_list"));
/*     */   }
/*     */ 
/*     */   public void setSessionCount(int count) {
/*  49 */     put("ssn_count", count);
/*     */   }
/*     */ 
/*     */   public void setApiUrl(String apiUrl) {
/*  53 */     put("api_url", apiUrl);
/*     */   }
/*     */ 
/*     */   public void setApiRegion(String region) {
/*  57 */     put("api_region", region);
/*     */   }
/*     */ 
/*     */   public void setApiUCN(String ucn) {
/*  61 */     put("api_ucn", ucn);
/*     */   }
/*     */ 
/*     */   public void setApiMaxUsers(int maxUsers) {
/*  65 */     put("api_max_usrs", maxUsers);
/*     */   }
/*     */ 
/*     */   public void setApiMode(int mode) {
/*  69 */     put("api_mode", mode);
/*     */   }
/*     */ 
/*     */   public void setApiType(int type) {
/*  73 */     put("api_type", type);
/*     */   }
/*     */ 
/*     */   public int getSessionCount() {
/*  77 */     return getInt("ssn_count");
/*     */   }
/*     */ 
/*     */   public String getApiUrl() {
/*  81 */     return getString("api_url");
/*     */   }
/*     */ 
/*     */   public String getApiRegion() {
/*  85 */     return getString("api_region");
/*     */   }
/*     */ 
/*     */   public String getApiUCN() {
/*  89 */     return getString("api_ucn");
/*     */   }
/*     */ 
/*     */   public int getApiMaxUsers() {
/*  93 */     return getInt("api_max_usrs");
/*     */   }
/*     */ 
/*     */   public int getApiMode() {
/*  97 */     return getInt("api_mode");
/*     */   }
/*     */ 
/*     */   public int getApiType() {
/* 101 */     return getInt("api_type");
/*     */   }
/*     */ 
/*     */   public void setSessionList(String sessionList) {
/* 105 */     put("session_list", sessionList);
/*     */   }
/*     */ 
/*     */   public String getSessionList() {
/* 109 */     return getString("session_list");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ApiSessionReportMessage
 * JD-Core Version:    0.6.0
 */