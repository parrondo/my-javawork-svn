/*     */ package com.dukascopy.transport.common.msg.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.text.ParseException;
/*     */ 
/*     */ public class NotificationMessage extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "notification";
/*     */   public static final String INFO = "INFO";
/*     */   public static final String WARNING = "WARNING";
/*     */   public static final String RED_ALERT = "ALERT";
/*     */   public static final String ERROR = "ERROR";
/*     */   public static final String LEVEL = "level";
/*     */   public static final String TEXT = "text";
/*     */   public static final String POSITION_ID = "positionId";
/*     */   public static final String ORDER_ID = "orderId";
/*     */   public static final String EXTERNAL_SYS_ID = "extSysId";
/*     */   public static final String STATE = "state";
/*     */   public static final String TTL = "TTL";
/*     */   public static final String NOTIFICATION_CODE = "notifCode";
/*     */   public static final String REQUEST_ID = "requestId";
/*     */   public static final String SESSION_ID = "sessionId";
/*     */ 
/*     */   public NotificationMessage()
/*     */   {
/*  65 */     setType("notification");
/*     */   }
/*     */ 
/*     */   public NotificationMessage(ProtocolMessage message)
/*     */   {
/*  74 */     super(message);
/*  75 */     setType("notification");
/*  76 */     setLevel(message.getString("level"));
/*  77 */     setPositionId(message.getString("positionId"));
/*  78 */     setText(message.getString("text"));
/*  79 */     put("orderId", message.getString("orderId"));
/*  80 */     put("state", message.getString("state"));
/*  81 */     put("extSysId", message.getString("extSysId"));
/*  82 */     put("TTL", message.getString("TTL"));
/*  83 */     put("notifCode", message.getString("notifCode"));
/*  84 */     setRequestId(message.getInteger("requestId"));
/*  85 */     setSessionId(message.getString("sessionId"));
/*     */   }
/*     */ 
/*     */   public NotificationMessage(String s)
/*     */     throws ParseException
/*     */   {
/*  95 */     super(s);
/*  96 */     setType("notification");
/*     */   }
/*     */ 
/*     */   public String getLevel()
/*     */   {
/* 105 */     return getString("level");
/*     */   }
/*     */ 
/*     */   public void setLevel(String priority)
/*     */   {
/* 114 */     put("level", priority);
/*     */   }
/*     */ 
/*     */   public void setNotificationCode(NotificationMessageCode notificationCode)
/*     */   {
/* 121 */     if (notificationCode != null)
/* 122 */       put("notifCode", notificationCode.asString());
/*     */   }
/*     */ 
/*     */   public void setNotificationCode(String notificationCode)
/*     */   {
/* 130 */     if (notificationCode != null)
/* 131 */       put("notifCode", notificationCode);
/*     */   }
/*     */ 
/*     */   public NotificationMessageCode getNotificationCode()
/*     */   {
/* 136 */     String codeString = getString("notifCode");
/* 137 */     if (codeString != null) {
/* 138 */       return NotificationMessageCode.fromString(codeString);
/*     */     }
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   public String getPositionId()
/*     */   {
/* 150 */     return getString("positionId");
/*     */   }
/*     */ 
/*     */   public void setPositionId(String positionId)
/*     */   {
/* 159 */     put("positionId", positionId);
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 168 */     return getString("text");
/*     */   }
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 177 */     put("text", text);
/*     */   }
/*     */ 
/*     */   public String getTTL()
/*     */   {
/* 186 */     return getString("TTL");
/*     */   }
/*     */ 
/*     */   public void setTTL(String ttl)
/*     */   {
/* 195 */     put("TTL", ttl);
/*     */   }
/*     */ 
/*     */   public String getOrderId()
/*     */   {
/* 205 */     return getString("orderId");
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId)
/*     */   {
/* 214 */     put("orderId", orderId);
/*     */   }
/*     */ 
/*     */   public OrderState getOrderState()
/*     */   {
/* 227 */     String orderState = getString("state");
/* 228 */     if (orderState != null) {
/* 229 */       return OrderState.fromString(orderState);
/*     */     }
/* 231 */     return null;
/*     */   }
/*     */ 
/*     */   public void setOrderState(OrderState orderState)
/*     */   {
/* 246 */     if (orderState != null)
/* 247 */       put("state", orderState.asString());
/*     */   }
/*     */ 
/*     */   public String getExternalSysId()
/*     */   {
/* 257 */     return getString("extSysId");
/*     */   }
/*     */ 
/*     */   public void setExternalSysId(String extId)
/*     */   {
/* 266 */     put("extSysId", extId);
/*     */   }
/*     */ 
/*     */   public void setRequestId(Integer reqId) {
/* 270 */     put("requestId", reqId);
/*     */   }
/*     */ 
/*     */   public Integer getRequestId() {
/* 274 */     return getInteger("requestId");
/*     */   }
/*     */ 
/*     */   public void setSessionId(String sessionId) {
/* 278 */     put("sessionId", sessionId);
/*     */   }
/*     */ 
/*     */   public String getSessionId() {
/* 282 */     return getString("sessionId");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.NotificationMessage
 * JD-Core Version:    0.6.0
 */