/*     */ package com.dukascopy.transport.common.cluster;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class ClusterServerInfo
/*     */   implements Serializable
/*     */ {
/*     */   private static final long maxTimeoutTic = 3L;
/*     */   public String baseUrl;
/*     */   public String externalUrl;
/*     */   public String clusterServerName;
/*     */   public long serverQuotient;
/*     */   public int sessionCount;
/*     */   public int taskPoolSize;
/*  26 */   public long lastActivityTime = 3L;
/*     */ 
/*     */   public void setClusterServerName(String clusterServerName)
/*     */   {
/*  38 */     this.clusterServerName = clusterServerName;
/*     */   }
/*     */ 
/*     */   public String getClusterServerName() {
/*  42 */     return this.clusterServerName;
/*     */   }
/*     */ 
/*     */   public void setServerQuotient(long serverQuotient) {
/*  46 */     this.serverQuotient = serverQuotient;
/*     */   }
/*     */ 
/*     */   public long getServerQuotient() {
/*  50 */     return this.serverQuotient;
/*     */   }
/*     */ 
/*     */   public void setBaseUrl(String baseUrl) {
/*  54 */     this.baseUrl = baseUrl;
/*     */   }
/*     */ 
/*     */   public String getBaseUrl() {
/*  58 */     return this.baseUrl;
/*     */   }
/*     */ 
/*     */   public void setSessionCount(int sessionCount) {
/*  62 */     this.sessionCount = sessionCount;
/*     */   }
/*     */ 
/*     */   public int getSessionCount() {
/*  66 */     return this.sessionCount;
/*     */   }
/*     */ 
/*     */   public void setTaskPoolSize(int taskPoolSize) {
/*  70 */     this.taskPoolSize = taskPoolSize;
/*     */   }
/*     */ 
/*     */   public int getTaskPoolSize() {
/*  74 */     return this.taskPoolSize;
/*     */   }
/*     */ 
/*     */   public void setLastActivityTime() {
/*  78 */     this.lastActivityTime = 3L;
/*     */   }
/*     */ 
/*     */   public long getLastActivityTime() {
/*  82 */     return this.lastActivityTime;
/*     */   }
/*     */ 
/*     */   public boolean isTimeout()
/*     */   {
/*  87 */     return this.lastActivityTime <= 0L;
/*     */   }
/*     */ 
/*     */   public String getExternalUrl()
/*     */   {
/*  94 */     return this.externalUrl;
/*     */   }
/*     */ 
/*     */   public void setExternalUrl(String externalUrl) {
/*  98 */     this.externalUrl = externalUrl;
/*     */   }
/*     */ 
/*     */   public void ping() {
/* 102 */     this.lastActivityTime -= 1L;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 108 */     StringBuffer sb = new StringBuffer("ClusterServerInfo{");
/* 109 */     sb.append("clusterServerName=").append(this.clusterServerName);
/* 110 */     sb.append(",baseUrl=").append(this.baseUrl);
/* 111 */     sb.append(",serverQuotient=").append(this.serverQuotient);
/* 112 */     sb.append(",externalUrl=").append(this.externalUrl);
/* 113 */     sb.append(",sessionCount=").append(this.sessionCount);
/* 114 */     sb.append(",totalTaskPoolSize=").append(this.taskPoolSize);
/* 115 */     sb.append(",lastActivityTime=").append(this.lastActivityTime);
/* 116 */     sb.append("}");
/* 117 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.cluster.ClusterServerInfo
 * JD-Core Version:    0.6.0
 */