/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ActivityLoginMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "alm";
/*     */   public static final String LOCAL_IP = "localIp";
/*     */   public static final String REMOTE_IP = "remoteIp";
/*     */   public static final String REMOTE_HOST = "remoteHost";
/*     */   public static final String LOGIN = "usedLogin";
/*     */   public static final String PASS = "usedPass";
/*     */ 
/*     */   public ActivityLoginMessage()
/*     */   {
/*  26 */     setType("alm");
/*     */   }
/*     */ 
/*     */   public ActivityLoginMessage(String json) throws ParseException {
/*  30 */     super(json);
/*  31 */     setType("alm");
/*     */   }
/*     */ 
/*     */   public ActivityLoginMessage(ProtocolMessage msg) {
/*  35 */     super(msg);
/*  36 */     setType("alm");
/*  37 */     setLocalIp(msg.getString("localIp"));
/*  38 */     setRemoteIp(msg.getString("remoteIp"));
/*  39 */     setRemoteHost(msg.getString("remoteHost"));
/*  40 */     setUsedLogin(msg.getString("usedLogin"));
/*  41 */     setUsedPass(msg.getString("usedPass"));
/*     */   }
/*     */ 
/*     */   public ActivityLoginMessage(JSONObject msg)
/*     */   {
/*  47 */     setType("alm");
/*  48 */     setUserId(msg.getString("userId"));
/*  49 */     put("check_time", msg.getString("check_time"));
/*  50 */     setLocalIp(msg.getString("localIp"));
/*  51 */     setRemoteIp(msg.getString("remoteIp"));
/*  52 */     setRemoteHost(msg.getString("remoteHost"));
/*  53 */     setUsedLogin(msg.getString("usedLogin"));
/*  54 */     setUsedPass(msg.getString("usedPass"));
/*     */   }
/*     */ 
/*     */   public ActivityLoginMessage(String localIp, String remoteIp, String remoteHost, String usedLogin, String usedPass)
/*     */   {
/*  60 */     setType("alm");
/*  61 */     setLocalIp(localIp);
/*  62 */     setRemoteIp(remoteIp);
/*  63 */     setRemoteHost(remoteHost);
/*  64 */     setUsedLogin(usedLogin);
/*  65 */     setUsedPass(usedPass);
/*     */   }
/*     */ 
/*     */   public String getLocalIp() {
/*  69 */     return getString("localIp");
/*     */   }
/*     */ 
/*     */   public void setLocalIp(String localIp) {
/*  73 */     put("localIp", localIp);
/*     */   }
/*     */ 
/*     */   public String getRemoteIp() {
/*  77 */     return getString("remoteIp");
/*     */   }
/*     */ 
/*     */   public void setRemoteIp(String remoteIp) {
/*  81 */     put("remoteIp", remoteIp);
/*     */   }
/*     */ 
/*     */   public String getRemoteHost() {
/*  85 */     return getString("remoteHost");
/*     */   }
/*     */ 
/*     */   public void setRemoteHost(String remoteHost) {
/*  89 */     put("remoteHost", remoteHost);
/*     */   }
/*     */ 
/*     */   public String getUsedLogin() {
/*  93 */     return getString("usedLogin");
/*     */   }
/*     */ 
/*     */   public void setUsedLogin(String usedLogin) {
/*  97 */     put("usedLogin", usedLogin);
/*     */   }
/*     */ 
/*     */   public String getUsedPass() {
/* 101 */     return getString("usedPass");
/*     */   }
/*     */ 
/*     */   public void setUsedPass(String usedPass) {
/* 105 */     put("usedPass", usedPass);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ActivityLoginMessage
 * JD-Core Version:    0.6.0
 */