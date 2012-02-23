/*     */ package com.dukascopy.transport.common.protocol.http;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class HttpRequestBuilder
/*     */ {
/*   9 */   private HttpRequest request = new HttpRequest();
/*     */ 
/*  11 */   private StringBuffer inputBuffer = new StringBuffer();
/*     */   protected static final int STATE_NONE = 0;
/*     */   protected static final int STATE_READREQUEST = 1;
/*     */   protected static final int STATE_READHEADERS = 2;
/*     */   protected static final int STATE_READBODY = 3;
/*     */   protected static final int STATE_EXECUTE = 4;
/*  23 */   protected int state = 1;
/*     */ 
/*     */   public HttpRequest getRequest(byte[] data) {
/*  26 */     applyProtocol(data);
/*  27 */     if (this.state == 3) {
/*  28 */       Long contentLength = this.request.getContentLength();
/*  29 */       if ((contentLength == null) || (this.request.getContent().length() >= contentLength.longValue())) {
/*  30 */         setState(4);
/*     */       }
/*     */     }
/*  33 */     if (4 == this.state) {
/*  34 */       this.request.update();
/*  35 */       return this.request;
/*     */     }
/*  37 */     return null;
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  41 */     this.request = new HttpRequest();
/*  42 */     this.inputBuffer = new StringBuffer();
/*  43 */     this.state = 1;
/*     */   }
/*     */ 
/*     */   public void applyProtocol(byte[] data)
/*     */   {
/*  50 */     this.inputBuffer.append(new String(data));
/*     */ 
/*  52 */     String buffer = this.inputBuffer.toString();
/*  53 */     int prevI = 0;
/*  54 */     int i = -1;
/*  55 */     if ((this.state == 1) && 
/*  56 */       ((i = buffer.indexOf("\r\n")) != -1)) {
/*  57 */       httpMethodReceived(buffer.substring(0, i));
/*  58 */       this.inputBuffer = new StringBuffer(buffer.substring(i + 2));
/*  59 */       setState(2);
/*     */     }
/*     */ 
/*  62 */     if (this.state == 2) {
/*  63 */       while ((i = buffer.indexOf("\r\n", prevI)) != -1) {
/*  64 */         String line = buffer.substring(prevI, i);
/*     */ 
/*  66 */         if (line.length() > 0)
/*  67 */           httpHeaderReceived(line);
/*     */         else {
/*  69 */           setState(3);
/*     */         }
/*  71 */         prevI = i + 2;
/*     */       }
/*  73 */       if (prevI > 0) {
/*  74 */         this.inputBuffer = new StringBuffer(buffer.substring(prevI));
/*     */       }
/*     */     }
/*  77 */     if (this.state == 3) {
/*  78 */       httpBodyPartReceived(this.inputBuffer.toString());
/*  79 */       this.inputBuffer = new StringBuffer();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setState(int state)
/*     */   {
/*     */     String value;
/*  91 */     switch (this.state) {
/*     */     case 0:
/*  93 */       break;
/*     */     case 1:
/*  95 */       break;
/*     */     case 2:
/*  97 */       for (String key : this.request.getHeaderFields().keySet()) {
/*  98 */         value = this.request.getHeaderField(key);
/*     */       }
/* 100 */       break;
/*     */     case 3:
/* 102 */       break;
/*     */     case 4:
/*     */     }
/*     */ 
/* 106 */     this.state = state;
/*     */   }
/*     */ 
/*     */   protected void httpMethodReceived(String line)
/*     */   {
/* 117 */     StringTokenizer st = new StringTokenizer(line, " ");
/* 118 */     int i = 0;
/* 119 */     while (st.hasMoreTokens()) {
/* 120 */       String token = st.nextToken();
/* 121 */       switch (i) {
/*     */       case 0:
/* 123 */         this.request.setMethod(HttpRequestMethod.parse(token));
/* 124 */         break;
/*     */       case 1:
/*     */         try {
/* 127 */           this.request.setUrl(new URL("http://" + token));
/*     */         }
/*     */         catch (MalformedURLException mue)
/*     */         {
/*     */         }
/*     */       case 2:
/* 133 */         this.request.setVersion(token);
/*     */       }
/*     */ 
/* 136 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void httpHeaderReceived(String line)
/*     */   {
/* 147 */     String key = null;
/* 148 */     String value = null;
/* 149 */     StringTokenizer st = new StringTokenizer(line, ":");
/* 150 */     if (st.hasMoreTokens()) {
/* 151 */       key = st.nextToken();
/* 152 */       if (st.hasMoreTokens()) {
/* 153 */         value = st.nextToken();
/*     */       }
/*     */     }
/* 156 */     if ((key != null) && (value != null))
/* 157 */       this.request.setHeaderField(key.trim(), value.trim());
/*     */   }
/*     */ 
/*     */   protected void httpBodyPartReceived(String bodyPart)
/*     */   {
/* 168 */     this.request.getContent().append(bodyPart);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.http.HttpRequestBuilder
 * JD-Core Version:    0.6.0
 */