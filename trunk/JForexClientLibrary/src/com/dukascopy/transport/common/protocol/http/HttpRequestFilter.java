/*    */ package com.dukascopy.transport.common.protocol.http;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.util.Date;
/*    */ import org.apache.mina.common.ByteBuffer;
/*    */ import org.apache.mina.common.IoFilter.NextFilter;
/*    */ import org.apache.mina.common.IoFilterAdapter;
/*    */ import org.apache.mina.common.IoSession;
/*    */ import org.apache.mina.common.WriteFuture;
/*    */ 
/*    */ public class HttpRequestFilter extends IoFilterAdapter
/*    */ {
/*    */   private HttpRequestHandler requestHandler;
/*    */   public static final String HTTP_VERSION = "HTTP/1.1";
/*    */ 
/*    */   public HttpRequestFilter(HttpRequestHandler requestHandler)
/*    */   {
/* 19 */     this.requestHandler = requestHandler;
/*    */   }
/*    */ 
/*    */   public void messageReceived(IoFilter.NextFilter arg0, IoSession arg1, Object arg2)
/*    */     throws Exception
/*    */   {
/* 30 */     ByteBuffer buffer = (ByteBuffer)arg2;
/* 31 */     DataInputStream dis = new DataInputStream(buffer.asInputStream());
/* 32 */     int available = dis.available();
/* 33 */     byte[] readBuf = new byte[available];
/* 34 */     while (available > 0) {
/* 35 */       int length = dis.read(readBuf, readBuf.length - available, available);
/* 36 */       if (length == -1) {
/*    */         break;
/*    */       }
/* 39 */       available -= length;
/*    */     }
/* 41 */     dis.close();
/* 42 */     if (arg1.getAttribute("MODE") == null) {
/* 43 */       if (isHttp(readBuf))
/* 44 */         arg1.setAttribute("MODE", "HTTP");
/*    */       else {
/* 46 */         arg1.setAttribute("MODE", "SOCKET");
/*    */       }
/*    */     }
/* 49 */     if (arg1.getAttribute("MODE").equals("HTTP")) {
/* 50 */       HttpRequestBuilder requestBuilder = (HttpRequestBuilder)arg1.getAttribute("httpBuilder");
/* 51 */       if (requestBuilder == null) {
/* 52 */         requestBuilder = new HttpRequestBuilder();
/* 53 */         arg1.setAttribute("httpBuilder", requestBuilder);
/*    */       }
/* 55 */       HttpRequest request = requestBuilder.getRequest(readBuf);
/* 56 */       if (request != null) {
/* 57 */         StringBuffer responseContent = this.requestHandler.handleRequest(request);
/* 58 */         HttpResponse response = new HttpResponse("HTTP/1.1");
/* 59 */         response.setHeaderField("Connection", "close");
/* 60 */         response.setHeaderField("Expires", "Mon, 26 Jul 1990 05:00:00 GMT");
/* 61 */         response.setHeaderField("Last-Modified", new Date().toString());
/* 62 */         response.setHeaderField("Cache-Control", "no-cache, must-revalidate, no-store, max-age=0");
/* 63 */         response.setHeaderField("Pragma", "no-cache");
/* 64 */         response.setHeaderField("Content-Type", "text/html");
/* 65 */         response.setStatusCode(HttpStatusCode.SUCCESS_OK);
/* 66 */         response.setHeaderField("Content-Type", "text/html");
/* 67 */         response.setHeaderField("Connection", "Keep-Alive");
/* 68 */         response.setContent(responseContent);
/* 69 */         response.update();
/* 70 */         WriteFuture wf = arg1.write(response);
/* 71 */         wf.join(2000L);
/* 72 */         requestBuilder.reset();
/*    */       }
/* 74 */       return;
/*    */     }
/*    */ 
/* 77 */     ByteBuffer b = ByteBuffer.wrap(readBuf);
/* 78 */     arg0.messageReceived(arg1, b);
/*    */   }
/*    */ 
/*    */   private boolean isHttp(byte[] buffer) {
/*    */     try {
/* 83 */       String str = new String(buffer, "UTF-8");
/* 84 */       if ((str.startsWith("GET")) || (str.startsWith("POST")) || (str.startsWith("get")) || (str.startsWith("post")))
/* 85 */         return true;
/*    */     }
/*    */     catch (Exception e) {
/* 88 */       e.printStackTrace();
/*    */     }
/* 90 */     return false;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.http.HttpRequestFilter
 * JD-Core Version:    0.6.0
 */