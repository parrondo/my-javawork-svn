/*     */ package com.dukascopy.transport.common.msg.request;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class ActivityMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "act";
/*     */   public static final String TYPE_UPDATE_PREV = "UPD_PREV";
/*     */   public static final String TYPE_LOG_IN = "LOG_IN";
/*     */   public static final String TYPE_LOG_OUT = "LOG_OUT";
/*     */   public static final String TYPE_NEW_ORDER = "NEW_ORDER";
/*     */   public static final String TYPE_ORDER_FINISHED = "ORDER_FINISHED";
/*     */   public static final String TYPE_ORDER_STATE_CHANAGE = "ORDER_STATE_CHANAGE";
/*     */   public static final String TYPE_WARNING = "WARNING";
/*     */   public static final String TYPE_SYSTEM = "SYSTEM";
/*     */   public static final String TYPE_ORDER_DATA_CHANGE = "ORDER_DATA_CHANGE";
/*     */   public static final String SERVICE_CUSTODIAN = "Custodian";
/*     */   public static final String SERVICE_API = "Api";
/*     */   public static final String SERVICE_ROUTER = "Router";
/*     */   public static final String SERVICE_AUTH = "Auth";
/*     */   public static final String MON_ALOG = "system";
/*     */   public static final String MON_RREPORT = "RPOT_REP";
/*     */   public static final String MON_CUSTODIAN = "CUST";
/*     */   public static final String MON_EXECUTORS = "EXEC";
/*     */   public static final String ACTIVITY_TYPE = "activityType";
/*     */   public static final String NOTIFICATION = "notf";
/*     */   public static final String NOTIF_LEVEL = "notifLevel";
/*     */   public static final String SERVICE = "service";
/*     */   public static final String TRANSACTION_ID = "transactionId";
/*     */   public static final String PLATFORM = "platform";
/*     */   public static final String ORDER_ID = "orderId";
/*     */   public static final String ORDER_GR_ID = "orderGroupId";
/*     */   public static final String FIELD_CH = "fieldCh";
/*     */   public static final String LOGIN_INFO = "loginInfo";
/*     */   public static final String ATTACH_MSG = "att_msg";
/*     */   public static final String MONITOR = "mon";
/*     */   public static final String EQUITY = "equity";
/*     */   public static final String USABLE_MARGIN = "usableMargin";
/*     */   public static final String NOTIFICATION_CODE = "notifCode";
/*     */   public static final String CLIENT_TIME = "ct";
/*     */ 
/*     */   public ActivityMessage()
/*     */   {
/*  74 */     setType("act");
/*     */   }
/*     */ 
/*     */   public ActivityMessage(String json) throws ParseException {
/*  78 */     super(json);
/*  79 */     setType("act");
/*     */   }
/*     */ 
/*     */   public ActivityMessage(ProtocolMessage msg) {
/*  83 */     super(msg);
/*  84 */     setType("act");
/*  85 */     put("activityType", msg.getString("activityType"));
/*  86 */     put("notf", msg.getString("notf"));
/*  87 */     put("notifLevel", msg.getString("notifLevel"));
/*  88 */     put("service", msg.getString("service"));
/*  89 */     put("transactionId", msg.getString("transactionId"));
/*  90 */     put("platform", msg.getString("platform"));
/*  91 */     put("mon", msg.getString("mon"));
/*  92 */     put("orderId", msg.getString("orderId"));
/*  93 */     put("orderGroupId", msg.getString("orderGroupId"));
/*  94 */     put("fieldCh", msg.getJSONArray("fieldCh"));
/*     */     try {
/*  96 */       put("loginInfo", msg.getJSONObject("loginInfo")); } catch (Exception ec) {
/*     */     }
/*     */     try {
/*  99 */       put("att_msg", msg.getJSONObject("att_msg")); } catch (Exception ec) {
/*     */     }
/* 101 */     put("equity", msg.getString("equity"));
/* 102 */     put("usableMargin", msg.getString("usableMargin"));
/* 103 */     put("notifCode", msg.getString("notifCode"));
/*     */   }
/*     */ 
/*     */   public ActivityMessage(String activityType, String notf, String notifLevel, String service, String transactionId, String userId, String platform, String orderId, String orderGroupId, List<ActivityFieldChangeMessage> fieldCh, ActivityLoginMessage loginInfo, ProtocolMessage msg, String monitor, BigDecimal equity, BigDecimal margin, String accountLogin)
/*     */   {
/* 108 */     setType("act");
/* 109 */     setActivityType(activityType);
/* 110 */     setNotf(notf);
/* 111 */     setNotifLevel(notifLevel);
/* 112 */     setService(service);
/* 113 */     setTransactionId(transactionId);
/* 114 */     setUserId(userId);
/* 115 */     setCheckTime(System.currentTimeMillis());
/* 116 */     setPlatform(platform);
/* 117 */     setOrderId(orderId);
/* 118 */     setOrderGroupId(orderGroupId);
/* 119 */     setFieldCh(fieldCh);
/* 120 */     setLoginInfo(loginInfo);
/* 121 */     setAttach(msg);
/* 122 */     setMonitor(monitor);
/* 123 */     setEquity(equity);
/* 124 */     setUsableMargin(margin);
/* 125 */     setAcountLoginId(accountLogin);
/*     */   }
/*     */ 
/*     */   public ActivityMessage(Long clTime, String activityType, String notf, String notifLevel, String service, String transactionId, String userId, String platform, String orderId, String orderGroupId, ProtocolMessage msg, String monitor, BigDecimal equity, BigDecimal margin, String accountLogin)
/*     */   {
/* 130 */     setType("act");
/* 131 */     setActivityType(activityType);
/* 132 */     setNotf(notf);
/* 133 */     setNotifLevel(notifLevel);
/* 134 */     setService(service);
/* 135 */     setTransactionId(transactionId);
/* 136 */     setUserId(userId);
/* 137 */     setCheckTime(System.currentTimeMillis());
/* 138 */     setPlatform(platform);
/* 139 */     setOrderId(orderId);
/* 140 */     setOrderGroupId(orderGroupId);
/* 141 */     setAttach(msg);
/* 142 */     setMonitor(monitor);
/* 143 */     setEquity(equity);
/* 144 */     setUsableMargin(margin);
/* 145 */     setAcountLoginId(accountLogin);
/* 146 */     setClientTime(clTime);
/*     */   }
/*     */ 
/*     */   public ActivityMessage(String transId, String orderId, String orderGroupId)
/*     */   {
/* 151 */     setType("act");
/* 152 */     setTransactionId(transId);
/* 153 */     setOrderId(orderId);
/* 154 */     setOrderGroupId(orderGroupId);
/* 155 */     setActivityType("UPD_PREV");
/*     */   }
/*     */ 
/*     */   public void setClientTime(Long time)
/*     */   {
/* 164 */     put("ct", "" + time);
/*     */   }
/*     */ 
/*     */   public Long getClientTime() {
/* 168 */     return getLong("ct");
/*     */   }
/*     */ 
/*     */   public String getActivityType() {
/* 172 */     return getString("activityType");
/*     */   }
/*     */ 
/*     */   public void setActivityType(String activityType) {
/* 176 */     put("activityType", activityType);
/*     */   }
/*     */ 
/*     */   public String getNotf() {
/* 180 */     return getString("notf");
/*     */   }
/*     */ 
/*     */   public void setNotf(String notif) {
/* 184 */     put("notf", notif);
/*     */   }
/*     */ 
/*     */   public String getNotifLevel() {
/* 188 */     return getString("notifLevel");
/*     */   }
/*     */ 
/*     */   public void setNotifLevel(String notifLevel) {
/* 192 */     put("notifLevel", notifLevel);
/*     */   }
/*     */ 
/*     */   public String getService() {
/* 196 */     return getString("service");
/*     */   }
/*     */ 
/*     */   public void setService(String service) {
/* 200 */     put("service", service);
/*     */   }
/*     */ 
/*     */   public String getMonitor() {
/* 204 */     return getString("mon");
/*     */   }
/*     */ 
/*     */   public void setMonitor(String monitor) {
/* 208 */     put("mon", monitor);
/*     */   }
/*     */ 
/*     */   public String getTransactionId() {
/* 212 */     return getString("transactionId");
/*     */   }
/*     */ 
/*     */   public void setTransactionId(String transactionId) {
/* 216 */     put("transactionId", transactionId);
/*     */   }
/*     */ 
/*     */   public String getPlatform()
/*     */   {
/* 221 */     return getString("platform");
/*     */   }
/*     */ 
/*     */   public void setPlatform(String platform) {
/* 225 */     put("platform", platform);
/*     */   }
/*     */ 
/*     */   public String getOrderId() {
/* 229 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/* 233 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public String getOrderGroupId() {
/* 237 */     return getString("orderGroupId");
/*     */   }
/*     */ 
/*     */   public void setOrderGroupId(String orderGroupId) {
/* 241 */     put("orderGroupId", orderGroupId);
/*     */   }
/*     */ 
/*     */   public List<ActivityFieldChangeMessage> getFieldCh() {
/* 245 */     List fch = new ArrayList();
/* 246 */     JSONArray fchArr = null;
/*     */     try {
/* 248 */       fchArr = getJSONArray("fieldCh");
/* 249 */       if (fchArr != null)
/* 250 */         for (int i = 0; i < fchArr.length(); i++)
/* 251 */           fch.add(new ActivityFieldChangeMessage(fchArr.getJSONObject(i)));
/*     */     }
/*     */     catch (ParseException e)
/*     */     {
/*     */     }
/*     */     catch (NoSuchElementException ex) {
/* 257 */       System.out.println("******** " + fchArr);
/*     */     }
/* 259 */     return fch;
/*     */   }
/*     */ 
/*     */   public void setFieldCh(List<ActivityFieldChangeMessage> fieldCh) {
/* 263 */     if (fieldCh == null) {
/* 264 */       return;
/*     */     }
/* 266 */     JSONArray fcArray = new JSONArray();
/* 267 */     for (ActivityFieldChangeMessage order : fieldCh) {
/* 268 */       fcArray.put(order);
/*     */     }
/* 270 */     put("fieldCh", fcArray);
/*     */   }
/*     */ 
/*     */   public ActivityLoginMessage getLoginInfo() {
/* 274 */     ActivityLoginMessage ret = null;
/*     */     try {
/* 276 */       ret = new ActivityLoginMessage(getJSONObject("loginInfo"));
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 280 */     return ret;
/*     */   }
/*     */ 
/*     */   public void setLoginInfo(ActivityLoginMessage loginInfo) {
/* 284 */     put("loginInfo", loginInfo);
/*     */   }
/*     */ 
/*     */   public ProtocolMessage getAttachMsg() {
/* 288 */     ProtocolMessage ret = null;
/*     */     try {
/* 290 */       JSONObject obj = getJSONObject("att_msg");
/* 291 */       if (obj == null) {
/* 292 */         return null;
/*     */       }
/* 294 */       ret = ProtocolMessage.parse(obj.toString());
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 298 */     return ret;
/*     */   }
/*     */ 
/*     */   public void setAttach(ProtocolMessage msg) {
/* 302 */     put("att_msg", msg);
/*     */   }
/*     */ 
/*     */   public BigDecimal getEquity()
/*     */   {
/* 311 */     String equityString = getString("equity");
/* 312 */     if (equityString != null) {
/* 313 */       return new BigDecimal(equityString);
/*     */     }
/* 315 */     return null;
/*     */   }
/*     */ 
/*     */   public void setEquity(BigDecimal equity)
/*     */   {
/* 325 */     if (equity == null) return;
/* 326 */     put("equity", equity.toPlainString());
/*     */   }
/*     */ 
/*     */   public BigDecimal getUsableMargin()
/*     */   {
/* 335 */     String marginString = getString("usableMargin");
/* 336 */     if (marginString != null) {
/* 337 */       return new BigDecimal(marginString);
/*     */     }
/* 339 */     return null;
/*     */   }
/*     */ 
/*     */   public void setUsableMargin(BigDecimal margin)
/*     */   {
/* 349 */     if (margin == null) return;
/* 350 */     put("usableMargin", margin.toPlainString());
/*     */   }
/*     */ 
/*     */   public void setNotificationCode(NotificationMessageCode notificationCode)
/*     */   {
/* 357 */     if (notificationCode != null)
/* 358 */       put("notifCode", notificationCode.asString());
/*     */   }
/*     */ 
/*     */   public NotificationMessageCode getNotificationCode()
/*     */   {
/* 363 */     String codeString = getString("notifCode");
/* 364 */     if (codeString != null) {
/* 365 */       return NotificationMessageCode.fromString(codeString);
/*     */     }
/* 367 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.request.ActivityMessage
 * JD-Core Version:    0.6.0
 */