/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ClientEvent extends Event
/*     */ {
/*     */   public static final String EVENT_ID = "event_id";
/*     */   public static final String SESSION_ID = "session_id";
/*     */   public static final String USER_ID = "user_id";
/*     */   public static final String CLIENT_TIME = "client_time";
/*     */   public static final String IS_INBOUND = "inbound";
/*     */   public static final boolean INBOUND = true;
/*     */   public static final boolean OUTBOUND = false;
/*     */   private int userId;
/*     */   private Date clientTime;
/*     */   private String sessionId;
/*  63 */   private boolean inbound = false;
/*     */ 
/*     */   public ClientEvent(int userId, boolean inbound)
/*     */   {
/*  67 */     this.userId = userId;
/*  68 */     this.inbound = inbound;
/*     */   }
/*     */ 
/*     */   public ClientEvent(int userId)
/*     */   {
/*  73 */     this.userId = userId;
/*     */   }
/*     */ 
/*     */   public ClientEvent(int userId, String sessionId, boolean inbound)
/*     */   {
/*  78 */     this.userId = userId;
/*  79 */     this.sessionId = sessionId;
/*  80 */     this.inbound = inbound;
/*     */   }
/*     */ 
/*     */   protected ClientEvent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ClientEvent(int userId, Date clientTime, String sessionId)
/*     */   {
/*  98 */     this();
/*  99 */     setUserId(userId);
/* 100 */     setClientTime(clientTime);
/* 101 */     setSessionId(sessionId);
/*     */   }
/*     */ 
/*     */   public int getUserId()
/*     */   {
/* 108 */     return this.userId;
/*     */   }
/*     */ 
/*     */   public void setUserId(int userId)
/*     */   {
/* 116 */     this.userId = userId;
/*     */   }
/*     */ 
/*     */   public Date getClientTime()
/*     */   {
/* 123 */     return this.clientTime;
/*     */   }
/*     */ 
/*     */   public void setClientTime(Date clientTime)
/*     */   {
/* 131 */     this.clientTime = clientTime;
/*     */   }
/*     */ 
/*     */   public String getSessionId() {
/* 135 */     return this.sessionId;
/*     */   }
/*     */ 
/*     */   public void setSessionId(String sessionId) {
/* 139 */     this.sessionId = sessionId;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getAttributes()
/*     */   {
/* 144 */     Map attributes = new HashMap();
/* 145 */     attributes.put("event_id", Long.valueOf(super.getId()));
/* 146 */     attributes.put("user_id", Integer.valueOf(this.userId));
/* 147 */     attributes.put("client_time", this.clientTime);
/* 148 */     attributes.put("session_id", this.sessionId);
/* 149 */     return Collections.unmodifiableMap(attributes);
/*     */   }
/*     */ 
/*     */   public boolean isInbound()
/*     */   {
/* 158 */     return this.inbound;
/*     */   }
/*     */ 
/*     */   public void setInbound(boolean inbound) {
/* 162 */     this.inbound = inbound;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.ClientEvent
 * JD-Core Version:    0.6.0
 */