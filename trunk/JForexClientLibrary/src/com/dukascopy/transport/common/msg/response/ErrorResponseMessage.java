/*     */ package com.dukascopy.transport.common.msg.response;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.ResponseMessage;
/*     */ 
/*     */ public class ErrorResponseMessage extends ResponseMessage
/*     */ {
/*     */   public static final String TYPE = "error";
/*     */   public static final String REQUEST_ID = "requestId";
/*     */   public static final String SESSION_ID = "sessionId";
/*     */ 
/*     */   public ErrorResponseMessage()
/*     */   {
/*  25 */     setType("error");
/*     */   }
/*     */ 
/*     */   public ErrorResponseMessage(ProtocolMessage message)
/*     */   {
/*  35 */     super(message);
/*     */ 
/*  37 */     setType("error");
/*     */ 
/*  39 */     setReason(message.getString("reason"));
/*  40 */     setFatal(message.getBool("fatal"));
/*  41 */     setRequestId(message.getInteger("requestId"));
/*  42 */     setSessionId(message.getString("sessionId"));
/*     */   }
/*     */ 
/*     */   public ErrorResponseMessage(String reason)
/*     */   {
/*  54 */     setType("error");
/*     */ 
/*  56 */     setReason(reason);
/*     */   }
/*     */ 
/*     */   public ErrorResponseMessage(String reason, boolean fatal)
/*     */   {
/*  70 */     setType("error");
/*     */ 
/*  72 */     setReason(reason);
/*  73 */     setFatal(Boolean.valueOf(fatal));
/*     */   }
/*     */ 
/*     */   public ErrorResponseMessage(ProtocolMessage message, String reason)
/*     */   {
/*  85 */     super(message);
/*     */ 
/*  87 */     setType("error");
/*     */ 
/*  89 */     setReason(reason);
/*     */   }
/*     */ 
/*     */   public ErrorResponseMessage(ProtocolMessage message, String reason, boolean fatal)
/*     */   {
/* 103 */     super(message);
/*     */ 
/* 105 */     setType("error");
/*     */ 
/* 107 */     setReason(reason);
/* 108 */     setFatal(Boolean.valueOf(fatal));
/*     */   }
/*     */ 
/*     */   public ErrorResponseMessage(ProtocolMessage message, String reason, boolean fatal, Integer requestId) {
/* 112 */     super(message);
/*     */ 
/* 114 */     setType("error");
/*     */ 
/* 116 */     setReason(reason);
/* 117 */     setFatal(Boolean.valueOf(fatal));
/* 118 */     setRequestId(requestId);
/*     */   }
/*     */ 
/*     */   public void setReason(String reason) {
/* 122 */     put("reason", reason);
/*     */   }
/*     */ 
/*     */   public String getReason() {
/* 126 */     return getString("reason");
/*     */   }
/*     */ 
/*     */   public void setFatal(Boolean fatal) {
/* 130 */     put("fatal", fatal);
/*     */   }
/*     */ 
/*     */   public Boolean getFatal() {
/* 134 */     return getBool("fatal");
/*     */   }
/*     */ 
/*     */   public void setRequestId(Integer reqId) {
/* 138 */     put("requestId", reqId);
/*     */   }
/*     */ 
/*     */   public Integer getRequestId() {
/* 142 */     return getInteger("requestId");
/*     */   }
/*     */ 
/*     */   public void setSessionId(String sessionId) {
/* 146 */     put("sessionId", sessionId);
/*     */   }
/*     */ 
/*     */   public String getSessionId() {
/* 150 */     return getString("sessionId");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.response.ErrorResponseMessage
 * JD-Core Version:    0.6.0
 */