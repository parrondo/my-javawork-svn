/*     */ package com.dukascopy.transport.common.protocol.http;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class HttpResponse
/*     */ {
/*     */   private String version;
/*     */   private HttpStatusCode statusCode;
/*  21 */   private Map<String, String> headerFields = new HashMap();
/*     */ 
/*  24 */   private StringBuffer content = new StringBuffer();
/*  25 */   private ByteArrayOutputStream output = new ByteArrayOutputStream();
/*     */ 
/*     */   public HttpResponse(String version)
/*     */   {
/*  31 */     setVersion(version);
/*     */   }
/*     */ 
/*     */   public void print(String line)
/*     */   {
/*  40 */     getContent().append(line);
/*     */   }
/*     */ 
/*     */   public void println(String line)
/*     */   {
/*  49 */     getContent().append(line).append("\n");
/*     */   }
/*     */ 
/*     */   public void update()
/*     */   {
/*  56 */     int contentLength = 0;
/*  57 */     if (this.content.length() > 0)
/*  58 */       contentLength = this.content.length();
/*  59 */     else if (this.output.size() > 0) {
/*  60 */       contentLength = this.output.size();
/*     */     }
/*  62 */     if (contentLength > 0)
/*  63 */       setHeaderField("Content-Length", String.valueOf(contentLength));
/*     */   }
/*     */ 
/*     */   public void setHeaderField(String key, String value)
/*     */   {
/*  74 */     this.headerFields.put(key, value);
/*     */   }
/*     */ 
/*     */   public String getHeaderField(String key)
/*     */   {
/*  84 */     return (String)this.headerFields.get(key);
/*     */   }
/*     */ 
/*     */   public boolean isConnectionKeepAlive()
/*     */   {
/*  93 */     String connection = getHeaderField("Connection");
/*     */ 
/*  95 */     return (connection == null) || (!connection.equalsIgnoreCase("close"));
/*     */   }
/*     */ 
/*     */   public void writeData(byte[] data)
/*     */     throws IOException
/*     */   {
/* 106 */     this.output.write(data);
/*     */   }
/*     */ 
/*     */   public byte[] getData()
/*     */     throws IOException
/*     */   {
/* 116 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 117 */     if ((this.content.length() > 0) || (getHeaderFields().size() > 0)) {
/* 118 */       out.write(toString().getBytes());
/*     */     }
/* 120 */     if (this.output.size() > 0) {
/* 121 */       out.write(this.output.toByteArray());
/*     */     }
/* 123 */     return out.toByteArray();
/*     */   }
/*     */ 
/*     */   public void setVersion(String version) {
/* 127 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public String getVersion() {
/* 131 */     return this.version;
/*     */   }
/*     */ 
/*     */   public void setStatusCode(HttpStatusCode statusCode) {
/* 135 */     this.statusCode = statusCode;
/*     */   }
/*     */ 
/*     */   public HttpStatusCode getStatusCode() {
/* 139 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   public void setHeaderFields(Map<String, String> headers) {
/* 143 */     this.headerFields = headers;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getHeaderFields() {
/* 147 */     return this.headerFields;
/*     */   }
/*     */ 
/*     */   public void setContent(StringBuffer content) {
/* 151 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public StringBuffer getContent() {
/* 155 */     return this.content;
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream getOutput() {
/* 159 */     return this.output;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 163 */     StringBuffer sb = new StringBuffer(this.version);
/* 164 */     sb.append(" ").append(this.statusCode.getStatusCode()).append(" ").append(this.statusCode.getMessage()).append("\r\n");
/* 165 */     for (String key : getHeaderFields().keySet()) {
/* 166 */       String value = (String)getHeaderFields().get(key);
/* 167 */       sb.append(key).append(": ").append(value).append("\r\n");
/*     */     }
/* 169 */     sb.append("\r\n");
/* 170 */     if (sb.length() > 0) {
/* 171 */       sb.append(this.content);
/*     */     }
/* 173 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.http.HttpResponse
 * JD-Core Version:    0.6.0
 */