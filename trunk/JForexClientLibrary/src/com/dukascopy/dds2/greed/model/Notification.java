/*     */ package com.dukascopy.dds2.greed.model;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class Notification
/*     */ {
/*     */   public static final String INFO = "INFO";
/*     */   public static final String WARNING = "WARNING";
/*     */   public static final String ERROR = "ERROR";
/*     */   public static final String INFOCLIENT = "INFOCLIENT";
/*     */   public static final String NOTIFCLIENT = "NOTIFCLIENT";
/*     */   public static final String RED_ALERT = "ALERT";
/*     */   private Date timestamp;
/*     */   private String serverTimestamp;
/*     */   private String content;
/*  48 */   private String priority = "INFO";
/*  49 */   private String positionId = null;
/*     */ 
/*  51 */   private String fullStackTrace = null;
/*     */ 
/*  61 */   private int panelId = -1;
/*     */ 
/*     */   public String getFullStackTrace()
/*     */   {
/*  54 */     return this.fullStackTrace;
/*     */   }
/*     */ 
/*     */   public void setFullStackTrace(String fullStackTrace) {
/*  58 */     this.fullStackTrace = fullStackTrace;
/*     */   }
/*     */ 
/*     */   public int getPanelId()
/*     */   {
/*  64 */     return this.panelId;
/*     */   }
/*     */ 
/*     */   public void setPanelId(int panelId) {
/*  68 */     this.panelId = panelId;
/*     */   }
/*     */ 
/*     */   public Notification(Date timestamp, String content)
/*     */   {
/*  76 */     this.timestamp = timestamp;
/*  77 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public Notification(String content)
/*     */   {
/*  85 */     this.timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
/*  86 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public Date getTimestamp()
/*     */   {
/*  93 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public void setTimestamp(Date timestamp)
/*     */   {
/* 101 */     this.timestamp = timestamp;
/*     */   }
/*     */ 
/*     */   public String getContent()
/*     */   {
/* 109 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void setContent(String content)
/*     */   {
/* 117 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public String getPriority()
/*     */   {
/* 125 */     return this.priority;
/*     */   }
/*     */ 
/*     */   public void setPriority(String priority)
/*     */   {
/* 133 */     this.priority = priority;
/*     */   }
/*     */ 
/*     */   public String getPositionId()
/*     */   {
/* 141 */     return this.positionId;
/*     */   }
/*     */ 
/*     */   public void setPositionId(String positionId)
/*     */   {
/* 149 */     this.positionId = positionId;
/*     */   }
/*     */ 
/*     */   public String getServerTimestamp() {
/* 153 */     return this.serverTimestamp;
/*     */   }
/*     */ 
/*     */   public void setServerTimestamp(String serverTimestamp) {
/* 157 */     this.serverTimestamp = serverTimestamp;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 162 */     return this.content;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.model.Notification
 * JD-Core Version:    0.6.0
 */