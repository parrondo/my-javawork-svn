/*     */ package com.dukascopy.transport.common.msg.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.json.JSONArray;
/*     */ 
/*     */ public class AuthentificationResponseMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE_REGULAR = "1";
/*     */   public static final String TYPE_GLOBAL = "2";
/*     */   public static final String TYPE = "authresp";
/*     */   public static final String USERNAME = "username";
/*     */   public static final String ACCOUNT_ID = "accountId";
/*     */   public static final String LOGIN_ID = "loginId";
/*     */   public static final String MANAGER_ID = "managerId";
/*     */   public static final String USER_TYPE = "userType";
/*     */   public static final String CLONED = "cloned";
/*     */   public static final String READ_ONLY = "readOnly";
/*     */   public static final String NO_FEED = "noFeed";
/*     */   public static final String INSTRUMENTS = "instruments";
/*     */   public static final String SS_TYPE = "ssType";
/*     */   public static final String SS_TYPE_PRODUCER = "producer";
/*     */   public static final String SS_TYPE_CONSUMER = "consumer";
/*     */ 
/*     */   public AuthentificationResponseMessage()
/*     */   {
/*  34 */     setType("authresp");
/*     */   }
/*     */ 
/*     */   public AuthentificationResponseMessage(ProtocolMessage message) {
/*  38 */     super(message);
/*  39 */     setType("authresp");
/*  40 */     put("username", message.getString("username"));
/*  41 */     put("accountId", message.getString("accountId"));
/*  42 */     put("loginId", message.getString("loginId"));
/*  43 */     put("managerId", message.getString("managerId"));
/*  44 */     put("userType", message.getString("userType"));
/*  45 */     put("cloned", message.getBool("cloned"));
/*  46 */     put("readOnly", message.getBool("readOnly"));
/*  47 */     put("noFeed", message.getBool("noFeed"));
/*  48 */     put("instruments", message.getJSONArray("instruments"));
/*  49 */     put("ssType", message.getString("ssType"));
/*     */   }
/*     */ 
/*     */   public AuthentificationResponseMessage(String username, String accountId, String loginId, String userType)
/*     */   {
/*  55 */     setType("authresp");
/*  56 */     put("username", username);
/*  57 */     put("accountId", accountId);
/*  58 */     put("loginId", loginId);
/*  59 */     put("userType", userType);
/*     */   }
/*     */ 
/*     */   public void setManagerId(String managerId) {
/*  63 */     put("managerId", managerId);
/*     */   }
/*     */ 
/*     */   public String getManagerId() {
/*  67 */     return getString("managerId");
/*     */   }
/*     */ 
/*     */   public String getAccountId() {
/*  71 */     return getString("accountId");
/*     */   }
/*     */ 
/*     */   public String getLoginId() {
/*  75 */     return getString("loginId");
/*     */   }
/*     */ 
/*     */   public String getUsername() {
/*  79 */     return getString("username");
/*     */   }
/*     */ 
/*     */   public String getUserType() {
/*  83 */     return getString("userType");
/*     */   }
/*     */ 
/*     */   public Boolean isCloned()
/*     */   {
/*  88 */     return getBool("cloned");
/*     */   }
/*     */ 
/*     */   public void setCloned(Boolean isCloned) {
/*  92 */     put("cloned", isCloned);
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly) {
/*  96 */     put("readOnly", readOnly);
/*     */   }
/*     */ 
/*     */   public Boolean getReadOnly() {
/* 100 */     return Boolean.valueOf((isNull("readOnly")) || (getBoolean("readOnly")));
/*     */   }
/*     */ 
/*     */   public void setNoFeed(boolean noFeed) {
/* 104 */     put("noFeed", noFeed);
/*     */   }
/*     */ 
/*     */   public Boolean getNoFeed() {
/* 108 */     return Boolean.valueOf((!isNull("noFeed")) && (getBoolean("noFeed")));
/*     */   }
/*     */ 
/*     */   public Set<String> getInstruments() {
/* 112 */     Set output = new HashSet();
/* 113 */     JSONArray jsonArray = getJSONArray("instruments");
/* 114 */     for (int i = 0; i < jsonArray.length(); i++) {
/* 115 */       output.add(jsonArray.getString(i));
/*     */     }
/* 117 */     return output;
/*     */   }
/*     */ 
/*     */   public void setInstruments(Set<String> instruments) {
/* 121 */     JSONArray jsonArray = new JSONArray();
/* 122 */     for (String instrument : instruments) {
/* 123 */       jsonArray.put(instrument);
/*     */     }
/* 125 */     put("instruments", jsonArray);
/*     */   }
/*     */ 
/*     */   public void setSsType(String ssType)
/*     */   {
/* 130 */     put("ssType", ssType);
/*     */   }
/*     */ 
/*     */   public String getSsType() {
/* 134 */     if (isNull("ssType")) {
/* 135 */       return null;
/*     */     }
/* 137 */     return getString("ssType");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.AuthentificationResponseMessage
 * JD-Core Version:    0.6.0
 */