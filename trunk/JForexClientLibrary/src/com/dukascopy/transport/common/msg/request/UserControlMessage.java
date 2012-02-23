/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.Serializable;
/*     */ import java.text.ParseException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class UserControlMessage extends ProtocolMessage
/*     */   implements Serializable
/*     */ {
/*     */   public static final String TYPE = "usrCtl";
/*     */   public static final String KICK = "kick";
/*     */   public static final String BAN = "ban";
/*     */   public static final String LOGIN_ID = "loginId";
/*     */   public static final String SID = "sid";
/*     */   public static final String USERNAME = "username";
/*     */   public static final String ACTION = "actn";
/*     */   public static final String TSID = "trsid";
/*     */ 
/*     */   public UserControlMessage()
/*     */   {
/*  32 */     setType("usrCtl");
/*     */   }
/*     */ 
/*     */   public UserControlMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  42 */     super(s);
/*  43 */     setType("usrCtl");
/*     */   }
/*     */ 
/*     */   public UserControlMessage(JSONObject s) throws ParseException {
/*  47 */     super(s);
/*  48 */     setType("usrCtl");
/*     */   }
/*     */ 
/*     */   public UserControlMessage(ProtocolMessage message)
/*     */   {
/*  58 */     setType("usrCtl");
/*  59 */     setUserId(message.getUserId());
/*  60 */     put("loginId", message.getString("loginId"));
/*  61 */     put("actn", message.getString("actn"));
/*  62 */     put("sid", message.getString("sid"));
/*  63 */     put("trsid", message.getString("trsid"));
/*  64 */     put("username", message.getString("username"));
/*     */   }
/*     */ 
/*     */   public String getLoginId() {
/*  68 */     return getString("loginId");
/*     */   }
/*     */ 
/*     */   public void setLoginId(String loginId) {
/*  72 */     put("loginId", loginId);
/*     */   }
/*     */ 
/*     */   public String getAction()
/*     */   {
/*  77 */     return getString("actn");
/*     */   }
/*     */ 
/*     */   public void setAction(String action) {
/*  81 */     put("actn", action);
/*     */   }
/*     */ 
/*     */   public String getSid() {
/*  85 */     return getString("sid");
/*     */   }
/*     */ 
/*     */   public void setSid(String sid) {
/*  89 */     put("sid", sid);
/*     */   }
/*     */ 
/*     */   public String getUserName() {
/*  93 */     return getString("username");
/*     */   }
/*     */ 
/*     */   public void setUserName(String username) {
/*  97 */     put("username", username);
/*     */   }
/*     */   public String getTransportSid() {
/* 100 */     return getString("trsid");
/*     */   }
/*     */ 
/*     */   public void setTransportSid(String sid) {
/* 104 */     put("trsid", sid);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.UserControlMessage
 * JD-Core Version:    0.6.0
 */