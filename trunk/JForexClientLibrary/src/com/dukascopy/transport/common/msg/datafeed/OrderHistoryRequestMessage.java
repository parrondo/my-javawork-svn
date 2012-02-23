/*     */ package com.dukascopy.transport.common.msg.datafeed;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ 
/*     */ public class OrderHistoryRequestMessage extends AbstractDFSMessage
/*     */ {
/*     */   public static final String TYPE = "order_history_request";
/*     */   public static final String START_TIME = "startTime";
/*     */   public static final String END_TIME = "endTime";
/*     */   public static final String GET_ROLLOVERED_ORDERS = "getRollovered";
/*     */   public static final String CURRENCY_PRIMARY = "currPrimary";
/*     */   public static final String CURRENCY_SECONDARY = "currSecondary";
/*     */   public static final String GET_MERGED_POSTITIONS = "getMergedPoss";
/*     */   public static final String GET_REJECTED = "getRejected";
/*     */   public static final String COMPRESS_RESULT = "useCompression";
/*     */ 
/*     */   public OrderHistoryRequestMessage()
/*     */   {
/*  36 */     setType("order_history_request");
/*     */   }
/*     */ 
/*     */   public OrderHistoryRequestMessage(ProtocolMessage message) {
/*  40 */     super(message);
/*  41 */     setType("order_history_request");
/*  42 */     put("startTime", message.getLong("startTime"));
/*  43 */     put("endTime", message.getLong("endTime"));
/*  44 */     put("getRollovered", message.getString("getRollovered"));
/*  45 */     put("getMergedPoss", message.getString("getMergedPoss"));
/*  46 */     put("currPrimary", message.getString("currPrimary"));
/*  47 */     put("currSecondary", message.getString("currSecondary"));
/*  48 */     put("getRejected", message.getString("getRejected"));
/*  49 */     put("useCompression", message.getString("useCompression"));
/*     */   }
/*     */ 
/*     */   public void setStartTime(Long startTime) {
/*  53 */     put("startTime", startTime);
/*     */   }
/*     */ 
/*     */   public Long getStartTime() {
/*  57 */     return getLong("startTime");
/*     */   }
/*     */ 
/*     */   public void setEndTime(Long endTime) {
/*  61 */     put("endTime", endTime);
/*     */   }
/*     */ 
/*     */   public Long getEndTime() {
/*  65 */     return getLong("endTime");
/*     */   }
/*     */ 
/*     */   public void setGetRolloveredOrders(boolean getRolloveredOrders) {
/*  69 */     put("getRollovered", getRolloveredOrders);
/*     */   }
/*     */ 
/*     */   public boolean isGetRolloveredOrders() {
/*  73 */     return getBoolean("getRollovered");
/*     */   }
/*     */ 
/*     */   public void setGetMergedPoss(boolean getMergedPoss) {
/*  77 */     put("getMergedPoss", getMergedPoss);
/*     */   }
/*     */ 
/*     */   public boolean isGetMergedPoss() {
/*  81 */     return getBoolean("getMergedPoss");
/*     */   }
/*     */ 
/*     */   public String getCurrencyPrimary() {
/*  85 */     return getString("currPrimary");
/*     */   }
/*     */ 
/*     */   public void setCurrencyPrimary(String currPrimary) {
/*  89 */     put("currPrimary", currPrimary);
/*     */   }
/*     */ 
/*     */   public String getCurrencySecondary() {
/*  93 */     return getString("currSecondary");
/*     */   }
/*     */ 
/*     */   public void setCurrencySecondary(String currSecondary) {
/*  97 */     put("currSecondary", currSecondary);
/*     */   }
/*     */ 
/*     */   public void setGetRejectedOrders(boolean getRejectedOrders) {
/* 101 */     put("getRejected", getRejectedOrders);
/*     */   }
/*     */ 
/*     */   public boolean isGetRejectedOrders() {
/* 105 */     return getBoolean("getRejected");
/*     */   }
/*     */ 
/*     */   public void setUseCompression(boolean useCompression) {
/* 109 */     put("useCompression", useCompression);
/*     */   }
/*     */ 
/*     */   public boolean isUseCompression() {
/* 113 */     return getBoolean("useCompression");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.datafeed.OrderHistoryRequestMessage
 * JD-Core Version:    0.6.0
 */