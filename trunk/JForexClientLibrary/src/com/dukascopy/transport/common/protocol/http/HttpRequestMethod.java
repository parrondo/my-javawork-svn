/*    */ package com.dukascopy.transport.common.protocol.http;
/*    */ 
/*    */ public enum HttpRequestMethod
/*    */ {
/*  9 */   NONE(0, "None"), 
/* 10 */   GET(1, "GET"), 
/* 11 */   POST(2, "POST"), 
/* 12 */   HEAD(3, "HEAD");
/*    */ 
/*    */   private int method;
/*    */   private String name;
/*    */ 
/*    */   private HttpRequestMethod(int method, String name)
/*    */   {
/* 24 */     setMethod(method);
/* 25 */     setName(name);
/*    */   }
/*    */ 
/*    */   public static HttpRequestMethod parse(String name)
/*    */   {
/* 35 */     HttpRequestMethod result = null;
/*    */ 
/* 37 */     if (name.equals("GET"))
/* 38 */       result = GET;
/* 39 */     else if (name.equals("POST"))
/* 40 */       result = POST;
/* 41 */     else if (name.equals("HEAD")) {
/* 42 */       result = HEAD;
/*    */     }
/*    */ 
/* 45 */     return result;
/*    */   }
/*    */ 
/*    */   public void setMethod(int method) {
/* 49 */     this.method = method;
/*    */   }
/*    */ 
/*    */   public int getMethod() {
/* 53 */     return this.method;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 57 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 61 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 65 */     return this.name;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.http.HttpRequestMethod
 * JD-Core Version:    0.6.0
 */