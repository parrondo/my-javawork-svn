/*     */ package com.dukascopy.dds2.events;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LoginEvent extends Event
/*     */ {
/*     */   public static final String IP = "ip";
/*     */   public static final String HOST = "host";
/*     */   public static final String LOGON_IP = "logon_ip";
/*     */   public static final String OPERATING_SYSTEM = "os";
/*     */   public static final String HTTP_AGENT = "http_agent";
/*     */   public static final String CLIENT_TYPE = "client_type";
/*     */   private String ip;
/*     */   private String host;
/*     */   private String logonIp;
/*     */   private String operatingSystem;
/*     */   private String httpAgent;
/*     */   private String clientType;
/*     */ 
/*     */   protected LoginEvent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LoginEvent(String ip, String host, String logonHost, String operatingSystem, String httpAgent, String clientType)
/*     */   {
/*  61 */     this.ip = ip;
/*  62 */     this.host = host;
/*  63 */     this.logonIp = logonHost;
/*  64 */     this.operatingSystem = operatingSystem;
/*  65 */     this.httpAgent = httpAgent;
/*  66 */     this.clientType = clientType;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getAttributes()
/*     */   {
/*  71 */     Map map = new HashMap();
/*  72 */     map.put("ip", this.ip);
/*  73 */     map.put("host", this.host);
/*  74 */     map.put("logon_ip", this.logonIp);
/*  75 */     map.put("os", this.operatingSystem);
/*  76 */     map.put("http_agent", this.httpAgent);
/*  77 */     map.put("client_type", this.clientType);
/*  78 */     return map;
/*     */   }
/*     */ 
/*     */   public String getClientType() {
/*  82 */     return this.clientType;
/*     */   }
/*     */ 
/*     */   public void setClientType(String clientType) {
/*  86 */     this.clientType = clientType;
/*     */   }
/*     */ 
/*     */   public String getHost() {
/*  90 */     return this.host;
/*     */   }
/*     */ 
/*     */   public void setHost(String host) {
/*  94 */     this.host = host;
/*     */   }
/*     */ 
/*     */   public String getHttpAgent() {
/*  98 */     return this.httpAgent;
/*     */   }
/*     */ 
/*     */   public void setHttpAgent(String httpAgent) {
/* 102 */     this.httpAgent = httpAgent;
/*     */   }
/*     */ 
/*     */   public String getIp() {
/* 106 */     return this.ip;
/*     */   }
/*     */ 
/*     */   public void setIp(String ip) {
/* 110 */     this.ip = ip;
/*     */   }
/*     */ 
/*     */   public String getLogonIp() {
/* 114 */     return this.logonIp;
/*     */   }
/*     */ 
/*     */   public void setLogonIp(String logonIp) {
/* 118 */     this.logonIp = logonIp;
/*     */   }
/*     */ 
/*     */   public String getOperatingSystem() {
/* 122 */     return this.operatingSystem;
/*     */   }
/*     */ 
/*     */   public void setOperatingSystem(String operatingSystem) {
/* 126 */     this.operatingSystem = operatingSystem;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.LoginEvent
 * JD-Core Version:    0.6.0
 */