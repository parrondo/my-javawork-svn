/*    */ package com.dukascopy.dds2.greed.agent.protocol;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ 
/*    */ public class JFXURLConnection extends URLConnection
/*    */ {
/* 18 */   private byte[] data = null;
/*    */ 
/*    */   protected JFXURLConnection(URL url, byte[] data) {
/* 21 */     super(url);
/* 22 */     this.data = data;
/*    */   }
/*    */ 
/*    */   public void connect()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public String getHeaderField(String name)
/*    */   {
/* 35 */     return super.getHeaderField(name);
/*    */   }
/*    */ 
/*    */   public String getHeaderField(int n)
/*    */   {
/* 41 */     return super.getHeaderField(n);
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream() throws IOException
/*    */   {
/* 46 */     return new ByteArrayInputStream(this.data);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.protocol.JFXURLConnection
 * JD-Core Version:    0.6.0
 */