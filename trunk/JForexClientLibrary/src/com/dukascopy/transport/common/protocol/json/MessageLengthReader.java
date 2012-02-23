/*    */ package com.dukascopy.transport.common.protocol.json;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class MessageLengthReader
/*    */ {
/*  9 */   private int nextByteToRead = 0;
/*    */ 
/* 11 */   private byte[] lenbytes = new byte[4];
/*    */ 
/*    */   public int getMessageLength(DataInputStream dis) throws IOException {
/* 14 */     int result = -1;
/*    */ 
/* 19 */     int lenReaded = this.nextByteToRead;
/* 20 */     for (int i = lenReaded; (i < 4) && 
/* 21 */       (dis.available() > 0); i++)
/*    */     {
/* 22 */       this.nextByteToRead = (i + 1);
/* 23 */       this.lenbytes[i] = dis.readByte();
/*    */     }
/*    */ 
/* 29 */     if (this.nextByteToRead >= 4) {
/* 30 */       DataInputStream is = new DataInputStream(new ByteArrayInputStream(this.lenbytes));
/* 31 */       result = is.readInt();
/*    */     }
/*    */ 
/* 35 */     return result;
/*    */   }
/*    */ 
/*    */   public void reset() {
/* 39 */     this.nextByteToRead = 0;
/* 40 */     this.lenbytes = new byte[4];
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.json.MessageLengthReader
 * JD-Core Version:    0.6.0
 */