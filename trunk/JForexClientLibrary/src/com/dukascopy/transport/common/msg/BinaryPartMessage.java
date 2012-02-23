/*    */ package com.dukascopy.transport.common.msg;
/*    */ 
/*    */ import com.dukascopy.transport.common.mina.Base64Encoder;
/*    */ 
/*    */ public class BinaryPartMessage extends ProtocolMessage
/*    */ {
/*    */   public static final String TYPE = "bpm";
/*    */ 
/*    */   public BinaryPartMessage()
/*    */   {
/* 13 */     setType("bpm");
/*    */   }
/*    */ 
/*    */   public BinaryPartMessage(ProtocolMessage message)
/*    */   {
/* 20 */     super(message);
/* 21 */     setType("bpm");
/* 22 */     put("data", message.getString("data"));
/* 23 */     put("streamId", message.getString("streamId"));
/* 24 */     put("eof", message.getBoolean("eof"));
/*    */   }
/*    */ 
/*    */   public BinaryPartMessage(String streamId, byte[] data)
/*    */   {
/* 31 */     setType("bpm");
/* 32 */     setData(data);
/* 33 */     setStreamId(streamId);
/*    */   }
/*    */ 
/*    */   public String getStreamId() {
/* 37 */     return getString("streamId");
/*    */   }
/*    */ 
/*    */   public void setStreamId(String streamId) {
/* 41 */     put("streamId", streamId);
/*    */   }
/*    */ 
/*    */   public byte[] getData()
/*    */   {
/* 46 */     byte[] b = new byte[0];
/* 47 */     String str = getString("data");
/* 48 */     if (str != null) {
/* 49 */       b = Base64Encoder.decode(str);
/* 50 */       return b;
/*    */     }
/* 52 */     return b;
/*    */   }
/*    */ 
/*    */   public void setData(byte[] data) {
/* 56 */     if (data != null)
/* 57 */       put("data", new String(Base64Encoder.encode(data)));
/*    */   }
/*    */ 
/*    */   public void setEOF(boolean eof)
/*    */   {
/* 62 */     put("eof", eof);
/*    */   }
/*    */ 
/*    */   public boolean isEOF() {
/* 66 */     return getBoolean("eof");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.BinaryPartMessage
 * JD-Core Version:    0.6.0
 */