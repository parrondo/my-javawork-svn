/*    */ package com.dukascopy.transport.common.protocol.json;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataInputStream;
/*    */ 
/*    */ public class TransportMessageFactory
/*    */ {
/*  8 */   private int remainingMessageBytes = 0;
/*    */ 
/* 10 */   private ByteArrayOutputStream messageOut = new ByteArrayOutputStream();
/*    */ 
/* 12 */   private MessageLengthReader mlr = new MessageLengthReader();
/*    */   private int maxMessageLength;
/*    */ 
/*    */   public TransportMessageFactory(int maxMessageLength)
/*    */   {
/* 18 */     this.maxMessageLength = maxMessageLength;
/*    */   }
/*    */ 
/*    */   public byte[] getProtocolBytes(DataInputStream dis, IoSessionState sessionState) throws Exception {
/* 22 */     int dataAvaliable = dis.available();
/*    */ 
/* 24 */     if (dataAvaliable < 1) {
/* 25 */       return null;
/*    */     }
/*    */ 
/* 28 */     if (this.remainingMessageBytes < 1) {
/* 29 */       sessionState.setState(2);
/* 30 */       int messageLength = this.mlr.getMessageLength(dis);
/* 31 */       if (messageLength < 1) {
/* 32 */         return null;
/*    */       }
/* 34 */       if (messageLength > this.maxMessageLength) {
/* 35 */         throw new Exception("Max message size reached: " + messageLength + " of MAX limit " + this.maxMessageLength);
/*    */       }
/* 37 */       this.mlr.reset();
/* 38 */       sessionState.setLastMessageLenght(messageLength);
/* 39 */       this.remainingMessageBytes = messageLength;
/* 40 */       sessionState.setState(3);
/* 41 */       dataAvaliable = dis.available();
/*    */     }
/*    */ 
/* 44 */     int bytesToRead = this.remainingMessageBytes;
/* 45 */     if (dataAvaliable < this.remainingMessageBytes) {
/* 46 */       this.remainingMessageBytes -= dataAvaliable;
/* 47 */       bytesToRead = dataAvaliable;
/*    */     } else {
/* 49 */       bytesToRead = this.remainingMessageBytes;
/* 50 */       this.remainingMessageBytes = 0;
/*    */     }
/* 52 */     sessionState.setState(3);
/* 53 */     byte[] readedBytes = new byte[bytesToRead];
/* 54 */     dis.read(readedBytes);
/* 55 */     this.messageOut.write(readedBytes);
/* 56 */     if (this.remainingMessageBytes < 1) {
/* 57 */       byte[] res = this.messageOut.toByteArray();
/* 58 */       this.messageOut = new ByteArrayOutputStream();
/* 59 */       this.mlr = new MessageLengthReader();
/* 60 */       sessionState.setState(1);
/* 61 */       return res;
/*    */     }
/* 63 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.json.TransportMessageFactory
 * JD-Core Version:    0.6.0
 */