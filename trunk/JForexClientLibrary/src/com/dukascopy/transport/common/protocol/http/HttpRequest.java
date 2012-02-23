/*     */ package com.dukascopy.transport.common.protocol.http;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class HttpRequest
/*     */ {
/*     */   protected URL url;
/*     */   protected String version;
/*  21 */   protected Map<String, String> getParams = new HashMap();
/*     */ 
/*  24 */   protected Map<String, String> postParams = new HashMap();
/*     */ 
/*  27 */   protected Map<String, String> headerFields = new HashMap();
/*     */ 
/*  30 */   protected StringBuffer content = new StringBuffer();
/*     */   protected HttpRequestMethod method;
/*     */ 
/*     */   public void update()
/*     */   {
/*  48 */     if ((getUrl() != null) && (getUrl().getQuery() != null)) {
/*  49 */       parseParams(getUrl().getQuery(), this.getParams);
/*     */     }
/*     */ 
/*  52 */     if (getContent() != null)
/*  53 */       parseParams(getContent().toString(), this.postParams);
/*     */   }
/*     */ 
/*     */   public void setMethod(HttpRequestMethod method)
/*     */   {
/*  58 */     this.method = method;
/*     */   }
/*     */ 
/*     */   public HttpRequestMethod getMethod() {
/*  62 */     return this.method;
/*     */   }
/*     */ 
/*     */   private void parseParams(String s, Map<String, String> params)
/*     */   {
/*  75 */     StringTokenizer pairsSt = new StringTokenizer(s, "&");
/*  76 */     while (pairsSt.hasMoreTokens()) {
/*  77 */       String pairToken = pairsSt.nextToken();
/*     */ 
/*  79 */       StringTokenizer pairSt = new StringTokenizer(pairToken, "=");
/*  80 */       int i = 0;
/*  81 */       String key = null;
/*  82 */       String value = "";
/*  83 */       while (pairSt.hasMoreTokens()) {
/*  84 */         String token = pairSt.nextToken();
/*  85 */         if (i == 0)
/*  86 */           key = token;
/*  87 */         else if (i == 1) {
/*  88 */           value = unescapeParamValue(token);
/*     */         }
/*  90 */         i++;
/*     */       }
/*  92 */       if (key != null)
/*  93 */         params.put(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String unescapeParamValue(String s)
/*     */   {
/* 107 */     int prevI = 0;
/* 108 */     int i = -1;
/* 109 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 111 */     while ((i = s.indexOf(37, prevI)) != -1) {
/*     */       try {
/* 113 */         int value = Integer.parseInt(s.substring(i + 1, i + 3), 16);
/* 114 */         sb.append(s.substring(prevI, i)).append((char)value);
/*     */       } catch (NumberFormatException nfe) {
/* 116 */         sb.append(s.substring(prevI, i + 3));
/*     */       }
/* 118 */       prevI = i + 3;
/*     */     }
/* 120 */     sb.append(s.substring(prevI));
/* 121 */     return sb.toString().replace('+', ' ');
/*     */   }
/*     */ 
/*     */   public void setGetParam(String key, String value)
/*     */   {
/* 133 */     this.getParams.put(key, value);
/*     */   }
/*     */ 
/*     */   public String getGetParam(String key)
/*     */   {
/* 144 */     return (String)this.getParams.get(key);
/*     */   }
/*     */ 
/*     */   public void setPostParam(String key, String value)
/*     */   {
/* 156 */     this.postParams.put(key, value);
/*     */   }
/*     */ 
/*     */   public String getPostParam(String key)
/*     */   {
/* 167 */     return (String)this.postParams.get(key);
/*     */   }
/*     */ 
/*     */   public Map getParams()
/*     */   {
/* 176 */     Map allParams = new HashMap();
/* 177 */     allParams.putAll(this.postParams);
/* 178 */     allParams.putAll(this.getParams);
/* 179 */     return allParams;
/*     */   }
/*     */ 
/*     */   public String getParam(String key)
/*     */   {
/* 190 */     String param = getGetParam(key);
/* 191 */     if (param == null) {
/* 192 */       param = getPostParam(key);
/*     */     }
/* 194 */     return param;
/*     */   }
/*     */ 
/*     */   public void setHeaderField(String key, String value)
/*     */   {
/* 206 */     this.headerFields.put(key, value);
/*     */   }
/*     */ 
/*     */   public String getHeaderField(String key)
/*     */   {
/* 217 */     return (String)this.headerFields.get(key);
/*     */   }
/*     */ 
/*     */   public Long getContentLength()
/*     */   {
/* 226 */     Long result = null;
/* 227 */     String s = getHeaderField("Content-Length");
/* 228 */     if (s != null)
/*     */       try {
/* 230 */         result = Long.valueOf(Long.parseLong(s));
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*     */       }
/* 235 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isConnectionKeepAlive()
/*     */   {
/* 244 */     String connection = getHeaderField("Connection");
/*     */ 
/* 246 */     return (connection == null) || (!connection.equalsIgnoreCase("close"));
/*     */   }
/*     */ 
/*     */   public void setUrl(URL url)
/*     */   {
/* 252 */     this.url = url;
/*     */   }
/*     */ 
/*     */   public URL getUrl() {
/* 256 */     return this.url;
/*     */   }
/*     */ 
/*     */   public void setVersion(String version) {
/* 260 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public String getVersion() {
/* 264 */     return this.version;
/*     */   }
/*     */ 
/*     */   public void setGetParams(Map<String, String> getParams) {
/* 268 */     this.getParams = getParams;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getGetParams() {
/* 272 */     return this.getParams;
/*     */   }
/*     */ 
/*     */   public void setPostParams(Map<String, String> getParams) {
/* 276 */     this.getParams = getParams;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getPostParams() {
/* 280 */     return this.postParams;
/*     */   }
/*     */ 
/*     */   public void setHeaderFields(Map<String, String> headerFields) {
/* 284 */     this.headerFields = headerFields;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getHeaderFields() {
/* 288 */     return this.headerFields;
/*     */   }
/*     */ 
/*     */   public void setContent(StringBuffer content) {
/* 292 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public StringBuffer getContent() {
/* 296 */     return this.content;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 300 */     System.out.println(unescapeParamValue("a+b+%0D%0A"));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.http.HttpRequest
 * JD-Core Version:    0.6.0
 */