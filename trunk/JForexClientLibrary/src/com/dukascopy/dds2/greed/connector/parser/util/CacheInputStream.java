/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class CacheInputStream extends InputStream
/*    */ {
/*    */   private InputStream fInputStream;
/*    */   private StringBuilder fStringBuffer;
/*    */ 
/*    */   public CacheInputStream(InputStream is)
/*    */   {
/* 13 */     this.fInputStream = is;
/*    */ 
/* 15 */     this.fStringBuffer = new StringBuilder();
/*    */   }
/*    */ 
/*    */   public int read() throws IOException {
/* 19 */     int ch = this.fInputStream.read();
/* 20 */     this.fStringBuffer.append((char)ch);
/* 21 */     return ch;
/*    */   }
/*    */ 
/*    */   public String getRange(int from, int to) {
/* 25 */     if (from < 0) {
/* 26 */       from = 0;
/*    */     }
/* 28 */     if (to > this.fStringBuffer.length()) {
/* 29 */       to = this.fStringBuffer.length();
/*    */     }
/* 31 */     return this.fStringBuffer.substring(from, to);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.CacheInputStream
 * JD-Core Version:    0.6.0
 */